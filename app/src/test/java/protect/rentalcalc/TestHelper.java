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
     * Check that a String matches a double value
     */
    static void checkDoubleField(double expected, String actual)
    {
        final double DELTA = 0.01;

        if(actual.isEmpty())
        {
            assertEquals(expected, 0, DELTA);
        }
        else
        {
            assertEquals(expected, Double.parseDouble(actual), DELTA);
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

                Map<String, Integer> maxLengths = new HashMap<>();
                maxLengths.put("purchasePrice", 13);
                maxLengths.put("afterRepairsValue", 13);
                maxLengths.put("downPayment", 3);
                maxLengths.put("interestRate", 3);
                maxLengths.put("loanDuration", 2);
                maxLengths.put("purchaseCosts", 3);
                maxLengths.put("repairRemodelCosts", 13);
                maxLengths.put("grossRent", 7);
                maxLengths.put("otherIncome", 7);
                maxLengths.put("expenses", 6);
                maxLengths.put("vacancy", 3);
                maxLengths.put("appreciation", 3);
                maxLengths.put("incomeIncrease", 3);
                maxLengths.put("expenseIncrease", 3);
                maxLengths.put("sellingCosts", 3);
                maxLengths.put("landValue", 13);
                maxLengths.put("incomeTaxRate", 3);

                if(maxLengths.containsKey(fieldName))
                {
                    String strValue = Integer.toString(value);
                    int maxLength = maxLengths.get(fieldName);
                    strValue = strValue.substring(0, Math.min(strValue.length(), maxLength));
                    value = Integer.parseInt(strValue);
                }

                field.set(property, value);
            }
        }
    }

    static void preloadPropertyWithItemize(Property property) throws IllegalAccessException
    {
        preloadProperty(property);

        for(int index = 1; index <= 10; index++)
        {
            property.purchaseCostsItemized.put("purchase cost " + index, index*10);

            if( (index % 2) == 0)
            {
                property.repairRemodelCostsItemized.put("repair cost " + index, index*10);
            }

            if( (index % 3) == 0)
            {
                property.expensesItemized.put("expense cost " + index, index*10);
            }
        }
    }
}
