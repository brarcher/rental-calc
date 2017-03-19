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
                PropertyDbIds.PROPERTY_MLS + " TEXT)");
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
                " where " + PropertyDbIds.ID + "=?", new String[]{String.format("%d", id)});

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
