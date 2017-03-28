package protect.rentalcalc;

import android.app.Activity;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class PropertyCursorAdapterTest
{
    private Activity activity;
    private DBHelper db;

    @Before
    public void setUp()
    {
        // Output logs emitted during tests so they may be accessed
        ShadowLog.stream = System.out;

        activity = Robolectric.setupActivity(PropertiesListActivity.class);
        db = new DBHelper(activity);
    }

    @After
    public void tearDown()
    {
        db.close();
    }

    @Test
    public void checkProperty()
    {
        for(String city : new String[]{"", "city"})
        {
            for(String state : new String[]{"", "state"})
            {
                for(Integer price : new Integer[]{0, 123456, 999999999})
                {
                    System.out.println("City: " + city);
                    System.out.println("State: " + state);
                    System.out.println("Price: " + price);

                    Property property = new Property();
                    property.nickname = "nickname";
                    property.purchasePrice = price;
                    property.addressCity = city;
                    property.addressState = state;

                    long id = db.insertProperty(property);
                    assertTrue(id >= DatabaseTestHelper.FIRST_ID);

                    Cursor cursor = db.getProperties();
                    cursor.moveToFirst();

                    PropertyCursorAdapter adapter = new PropertyCursorAdapter(activity, cursor);
                    View view = adapter.newView(activity, cursor, null);
                    adapter.bindView(view, activity, cursor);
                    cursor.close();

                    boolean result = db.deleteProperty(id);
                    assertTrue(result);

                    TextView nicknameField = (TextView) view.findViewById(R.id.nickname);
                    assertEquals(property.nickname, nicknameField.getText().toString());

                    TextView addressField = (TextView) view.findViewById(R.id.shortAddress);
                    String expectedAddress;
                    if(city.isEmpty())
                    {
                        if(state.isEmpty())
                        {
                            expectedAddress = "";
                        }
                        else
                        {
                            expectedAddress = "state";
                        }
                    }
                    else
                    {
                        if(state.isEmpty())
                        {
                            expectedAddress = "city";
                        }
                        else
                        {
                            expectedAddress = "city, state";
                        }
                    }

                    assertEquals(expectedAddress, addressField.getText().toString());

                    TextView priceField = (TextView) view.findViewById(R.id.price);
                    String expectedPrice = String.format(Locale.US, "%dK", property.purchasePrice/1000);
                    assertEquals(expectedPrice, priceField.getText().toString());
                }
            }
        }
    }
}
