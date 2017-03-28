package protect.rentalcalc;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class DatabaseTest
{
    private DBHelper db;

    @Before
    public void setUp()
    {
        // Output logs emitted during tests so they may be accessed
        ShadowLog.stream = System.out;

        Activity activity = Robolectric.setupActivity(PropertiesListActivity.class);
        db = new DBHelper(activity);
    }

    @After
    public void tearDown()
    {
        db.close();
    }

    @Test
    public void addRemoveOneProperty()
    {
        Property property = db.getProperty(DatabaseTestHelper.FIRST_ID);
        assertNull(property);

        assertEquals(db.getPropertyCount(), 0);
        DatabaseTestHelper.addProperties(db, 1);

        DatabaseTestHelper.checkProperties(db, 1);

        property = db.getProperty(DatabaseTestHelper.FIRST_ID);
        assertNotNull(property);
        DatabaseTestHelper.checkProperty(property, 1);

        db.deleteProperty(property.id);
        assertEquals(db.getPropertyCount(), 0);
        property = db.getProperty(DatabaseTestHelper.FIRST_ID);
        assertNull(property);
    }

    @Test
    public void multipleProperties()
    {
        final int NUM_PROPERTIES = 10;

        DatabaseTestHelper.addProperties(db, NUM_PROPERTIES);
        DatabaseTestHelper.checkProperties(db, NUM_PROPERTIES);

        int remainingProperties = NUM_PROPERTIES;
        Cursor cursor = db.getProperties();
        while(cursor.moveToNext())
        {
            Property property = Property.toProperty(cursor);
            boolean result = db.deleteProperty(property.id);
            assertTrue(result);
            remainingProperties--;
            assertEquals(remainingProperties, db.getPropertyCount());
        }

        assertEquals(0, db.getPropertyCount());
    }


    @Test
    public void updateProperty()
    {
        DatabaseTestHelper.addProperties(db, 1);
        Property property = db.getProperty(DatabaseTestHelper.FIRST_ID);
        assertNotNull(property);

        final int TEST_VALUE = 1234;
        DatabaseTestHelper.setPropertyValues(property, TEST_VALUE);

        boolean result = db.updateProperty(property);
        assertTrue(result);
        property = db.getProperty(DatabaseTestHelper.FIRST_ID);
        assertNotNull(property);

        DatabaseTestHelper.checkProperty(property, TEST_VALUE);
    }


    @Test
    public void updateMissingProperty()
    {
        Property property = new Property();
        property.id = DatabaseTestHelper.FIRST_ID;
        boolean result = db.updateProperty(property);
        assertEquals(false, result);
    }

    @Test
    public void addDefaultProperty()
    {
        Property property = new Property();
        long id = db.insertProperty(property);
        assertEquals(DatabaseTestHelper.FIRST_ID, id);
    }
}