package protect.rentalcalc;

import android.content.Context;
import android.util.Log;

enum ParkingType
{
    BLANK(R.string.blank),
    CAR_PORT(R.string.carport),
    GARAGE(R.string.garage),
    OFF_STREET(R.string.offstreet),
    ON_STREET(R.string.onstreet),
    NONE(R.string.none),
    PRIVATE_LOT(R.string.privatelot),
    ;

    private static final String TAG = "RentalCalc";

    final int stringId;

    ParkingType(int stringId)
    {
        this.stringId = stringId;
    }

    static ParkingType fromString(Context context, String string)
    {
        if(string != null)
        {
            for(ParkingType type : ParkingType.values())
            {
                if(string.equals(context.getString(type.stringId)))
                {
                    return type;
                }
            }
        }

        Log.w(TAG, "Failed to lookup ParkingType id " + string);

        return ParkingType.BLANK;
    }
}
