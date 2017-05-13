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

    private void setupDatabaseVersion1(SQLiteDatabase database)
    {
        // Delete the tables as they exist now
        database.execSQL("drop table " + DBHelper.PropertyDbIds.TABLE);

        // Create the table as it existed in revision 1
        // create table for properties
        database.execSQL("create table " + DBHelper.PropertyDbIds.TABLE + "(" +
                DBHelper.PropertyDbIds.ID + " INTEGER primary key autoincrement," +
                DBHelper.PropertyDbIds.NICKNAME + " TEXT," +
                DBHelper.PropertyDbIds.ADDRESS_STREET + " TEXT," +
                DBHelper.PropertyDbIds.ADDRESS_CITY + " TEXT," +
                DBHelper.PropertyDbIds.ADDRESS_STATE + " TEXT," +
                DBHelper.PropertyDbIds.ADDRESS_ZIP + " TEXT," +
                DBHelper.PropertyDbIds.PROPERTY_TYPE + " INTEGER," +
                DBHelper.PropertyDbIds.PROPERTY_BEDS + " TEXT," +
                DBHelper.PropertyDbIds.PROPERTY_BATHS + " TEXT," +
                DBHelper.PropertyDbIds.PROPERTY_SQUARE_FOOTAGE + " INTEGER," +
                DBHelper.PropertyDbIds.PROPERTY_LOT + " INTEGER," +
                DBHelper.PropertyDbIds.PROPERTY_YEAR + " INTEGER," +
                DBHelper.PropertyDbIds.PROPERTY_PARKING + " INTEGER," +
                DBHelper.PropertyDbIds.PROPERTY_ZONING + " INTEGER," +
                DBHelper.PropertyDbIds.PROPERTY_MLS + " TEXT," +
                DBHelper.PropertyDbIds.PURCHASE_PRICE + " INTEGER," +
                DBHelper.PropertyDbIds.AFTER_REPAIRS_VALUE + " INTEGER," +
                DBHelper.PropertyDbIds.USE_LOAN + " INTEGER," +
                DBHelper.PropertyDbIds.DOWN_PAYMENT + " INTEGER," +
                DBHelper.PropertyDbIds.INTEREST_RATE + " REAL," +
                DBHelper.PropertyDbIds.LOAN_DURATION + " INTEGER," +
                DBHelper.PropertyDbIds.PURCHASE_COSTS + " INTEGER," +
                DBHelper.PropertyDbIds.REPAIR_REMODEL_COSTS + " INTEGER," +
                DBHelper.PropertyDbIds.GROSS_RENT + " INTEGER," +
                DBHelper.PropertyDbIds.OTHER_INCOME + " INTEGER," +
                DBHelper.PropertyDbIds.EXPENSES + " INTEGER," +
                DBHelper.PropertyDbIds.VACANCY + " INTEGER," +
                DBHelper.PropertyDbIds.APPRECIATION + " INTEGER," +
                DBHelper.PropertyDbIds.INCOME_INCREASE + " INTEGER," +
                DBHelper.PropertyDbIds.EXPENSE_INCREASE + " INTEGER," +
                DBHelper.PropertyDbIds.SELLING_COSTS + " INTEGER," +
                DBHelper.PropertyDbIds.LAND_VALUE + " INTEGER," +
                DBHelper.PropertyDbIds.NOTES + " TEXT)");
    }

    private void insertPropertyVersion1(SQLiteDatabase database, Property property)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.PropertyDbIds.NICKNAME, property.nickname);
        contentValues.put(DBHelper.PropertyDbIds.ADDRESS_STREET, property.addressStreet);
        contentValues.put(DBHelper.PropertyDbIds.ADDRESS_CITY, property.addressCity);
        contentValues.put(DBHelper.PropertyDbIds.ADDRESS_STATE, property.addressState);
        contentValues.put(DBHelper.PropertyDbIds.ADDRESS_ZIP, property.addressZip);
        contentValues.put(DBHelper.PropertyDbIds.PROPERTY_TYPE, property.propertyType);
        contentValues.put(DBHelper.PropertyDbIds.PROPERTY_BEDS, property.propertyBeds);
        contentValues.put(DBHelper.PropertyDbIds.PROPERTY_BATHS, property.propertyBaths);
        contentValues.put(DBHelper.PropertyDbIds.PROPERTY_SQUARE_FOOTAGE, property.propertySqft);
        contentValues.put(DBHelper.PropertyDbIds.PROPERTY_LOT, property.propertyLot);
        contentValues.put(DBHelper.PropertyDbIds.PROPERTY_YEAR, property.propertyYear);
        contentValues.put(DBHelper.PropertyDbIds.PROPERTY_PARKING, property.propertyParking);
        contentValues.put(DBHelper.PropertyDbIds.PROPERTY_ZONING, property.propertyZoning);
        contentValues.put(DBHelper.PropertyDbIds.PROPERTY_MLS, property.propertyMls);
        contentValues.put(DBHelper.PropertyDbIds.PURCHASE_PRICE, property.purchasePrice);
        contentValues.put(DBHelper.PropertyDbIds.AFTER_REPAIRS_VALUE, property.afterRepairsValue);
        contentValues.put(DBHelper.PropertyDbIds.USE_LOAN, property.useLoan);
        contentValues.put(DBHelper.PropertyDbIds.DOWN_PAYMENT, property.downPayment);
        contentValues.put(DBHelper.PropertyDbIds.INTEREST_RATE, property.interestRate);
        contentValues.put(DBHelper.PropertyDbIds.LOAN_DURATION, property.loanDuration);
        contentValues.put(DBHelper.PropertyDbIds.PURCHASE_COSTS, property.purchaseCosts);
        contentValues.put(DBHelper.PropertyDbIds.REPAIR_REMODEL_COSTS, property.repairRemodelCosts);
        contentValues.put(DBHelper.PropertyDbIds.GROSS_RENT, property.grossRent);
        contentValues.put(DBHelper.PropertyDbIds.OTHER_INCOME, property.otherIncome);
        contentValues.put(DBHelper.PropertyDbIds.EXPENSES, property.expenses);
        contentValues.put(DBHelper.PropertyDbIds.VACANCY, property.vacancy);
        contentValues.put(DBHelper.PropertyDbIds.APPRECIATION, property.appreciation);
        contentValues.put(DBHelper.PropertyDbIds.INCOME_INCREASE, property.incomeIncrease);
        contentValues.put(DBHelper.PropertyDbIds.EXPENSE_INCREASE, property.expenseIncrease);
        contentValues.put(DBHelper.PropertyDbIds.SELLING_COSTS, property.sellingCosts);
        contentValues.put(DBHelper.PropertyDbIds.LAND_VALUE, property.landValue);
        contentValues.put(DBHelper.PropertyDbIds.NOTES, property.notes);

        long newId = database.insert(DBHelper.PropertyDbIds.TABLE, null, contentValues);
        assertEquals(DatabaseTestHelper.FIRST_ID, newId);
    }

    @Test
    public void databaseUpgradeFromVersion1()
    {
        SQLiteDatabase database = db.getWritableDatabase();

        // Setup the database as it appeared in revision 1
        setupDatabaseVersion1(database);

        // Insert a property
        final int TEST_VALUE = 10;
        Property property = new Property();
        DatabaseTestHelper.setPropertyValues(property, TEST_VALUE);
        insertPropertyVersion1(database, property);

        // Upgrade database
        db.onUpgrade(database, DBHelper.ORIGINAL_DATABASE_VERSION, DBHelper.DATABASE_VERSION);

        // Determine that the entries are queryable and the fields are correct
        Cursor cursor = db.getProperties();
        cursor.moveToFirst();
        Property newProperty = Property.toProperty(cursor);

        // The default income tax rate in the database is 0%
        assertEquals(0, newProperty.incomeTaxRate);

        // Now check the remaining fields are still the same
        newProperty.incomeTaxRate = property.incomeTaxRate;
        DatabaseTestHelper.checkProperty(newProperty, TEST_VALUE);

        database.close();
    }
}