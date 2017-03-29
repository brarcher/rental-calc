package protect.rentalcalc;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

class TestHelper
{
    /**
     * Check that a String matches an integer value
     */
    static void checkIntField(int expected, String actual)
    {
        if(actual.isEmpty())
        {
            assertEquals(expected, 0);
        }
        else
        {
            assertEquals(expected, Integer.parseInt(actual));
        }
    }

    /**
     * Assign values to a property so that they are valid but not
     * default values. Presumable each field is also unique.
     */
    static void preloadProperty(Property property) throws IllegalAccessException
    {
        // Set the fields to some non-default value which should be
        // different for every field
        for(Field field : property.getClass().getDeclaredFields())
        {
            if(field.getType() == String.class)
            {
                String fieldName = field.getName();
                String value = fieldName;

                // These are spinners and the possible values are limited
                if(fieldName.equals("propertyBeds"))
                {
                    value = "5";
                }
                else if(fieldName.equals("propertyBaths"))
                {
                    value = "5.5";
                }

                field.set(property, value);
            }

            if(field.getType() == Integer.TYPE)
            {
                String fieldName = field.getName();
                int value = Math.abs(fieldName.hashCode());

                // These are spinners and the possible values are limited
                if(fieldName.equals("propertyType"))
                {
                    value = value % PropertyType.values().length;
                }
                else if(fieldName.equals("propertyParking"))
                {
                    value = value % ParkingType.values().length;
                }

                field.set(property, value);
            }
        }
    }
}
