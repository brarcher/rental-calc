package protect.rentalcalc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Helper class for managing data in the database
 */
class DBHelper extends SQLiteOpenHelper
{
    private static final String TAG = "RentalCalc";

    private static final String DATABASE_NAME = "RentalCalc.db";

    private static final int ORIGINAL_DATABASE_VERSION = 1;
    private static final int DATABASE_VERSION = 1;

    /**
     * All strings used with the budget table
     */
    static class PropertyDbIds
    {
        static final String TABLE = "properties";
        static final String ID = "_id";

        static final String NICKNAME = "nickname";
        static final String ADDRESS_STREET = "addressStreet";
        static final String ADDRESS_CITY = "addressCity";
        static final String ADDRESS_STATE = "addressState";
        static final String ADDRESS_ZIP = "addressZip";
        static final String PROPERTY_TYPE = "propertyType";
        static final String PROPERTY_BEDS = "propertyBeds";
        static final String PROPERTY_BATHS = "propertyBaths";
        static final String PROPERTY_SQUARE_FOOTAGE = "propertySqft";
        static final String PROPERTY_LOT = "propertyLot";
        static final String PROPERTY_YEAR = "propertyYear";
        static final String PROPERTY_PARKING = "propertyParking";
        static final String PROPERTY_ZONING = "propertyZoning";
        static final String PROPERTY_MLS = "propertyMls";

        static final String PURCHASE_PRICE = "purchasePrice";
        static final String AFTER_REPAIRS_VALUE = "afterRepairsValue";
        static final String USE_LOAN = "useLoan";
        static final String DOWN_PAYMENT = "downPayment";
        static final String INTEREST_RATE = "interestRate";
        static final String LOAN_DURATION = "loanDuration";
        static final String PURCHASE_COSTS = "purchaseCosts";
        static final String REPAIR_REMODEL_COSTS = "repairRemodelCosts";
        static final String GROSS_RENT = "grossRent";
        static final String OTHER_INCOME = "otherIncome";
        static final String EXPENSES = "expenses";
        static final String VACANCY = "vacancy";
        static final String APPRECIATION = "appreciation";
        static final String INCOME_INCREASE = "incomeIncrease";
        static final String EXPENSE_INCREASE = "expenseIncrease";
        static final String SELLING_COSTS = "sellingCosts";
        static final String LAND_VALUE = "landValue";
    }

    DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {

        // create table for properties
        db.execSQL("create table " + PropertyDbIds.TABLE + "(" +
                PropertyDbIds.ID + " INTEGER primary key autoincrement," +
                PropertyDbIds.NICKNAME + " TEXT," +
                PropertyDbIds.ADDRESS_STREET + " TEXT," +
                PropertyDbIds.ADDRESS_CITY + " TEXT," +
                PropertyDbIds.ADDRESS_STATE + " TEXT," +
                PropertyDbIds.ADDRESS_ZIP + " TEXT," +
                PropertyDbIds.PROPERTY_TYPE + " INTEGER," +
                PropertyDbIds.PROPERTY_BEDS + " TEXT," +
                PropertyDbIds.PROPERTY_BATHS + " TEXT," +
                PropertyDbIds.PROPERTY_SQUARE_FOOTAGE + " INTEGER," +
                PropertyDbIds.PROPERTY_LOT + " INTEGER," +
                PropertyDbIds.PROPERTY_YEAR + " INTEGER," +
                PropertyDbIds.PROPERTY_PARKING + " INTEGER," +
                PropertyDbIds.PROPERTY_ZONING + " INTEGER," +
                PropertyDbIds.PROPERTY_MLS + " TEXT," +
                PropertyDbIds.PURCHASE_PRICE + " INTEGER," +
                PropertyDbIds.AFTER_REPAIRS_VALUE + " INTEGER," +
                PropertyDbIds.USE_LOAN + " INTEGER," +
                PropertyDbIds.DOWN_PAYMENT + " INTEGER," +
                PropertyDbIds.INTEREST_RATE + " REAL," +
                PropertyDbIds.LOAN_DURATION + " INTEGER," +
                PropertyDbIds.PURCHASE_COSTS + " INTEGER," +
                PropertyDbIds.REPAIR_REMODEL_COSTS + " INTEGER," +
                PropertyDbIds.GROSS_RENT + " INTEGER," +
                PropertyDbIds.OTHER_INCOME + " INTEGER," +
                PropertyDbIds.EXPENSES + " INTEGER," +
                PropertyDbIds.VACANCY + " INTEGER," +
                PropertyDbIds.APPRECIATION + " INTEGER," +
                PropertyDbIds.INCOME_INCREASE + " INTEGER," +
                PropertyDbIds.EXPENSE_INCREASE + " INTEGER," +
                PropertyDbIds.SELLING_COSTS + " INTEGER," +
                PropertyDbIds.LAND_VALUE + " INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // For now, nothing to do, only one database version
    }

    /**
     * Insert a property into the database. The ID of the
     * property will be ignored, as it will be auto-assigned.
     *
     * @return the index of the new value if successful,
     * -1 otherwise
     */
    long insertProperty(final Property property)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PropertyDbIds.NICKNAME, property.nickname);
        contentValues.put(PropertyDbIds.ADDRESS_STREET, property.addressStreet);
        contentValues.put(PropertyDbIds.ADDRESS_CITY, property.addressCity);
        contentValues.put(PropertyDbIds.ADDRESS_STATE, property.addressState);
        contentValues.put(PropertyDbIds.ADDRESS_ZIP, property.addressZip);
        contentValues.put(PropertyDbIds.PROPERTY_TYPE, property.propertyType);
        contentValues.put(PropertyDbIds.PROPERTY_BEDS, property.propertyBeds);
        contentValues.put(PropertyDbIds.PROPERTY_BATHS, property.propertyBaths);
        contentValues.put(PropertyDbIds.PROPERTY_SQUARE_FOOTAGE, property.propertySqft);
        contentValues.put(PropertyDbIds.PROPERTY_LOT, property.propertyLot);
        contentValues.put(PropertyDbIds.PROPERTY_YEAR, property.propertyYear);
        contentValues.put(PropertyDbIds.PROPERTY_PARKING, property.propertyParking);
        contentValues.put(PropertyDbIds.PROPERTY_ZONING, property.propertyZoning);
        contentValues.put(PropertyDbIds.PROPERTY_MLS, property.propertyMls);
        contentValues.put(PropertyDbIds.PURCHASE_PRICE, property.purchasePrice);
        contentValues.put(PropertyDbIds.AFTER_REPAIRS_VALUE, property.afterRepairsValue);
        contentValues.put(PropertyDbIds.USE_LOAN, property.useLoan);
        contentValues.put(PropertyDbIds.DOWN_PAYMENT, property.downPayment);
        contentValues.put(PropertyDbIds.INTEREST_RATE, property.interestRate);
        contentValues.put(PropertyDbIds.LOAN_DURATION, property.loanDuration);
        contentValues.put(PropertyDbIds.PURCHASE_COSTS, property.purchaseCosts);
        contentValues.put(PropertyDbIds.REPAIR_REMODEL_COSTS, property.repairRemodelCosts);
        contentValues.put(PropertyDbIds.GROSS_RENT, property.grossRent);
        contentValues.put(PropertyDbIds.OTHER_INCOME, property.otherIncome);
        contentValues.put(PropertyDbIds.EXPENSES, property.expenses);
        contentValues.put(PropertyDbIds.VACANCY, property.vacancy);
        contentValues.put(PropertyDbIds.APPRECIATION, property.appreciation);
        contentValues.put(PropertyDbIds.INCOME_INCREASE, property.incomeIncrease);
        contentValues.put(PropertyDbIds.EXPENSE_INCREASE, property.expenseIncrease);
        contentValues.put(PropertyDbIds.SELLING_COSTS, property.sellingCosts);
        contentValues.put(PropertyDbIds.LAND_VALUE, property.landValue);

        SQLiteDatabase db = getWritableDatabase();
        long newId = db.insert(PropertyDbIds.TABLE, null, contentValues);
        db.close();

