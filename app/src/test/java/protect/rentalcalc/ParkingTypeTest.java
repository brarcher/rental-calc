package protect.rentalcalc;

import android.content.res.Resources;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class ParkingTypeTest
{
    @Test
    public void testStringLookup()
    {
        Resources resources = RuntimeEnvironment.application.getResources();
        for(ParkingType type : ParkingType.values())
        {
            String string = resources.getString(type.stringId);
            ParkingType lookup = ParkingType.fromString(RuntimeEnvironment.application, string);
            assertTrue(type == lookup);
        }

        // Check that a failed lookup also returns the expected value
        ParkingType type = ParkingType.fromString(RuntimeEnvironment.application, "Does not exist");
        assertTrue(type == ParkingType.BLANK);

        type = ParkingType.fromString(RuntimeEnvironment.application, null);
        assertTrue(type == ParkingType.BLANK);
    }
}