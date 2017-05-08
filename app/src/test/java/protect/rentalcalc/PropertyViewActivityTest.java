package protect.rentalcalc;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.ShadowToast;

import java.util.Locale;

import static org.junit.Assert.assertNotNull;
import static protect.rentalcalc.TestHelper.checkIntField;
import static protect.rentalcalc.TestHelper.preloadProperty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class PropertyViewActivityTest
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
        ActivityController controller = Robolectric.buildActivity(PropertyViewActivity.class).create();
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
    public void startAsAddCheckActionBar() throws Exception
    {
        ActivityController controller = Robolectric.buildActivity(PropertyViewActivity.class).create();
        Activity activity = (Activity)controller.get();

        controller.start();
        controller.visible();
        controller.resume();

        final Menu menu = shadowOf(activity).getOptionsMenu();
        assertNotNull(menu);

        assertEquals(menu.size(), 1);

        MenuItem item = menu.findItem(R.id.action_save);
        assertNotNull(item);
        assertEquals("Save", item.getTitle().toString());
    }

    @Test
    public void startWithPropertyCheckActionBar() throws Exception
    {
        ActivityController controller = startWithProperty(new Property());
        Activity activity = (Activity)controller.get();

        final Menu menu = shadowOf(activity).getOptionsMenu();
        assertNotNull(menu);

        assertEquals(menu.size(), 1);

        MenuItem item = menu.findItem(R.id.action_save);
        assertNotNull(item);
        assertEquals("Save", item.getTitle().toString());
    }

    private void checkFields(Activity activity, Property property)
    {
        EditText nicknameField = (EditText)activity.findViewById(R.id.nickname);
        EditText streetField = (EditText)activity.findViewById(R.id.street);
        EditText cityField = (EditText)activity.findViewById(R.id.city);
        EditText stateField = (EditText)activity.findViewById(R.id.state);
        EditText zipField = (EditText)activity.findViewById(R.id.zip);
        Spinner typeSpinner = (Spinner)activity.findViewById(R.id.type);
        Spinner bedsSpinner = (Spinner)activity.findViewById(R.id.beds);
        Spinner bathsSpinner = (Spinner)activity.findViewById(R.id.baths);
        EditText sqftField = (EditText)activity.findViewById(R.id.sqft);
        EditText lotField = (EditText)activity.findViewById(R.id.lot);
        EditText yearField = (EditText)activity.findViewById(R.id.year);
        Spinner parkingSpinner = (Spinner)activity.findViewById(R.id.parking);
        EditText zoningField = (EditText)activity.findViewById(R.id.zoning);
        EditText mlsField = (EditText)activity.findViewById(R.id.mls);

        assertEquals(property.nickname, nicknameField.getText().toString());
        assertEquals(property.addressStreet, streetField.getText().toString());
        assertEquals(property.addressCity, cityField.getText().toString());
        assertEquals(property.addressState, stateField.getText().toString());
        assertEquals(property.addressZip, zipField.getText().toString());

        checkIntField(property.propertySqft, sqftField.getText().toString());
        checkIntField(property.propertyLot, lotField.getText().toString());
        checkIntField(property.propertyYear, yearField.getText().toString());

        assertEquals(property.propertyZoning, zoningField.getText().toString());
        assertEquals(property.propertyMls, mlsField.getText().toString());

        PropertyType type = PropertyType.fromString(RuntimeEnvironment.application, typeSpinner.getSelectedItem().toString());
        assertEquals(property.propertyType, type.ordinal());

        ParkingType parking = ParkingType.fromString(RuntimeEnvironment.application, parkingSpinner.getSelectedItem().toString());
        assertEquals(property.propertyParking, parking.ordinal());

        assertEquals(property.propertyBeds, bedsSpinner.getSelectedItem().toString());
        assertEquals(property.propertyBaths, bathsSpinner.getSelectedItem().toString());
    }

    private void setFields(Activity activity, Property property)
    {
        EditText nicknameField = (EditText)activity.findViewById(R.id.nickname);
        EditText streetField = (EditText)activity.findViewById(R.id.street);
        EditText cityField = (EditText)activity.findViewById(R.id.city);
        EditText stateField = (EditText)activity.findViewById(R.id.state);
        EditText zipField = (EditText)activity.findViewById(R.id.zip);
        Spinner typeSpinner = (Spinner)activity.findViewById(R.id.type);
        Spinner bedsSpinner = (Spinner)activity.findViewById(R.id.beds);
        Spinner bathsSpinner = (Spinner)activity.findViewById(R.id.baths);
        EditText sqftField = (EditText)activity.findViewById(R.id.sqft);
        EditText lotField = (EditText)activity.findViewById(R.id.lot);
        EditText yearField = (EditText)activity.findViewById(R.id.year);
        Spinner parkingSpinner = (Spinner)activity.findViewById(R.id.parking);
        EditText zoningField = (EditText)activity.findViewById(R.id.zoning);
        EditText mlsField = (EditText)activity.findViewById(R.id.mls);

        nicknameField.setText(property.nickname);
        streetField.setText(property.addressStreet);
        cityField.setText(property.addressCity);
        stateField.setText(property.addressState);
        zipField.setText(property.addressZip);
        sqftField.setText(String.format(Locale.US, "%d", property.propertySqft));
        lotField.setText(String.format(Locale.US, "%d", property.propertyLot));
        yearField.setText(String.format(Locale.US, "%d", property.propertyYear));
        zoningField.setText(property.propertyZoning);
        mlsField.setText(property.propertyMls);

        typeSpinner.setSelection(property.propertyType);
        parkingSpinner.setSelection(property.propertyParking);

        int beds = Integer.parseInt(property.propertyBeds);
        bedsSpinner.setSelection(beds-1);

        int baths = Integer.parseInt(property.propertyBaths.replace(".5", ""));
        baths -= 1;
        baths *= 2;
        if(property.propertyBaths.contains(".5"))
        {
            baths += 1;
        }
        bathsSpinner.setSelection(baths);
    }

    @Test
    public void startWithoutBundle()
    {
        ActivityController controller = Robolectric.buildActivity(PropertyViewActivity.class).create();
        Activity activity = (Activity)controller.get();

        checkFields(activity, new Property());
    }

    @Test
    public void startWithoutProperty()
    {
        Intent intent = new Intent();
        final Bundle bundle = new Bundle();
        intent.putExtras(bundle);

        ActivityController controller = Robolectric.buildActivity(PropertyViewActivity.class, intent).create();
        Activity activity = (Activity)controller.get();

        checkFields(activity, new Property());
    }

    @Test
    public void startWithoutPropertyAndSave() throws IllegalAccessException
    {
        Intent intent = new Intent();
        final Bundle bundle = new Bundle();
        intent.putExtras(bundle);

        ActivityController controller = Robolectric.buildActivity(PropertyViewActivity.class, intent).create();
        Activity activity = (Activity)controller.get();
        controller.start();
        controller.visible();
        controller.resume();

        Property property = new Property();
        preloadProperty(property);
        setFields(activity, property);

        assertNotNull(shadowOf(activity).getOptionsMenu().findItem(R.id.action_save));
        shadowOf(activity).clickMenuItem(R.id.action_save);
        assertTrue(activity.isFinishing());

        Intent next = shadowOf(activity).getNextStartedActivity();
        ComponentName componentName = next.getComponent();
        String name = componentName.flattenToShortString();
        assertEquals("protect.rentalcalc/.PropertyOverviewActivity", name);
        Bundle nextBundle = next.getExtras();
        assertNotNull(nextBundle);
        assertTrue(nextBundle.containsKey("id"));
        assertEquals(DatabaseTestHelper.FIRST_ID, nextBundle.getLong("id"));

        Property updatedProperty = db.getProperty(DatabaseTestHelper.FIRST_ID);

        compareRelevantPropertyFields(property, updatedProperty);
    }

    @Test
    public void startWithMissingProperty()
    {
        Intent intent = new Intent();
        final Bundle bundle = new Bundle();
        bundle.putLong("id", 0);
        intent.putExtras(bundle);

        ActivityController controller = Robolectric.buildActivity(PropertyViewActivity.class, intent).create();
        Activity activity = (Activity)controller.get();

        checkFields(activity, new Property());
    }

    private ActivityController startWithProperty(Property property)
    {
        long id = db.insertProperty(property);

        Intent intent = new Intent();
        final Bundle bundle = new Bundle();
        bundle.putLong("id", id);
        intent.putExtras(bundle);

        ActivityController controller = Robolectric.buildActivity(PropertyViewActivity.class, intent).create();
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
    public void startWithPropertyCheckInitialLoad() throws Exception
    {
        Property property = new Property();
        preloadProperty(property);

        ActivityController controller = startWithProperty(property);
        Activity activity = (Activity)controller.get();

        checkFields(activity, property);
    }

    @Test
    public void startWithPropertyChangeValues() throws Exception
    {
        Property property = new Property();
        preloadProperty(property);

        ActivityController controller = startWithProperty(property);
        Activity activity = (Activity)controller.get();

        checkFields(activity, property);

        property = new Property();
        setFields(activity, property);

        // Check that the values are still valid after a pause and resume.
        controller.pause();
        controller.resume();

        checkFields(activity, property);
    }

    private void compareRelevantPropertyFields(Property a, Property b)
    {
        assertEquals(a.nickname, b.nickname);
        assertEquals(a.addressStreet, b.addressStreet);
        assertEquals(a.addressCity, b.addressCity);
        assertEquals(a.addressState, b.addressState);
        assertEquals(a.addressZip, b.addressZip);
        assertEquals(a.propertySqft, b.propertySqft);
        assertEquals(a.propertyLot, b.propertyLot);
        assertEquals(a.propertyYear, b.propertyYear);
        assertEquals(a.propertyZoning, b.propertyZoning);
        assertEquals(a.propertyMls, b.propertyMls);
        assertEquals(a.propertyType, b.propertyType);
        assertEquals(a.propertyParking, b.propertyParking);
        assertEquals(a.propertyBeds, b.propertyBeds);
    }

    @Test
    public void startWithPropertyAndSave() throws Exception
    {
        Property property = new Property();

        ActivityController controller = startWithProperty(property);
        Activity activity = (Activity)controller.get();

        checkFields(activity, property);

        preloadProperty(property);
        setFields(activity, property);
        checkFields(activity, property);

        assertNotNull(shadowOf(activity).getOptionsMenu().findItem(R.id.action_save));
        shadowOf(activity).clickMenuItem(R.id.action_save);
        assertTrue(activity.isFinishing());

        Intent next = shadowOf(activity).getNextStartedActivity();
        assertNull(next);

        Property updatedProperty = db.getProperty(DatabaseTestHelper.FIRST_ID);

        compareRelevantPropertyFields(property, updatedProperty);
    }
}
