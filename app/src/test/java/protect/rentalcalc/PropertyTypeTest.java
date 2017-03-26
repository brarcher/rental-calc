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
public class PropertyTypeTest
{
    @Test
    public void testStringLookup()
    {
        Resources resources = RuntimeEnvironment.application.getResources();
        for(PropertyType type : PropertyType.values())
        {
            String string = resources.getString(type.stringId);
            PropertyType lookup = PropertyType.fromString(RuntimeEnvironment.application, string);
            assertTrue(type == lookup);
        }

        // Check that a failed lookup also returns the expected value
        PropertyType type = PropertyType.fromString(RuntimeEnvironment.application, "Does not exist");
        assertTrue(type == PropertyType.BLANK);

        type = PropertyType.fromString(RuntimeEnvironment.application, null);
        assertTrue(type == PropertyType.BLANK);
    }
}