        return newId;
    }

    /**
     * Insert a property into the database, using a provided
     * writable database instance. This is useful if
     * multiple insertions will occur in the same transaction.
     * The ID of the property will be used, and the operation
     * will be rejected if the ID is already in use.
     *
     * @param writableDb
     *      writable database instance to use
     * @return the index of the new value if successful,
     * -1 otherwise
     */
    long insertProperty(final SQLiteDatabase writableDb, final Property property)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PropertyDbIds.ID, property.id);
        contentValues.put(PropertyDbIds.NICKNAME, property.nickname);
        contentValues.put(PropertyDbIds.ADDRESS_STREET, property.addressStreet);
        contentValues.put(PropertyDbIds.ADDRESS_CITY, property.addressCity);
        contentValues.put(PropertyDbIds.ADDRESS_STATE, property.addressState);
        contentValues.put(PropertyDbIds.ADDRESS_ZIP, property.addressZip);
        contentValues.put(PropertyDbIds.PROPERTY_TYPE, property.propertyType);
        contentValues.put(PropertyDbIds.PROPERTY_BEDS, property.propertyBeds);
        contentValues.put(PropertyDbIds.PROPERTY_BATHS, property.propertyBaths);
        contentValues.put(PropertyDbIds.PROPERTY_SQUARE_FOOTAGE, property.propertySqft);
        contentValues.put(PropertyDbIds.PROPERTY_LOT, property.propertyLot);
        contentValues.put(PropertyDbIds.PROPERTY_YEAR, property.propertyYear);
        contentValues.put(PropertyDbIds.PROPERTY_PARKING, property.propertyParking);
        contentValues.put(PropertyDbIds.PROPERTY_ZONING, property.propertyZoning);
        contentValues.put(PropertyDbIds.PROPERTY_MLS, property.propertyMls);
        contentValues.put(PropertyDbIds.PURCHASE_PRICE, property.purchasePrice);
        contentValues.put(PropertyDbIds.AFTER_REPAIRS_VALUE, property.afterRepairsValue);
        contentValues.put(PropertyDbIds.USE_LOAN, property.useLoan);
        contentValues.put(PropertyDbIds.DOWN_PAYMENT, property.downPayment);
        contentValues.put(PropertyDbIds.INTEREST_RATE, property.interestRate);
        contentValues.put(PropertyDbIds.LOAN_DURATION, property.loanDuration);
        contentValues.put(PropertyDbIds.PURCHASE_COSTS, property.purchaseCosts);
        contentValues.put(PropertyDbIds.REPAIR_REMODEL_COSTS, property.repairRemodelCosts);
        contentValues.put(PropertyDbIds.GROSS_RENT, property.grossRent);
        contentValues.put(PropertyDbIds.OTHER_INCOME, property.otherIncome);
        contentValues.put(PropertyDbIds.EXPENSES, property.expenses);
        contentValues.put(PropertyDbIds.VACANCY, property.vacancy);
        contentValues.put(PropertyDbIds.APPRECIATION, property.appreciation);
        contentValues.put(PropertyDbIds.INCOME_INCREASE, property.incomeIncrease);
        contentValues.put(PropertyDbIds.EXPENSE_INCREASE, property.expenseIncrease);
        contentValues.put(PropertyDbIds.SELLING_COSTS, property.sellingCosts);
        contentValues.put(PropertyDbIds.LAND_VALUE, property.landValue);

        long newId = writableDb.insert(PropertyDbIds.TABLE, null, contentValues);

        return newId;
    }

    /**
     * Update the property in the database. The unique ID for
     * the property will be used, all other fields represent
     * the values as will be updated in the database.
     *
     * @return true if the provided property exists and the value
     * was successfully updated, false otherwise.
     */
    boolean updateProperty(final Property property)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PropertyDbIds.NICKNAME, property.nickname);
        contentValues.put(PropertyDbIds.ADDRESS_STREET, property.addressStreet);
        contentValues.put(PropertyDbIds.ADDRESS_CITY, property.addressCity);
        contentValues.put(PropertyDbIds.ADDRESS_STATE, property.addressState);
        contentValues.put(PropertyDbIds.ADDRESS_ZIP, property.addressZip);
        contentValues.put(PropertyDbIds.PROPERTY_TYPE, property.propertyType);
        contentValues.put(PropertyDbIds.PROPERTY_BEDS, property.propertyBeds);
        contentValues.put(PropertyDbIds.PROPERTY_BATHS, property.propertyBaths);
        contentValues.put(PropertyDbIds.PROPERTY_SQUARE_FOOTAGE, property.propertySqft);
        contentValues.put(PropertyDbIds.PROPERTY_LOT, property.propertyLot);
        contentValues.put(PropertyDbIds.PROPERTY_YEAR, property.propertyYear);
        contentValues.put(PropertyDbIds.PROPERTY_PARKING, property.propertyParking);
        contentValues.put(PropertyDbIds.PROPERTY_ZONING, property.propertyZoning);
        contentValues.put(PropertyDbIds.PROPERTY_MLS, property.propertyMls);
        contentValues.put(PropertyDbIds.PURCHASE_PRICE, property.purchasePrice);
        contentValues.put(PropertyDbIds.AFTER_REPAIRS_VALUE, property.afterRepairsValue);
        contentValues.put(PropertyDbIds.USE_LOAN, property.useLoan);
        contentValues.put(PropertyDbIds.DOWN_PAYMENT, property.downPayment);
        contentValues.put(PropertyDbIds.INTEREST_RATE, property.interestRate);
        contentValues.put(PropertyDbIds.LOAN_DURATION, property.loanDuration);
        contentValues.put(PropertyDbIds.PURCHASE_COSTS, property.purchaseCosts);
        contentValues.put(PropertyDbIds.REPAIR_REMODEL_COSTS, property.repairRemodelCosts);
        contentValues.put(PropertyDbIds.GROSS_RENT, property.grossRent);
        contentValues.put(PropertyDbIds.OTHER_INCOME, property.otherIncome);
        contentValues.put(PropertyDbIds.EXPENSES, property.expenses);
        contentValues.put(PropertyDbIds.VACANCY, property.vacancy);
        contentValues.put(PropertyDbIds.APPRECIATION, property.appreciation);
        contentValues.put(PropertyDbIds.INCOME_INCREASE, property.incomeIncrease);
        contentValues.put(PropertyDbIds.EXPENSE_INCREASE, property.expenseIncrease);
        contentValues.put(PropertyDbIds.SELLING_COSTS, property.sellingCosts);
        contentValues.put(PropertyDbIds.LAND_VALUE, property.landValue);

        SQLiteDatabase db = getWritableDatabase();
        int rowsUpdated = db.update(PropertyDbIds.TABLE, contentValues,
                PropertyDbIds.ID + "=?",
                new String[]{Integer.toString(property.id)});
        db.close();

        return (rowsUpdated == 1);
    }

    /**
     * Get Property object for the named property in the database,
     *
     * @param id
     *      id of the property to query
     * @return Property object representing the named property,
     * or null if it could not be queried
     */
    Property getProperty(final int id)
    {
        SQLiteDatabase db = getReadableDatabase();
        Cursor data = db.rawQuery("select * from " + PropertyDbIds.TABLE +
                " where " + PropertyDbIds.ID + "=?", new String[]{Integer.toString(id)});

        Property property = null;

        if(data.getCount() == 1)
        {
            data.moveToFirst();
            property = Property.toProperty(data);
        }

        data.close();
        db.close();

        return property;
    }

    /**
     * Returns the number of properties in the database
     * of the provided type.
     *
     * @return the number of properties in the database
     * of the given type
     */
    int getPropertyCount()
    {
        SQLiteDatabase db = getReadableDatabase();
        Cursor data =  db.rawQuery("SELECT Count(*) FROM " + PropertyDbIds.TABLE, new String[]{});

        int numItems = 0;

        if(data.getCount() == 1)
        {
            data.moveToFirst();
            numItems = data.getInt(0);
        }

        data.close();
        db.close();

        return numItems;
    }

    /**
     * Delete a given property from the database
     *
     * @param id
     *      id of the property to delete
     * @return if the property was successfully deleted,
     * false otherwise
     */
    boolean deleteProperty(final int id)
    {
        SQLiteDatabase db = getWritableDatabase();
        int rowsDeleted =  db.delete(PropertyDbIds.TABLE,
                PropertyDbIds.ID + " = ? ",
                new String[]{Integer.toString(id)});
        db.close();

        return (rowsDeleted == 1);
    }

    /**
     * Returns a cursor pointing to all properties.
     */
    Cursor getProperties()
    {
        SQLiteDatabase db = getReadableDatabase();

        String query = "select * from " + PropertyDbIds.TABLE;

        query += " ORDER BY " + PropertyDbIds.NICKNAME;

        Cursor res =  db.rawQuery(query, new String[0]);
        return res;
    }
}
