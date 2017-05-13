package protect.rentalcalc;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.google.common.io.ByteStreams;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Collection of methods to help testing data in the database
 */
class DatabaseTestHelper
{
    // Value of the first ID used in a database
    static final long FIRST_ID = 1;

    static void setPropertyValues(Property property, int index)
    {
        String indexStr = String.format(Locale.US, "%4d", index);
        boolean boolValues = (index % 2) == 1;

        property.nickname = "nickname" + indexStr;
        property.addressStreet = "street" + indexStr;
        property.addressCity = "city" + indexStr;
        property.addressState = "state" + indexStr;
        property.addressZip = "zip" + indexStr;
        property.propertyType = PropertyType.HOUSE.ordinal();
        property.propertyBeds = "3";
        property.propertyBaths = "3.5";
        property.propertySqft = index;
        property.propertyLot = index;
        property.propertyYear = index;
        property.propertyParking = ParkingType.GARAGE.ordinal();
        property.propertyZoning = "zoning" + indexStr;
        property.propertyMls = "mls" + indexStr;
        property.purchasePrice = index;
        property.afterRepairsValue = index;
        property.useLoan = boolValues;
        property.downPayment = index;
        property.interestRate = (double)index + 0.1;
        property.loanDuration = index;
        property.purchaseCosts = index;
        property.repairRemodelCosts = index;
        property.grossRent = index;
        property.otherIncome = index;
        property.expenses = index;
        property.vacancy = index;
        property.appreciation = index;
        property.incomeIncrease = index;
        property.expenseIncrease = index;
        property.incomeTaxRate = index;
        property.sellingCosts = index;
        property.landValue = index;
        property.notes = "notes" + indexStr;
    }

    /**
     * Add the given number of properties, each with
     * an index in the nickname and other fields.
     */
    static void addProperties(DBHelper db, int propertiesToAdd)
    {
        // Add in reverse order to test sorting
        for(int index = propertiesToAdd; index > 0; index--)
        {
            Property property = new Property();
            setPropertyValues(property, index);
            long id = db.insertProperty(property);
            assertTrue(id >= 0);
        }

        assertEquals(propertiesToAdd, db.getPropertyCount());
    }

    /**
     * Check that the passed property follows the pattern specified in
     * addProperties(), given the index.
     */
    static void checkProperty(Property property, int index)
    {
        String indexStr = String.format(Locale.US, "%4d", index);
        boolean boolValues = (index % 2) == 1;

        assertEquals("nickname" + indexStr, property.nickname);
        assertEquals("street" + indexStr, property.addressStreet);
        assertEquals("city" + indexStr, property.addressCity);
        assertEquals("state" + indexStr, property.addressState);
        assertEquals("zip" + indexStr, property.addressZip);
        assertEquals(PropertyType.HOUSE.ordinal(), property.propertyType);
        assertEquals("3", property.propertyBeds);
        assertEquals("3.5", property.propertyBaths);
        assertEquals(index, property.propertySqft);
        assertEquals(index, property.propertyLot);
        assertEquals(index, property.propertyYear);
        assertEquals(ParkingType.GARAGE.ordinal(), property.propertyParking);
        assertEquals("zoning" + indexStr, property.propertyZoning);
        assertEquals("mls" + indexStr, property.propertyMls);
        assertEquals(index, property.purchasePrice);
        assertEquals(index, property.afterRepairsValue);
        assertEquals(boolValues, property.useLoan);
        assertEquals(index, property.downPayment);
        assertEquals((double)index + 0.1, property.interestRate, 0.1);
        assertEquals(index, property.loanDuration);
        assertEquals(index, property.purchaseCosts);
        assertEquals(index, property.repairRemodelCosts);
        assertEquals(index, property.grossRent);
        assertEquals(index, property.otherIncome);
        assertEquals(index, property.expenses);
        assertEquals(index, property.vacancy);
        assertEquals(index, property.appreciation);
        assertEquals(index, property.incomeIncrease);
        assertEquals(index, property.expenseIncrease);
        assertEquals(index, property.incomeTaxRate);
        assertEquals(index, property.sellingCosts);
        assertEquals(index, property.landValue);
        assertEquals("notes" + indexStr, property.notes);
    }

    /**
     * Check that the expected number of properties exist and that
     * all of the properties follow the pattern specified in addProperties(),
     * and are in sequential order where the smallest property is based on
     * 1.
     */
    static void checkProperties(DBHelper db, int expectedCount)
    {
        assertEquals(expectedCount, db.getPropertyCount());

        Cursor cursor = db.getProperties();
        int index = 1;

        while(cursor.moveToNext())
        {
            Property property = Property.toProperty(cursor);
            checkProperty(property, index);
            index++;
        }
    }

    /**
     * Delete the contents of the budgets and transactions databases
     */
    static void clearDatabase(DBHelper db, Context context)
    {
        SQLiteDatabase database = db.getWritableDatabase();
        database.execSQL("delete from " + DBHelper.PropertyDbIds.TABLE);
        database.close();

        assertEquals(0, db.getPropertyCount());
    }
}
