package protect.rentalcalc;

import android.database.Cursor;

class Property
{
    final int id;
    final String nickname;
    final String addressStreet;
    final String addressCity;
    final String addressState;
    final String addressZip;
    final int propertyType;
    final String propertyBeds;
    final String propertyBaths;
    final int propertySqft;
    final int propertyLot;
    final int propertyYear;
    final int propertyParking;
    final String propertyZoning;
    final String propertyMls;
    final int propertyPrice;

    Property(final int id, final String nickname, final String addressStreet,
                     final String addressCity, final String addressState, final String addressZip,
                     final int propertyType, final String propertyBeds, final String propertyBaths,
                     final int propertySqft, final int propertyLot, final int propertyYear,
                     final int propertyParking, final String propertyZoning, final String propertyMls)
    {
        this.id = id;
        this.nickname = nickname;
        this.addressStreet = addressStreet;
        this.addressCity = addressCity;
        this.addressState = addressState;
        this.addressZip = addressZip;
        this.propertyType = propertyType;
        this.propertyBeds = propertyBeds;
        this.propertyBaths = propertyBaths;
        this.propertySqft = propertySqft;
        this.propertyLot = propertyLot;
        this.propertyYear = propertyYear;
        this.propertyParking = propertyParking;
        this.propertyZoning = propertyZoning;
        this.propertyMls = propertyMls;
        this.propertyPrice = 0;
    }

    private static String toBlankIfNull(final String string)
    {
        if(string != null)
        {
            return string;
        }
        else
        {
            return "";
        }
    }

    static Property toProperty(Cursor cursor)
    {
        final int id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.PropertyDbIds.ID));
        final String nickname = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.PropertyDbIds.NICKNAME));
        final String addressStreet = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.PropertyDbIds.ADDRESS_STREET));
        final String addressCity  = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.PropertyDbIds.ADDRESS_CITY));
        final String addressState = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.PropertyDbIds.ADDRESS_STATE));
        final String addressZip = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.PropertyDbIds.ADDRESS_ZIP));
        final int propertyType = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.PropertyDbIds.PROPERTY_TYPE));
        final String propertyBeds = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.PropertyDbIds.PROPERTY_BEDS));
        final String propertyBaths = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.PropertyDbIds.PROPERTY_BATHS));
        final int propertySqft = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.PropertyDbIds.PROPERTY_SQUARE_FOOTAGE));
        final int propertyLot = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.PropertyDbIds.PROPERTY_LOT));
        final int propertyYear = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.PropertyDbIds.PROPERTY_YEAR));
        final int propertyParking = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.PropertyDbIds.PROPERTY_PARKING));
        final String propertyZoning = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.PropertyDbIds.PROPERTY_ZONING));
        final String propertyMls = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.PropertyDbIds.PROPERTY_MLS));

        return new Property(
                id,
                toBlankIfNull(nickname),
                toBlankIfNull(addressStreet),
                toBlankIfNull(addressCity),
                toBlankIfNull(addressState),
                toBlankIfNull(addressZip),
                propertyType,
                toBlankIfNull(propertyBeds),
                toBlankIfNull(propertyBaths),
                propertySqft,
                propertyLot,
                propertyYear,
                propertyParking,
                toBlankIfNull(propertyZoning),
                toBlankIfNull(propertyMls)
                //0 //TODO: FIX
                );
    }
}