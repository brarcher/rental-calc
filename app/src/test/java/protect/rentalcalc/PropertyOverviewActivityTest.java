package protect.rentalcalc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.ShadowToast;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class PropertyOverviewActivityTest
{
    private DBHelper db;

    @Before
    public void setUp()
    {
        // Output logs emitted during tests so they may be accessed
        ShadowLog.stream = System.out;
        db = new DBHelper(RuntimeEnvironment.application);
    }

    @After
    public void tearDown()
    {
        db.close();
    }

    @Test
    public void clickBackFinishes()
    {
        ActivityController controller = startWithProperty(new Property());
        Activity activity = (Activity)controller.get();

        controller.start();
        controller.visible();
        controller.resume();

        assertTrue(shadowOf(activity).isFinishing() == false);
        shadowOf(activity).clickMenuItem(android.R.id.home);
        assertTrue(shadowOf(activity).isFinishing());

        // Check that can finish out the lifecycle
        controller.pause();
        controller.stop();
        controller.destroy();
    }

    @Test
    public void startWithoutBundle()
    {
        ActivityController controller = Robolectric.buildActivity(PropertyOverviewActivity.class).create();
        Activity activity = (Activity)controller.get();

        controller.start();
        controller.visible();
        controller.resume();
        assertTrue(activity.isFinishing());

        String latestToast = ShadowToast.getTextOfLatestToast();
        assertNotNull(latestToast);
    }

    @Test
    public void startWithoutProperty()
    {
        Intent intent = new Intent();
        final Bundle bundle = new Bundle();
        intent.putExtras(bundle);

        ActivityController controller = Robolectric.buildActivity(PropertyOverviewActivity.class, intent).create();
        Activity activity = (Activity)controller.get();

        controller.start();
        controller.visible();
        controller.resume();
        assertTrue(activity.isFinishing());

        String latestToast = ShadowToast.getTextOfLatestToast();
        assertNotNull(latestToast);
    }

    @Test
    public void startWithMissingProperty()
    {
        Intent intent = new Intent();
        final Bundle bundle = new Bundle();
        bundle.putLong("id", 0);
        intent.putExtras(bundle);

        ActivityController controller = Robolectric.buildActivity(PropertyOverviewActivity.class, intent).create();
        Activity activity = (Activity)controller.get();

        controller.start();
        controller.visible();
        controller.resume();
        assertTrue(activity.isFinishing());

        String latestToast = ShadowToast.getTextOfLatestToast();
        assertNotNull(latestToast);
    }

    private ActivityController startWithProperty(Property property)
    {
        long id = db.insertProperty(property);

        Intent intent = new Intent();
        final Bundle bundle = new Bundle();
        bundle.putLong("id", id);
        intent.putExtras(bundle);

        ActivityController controller = Robolectric.buildActivity(PropertyOverviewActivity.class, intent).create();
        Activity activity = (Activity)controller.get();

        controller.start();
        controller.visible();
        controller.resume();
        assertTrue(activity.isFinishing() == false);

        String latestToast = ShadowToast.getTextOfLatestToast();
        assertNull(latestToast);

        return controller;
    }

    @Test
    public void clickDelete()
    {
        ActivityController controller = startWithProperty(new Property());
        Activity activity = (Activity)controller.get();

        shadowOf(activity).clickMenuItem(R.id.action_delete);

        // AppCompatDialog does not have a shadow due
        // https://github.com/robolectric/robolectric/issues/2232
        // Continue test after that is resolved
    }


    @Test
    public void startSummaryText()
    {
        ActivityController controller = startWithProperty(new Property());
        Activity activity = (Activity)controller.get();

        Property property = db.getProperty(DatabaseTestHelper.FIRST_ID);

        for(String city : new String[]{"", "city"})
        {
            for(String state : new String[]{"", "state"})
            {
                for(String zip : new String[]{"", "zip"})
                {
                    for(Integer price : new Integer[]{0, 123456, 999999999})
                    {
                        property.nickname = "nickname";
                        property.addressStreet = "street";
                        property.addressCity = city;
                        property.addressState = state;
                        property.addressZip = zip;
                        property.purchasePrice = price;

                        System.out.println("Nickname: " + property.nickname);
                        System.out.println("Street: " + property.addressStreet);
                        System.out.println("City: " + property.addressCity);
                        System.out.println("State: " + property.addressState);
                        System.out.println("Zip: " + property.addressZip);
                        System.out.println("Price: " + property.purchasePrice);

                        boolean result = db.updateProperty(property);
                        assertTrue(result);

                        controller.pause();
                        controller.resume();

                        TextView nickname = (TextView)activity.findViewById(R.id.nickname);
                        assertEquals(property.nickname, nickname.getText().toString());

                        TextView street = (TextView)activity.findViewById(R.id.street);
                        assertEquals(property.addressStreet, street.getText().toString());

                        TextView priceField = (TextView) activity.findViewById(R.id.price);
                        String expectedPrice = String.format(Locale.US, "%dK", property.purchasePrice/1000);
                        assertEquals(expectedPrice, priceField.getText().toString());

                        TextView stateZip = (TextView) activity.findViewById(R.id.stateZip);
                        String expectedstateZip;

                        if(property.addressCity.isEmpty())
                        {
                            if(property.addressState.isEmpty())
                            {
                                if(property.addressZip.isEmpty())
                                {
                                    expectedstateZip = "";
                                }
                                else
                                {
                                    expectedstateZip = "zip";
                                }
                            }
                            else
                            {
                                if(property.addressZip.isEmpty())
                                {
                                    expectedstateZip = "state";
                                }
                                else
                                {
                                    expectedstateZip = "state zip";
                                }
                            }
                        }
                        else
                        {
                            if(property.addressState.isEmpty())
                            {
                                if(property.addressZip.isEmpty())
                                {
                                    expectedstateZip = "city";
                                }
                                else
                                {
                                    expectedstateZip = "city, zip";
                                }
                            }
                            else
                            {
                                if(property.addressZip.isEmpty())
                                {
                                    expectedstateZip = "city, state";
                                }
                                else
                                {
                                    expectedstateZip = "city, state zip";
                                }
                            }
                        }

                        assertEquals(expectedstateZip, stateZip.getText().toString());
                    }
                }
            }
        }
    }

    private void checkOpenActivity(int viewId, String nextActivity)
    {
        ActivityController controller = startWithProperty(new Property());
        Activity activity = (Activity)controller.get();

        View propertyView = activity.findViewById(viewId);
        propertyView.performClick();

        assertTrue(activity.isFinishing() == false);
        Intent next = shadowOf(activity).getNextStartedActivity();

        ComponentName componentName = next.getComponent();
        String name = componentName.flattenToShortString();
        assertEquals(nextActivity, name);

        Bundle extras = next.getExtras();
        assertNotNull(extras);
        assertTrue(extras.containsKey("id"));
        long id = extras.getLong("id", -1);
        assertEquals(DatabaseTestHelper.FIRST_ID, id);
    }

    @Test
    public void openPropertyView()
    {
        checkOpenActivity(R.id.propertyView, "protect.rentalcalc/.PropertyViewActivity");
    }

    @Test
    public void openPropertyWorksheet()
    {
        checkOpenActivity(R.id.propertyWorksheet, "protect.rentalcalc/.PropertyWorksheetActivity");
    }

    @Test
    public void openPropertyNotes()
    {
        checkOpenActivity(R.id.propertyNotes, "protect.rentalcalc/.PropertyNotesActivity");
    }
}
