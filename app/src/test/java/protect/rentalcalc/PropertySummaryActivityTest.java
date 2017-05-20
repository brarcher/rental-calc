package protect.rentalcalc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.common.collect.ImmutableMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.ShadowToast;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class PropertySummaryActivityTest
{
    private DBHelper db;

    private final Map<Integer, String> fieldNameLookup = new ImmutableMap.Builder<Integer, String>()
            .put(R.id.priceValue, "priceValue")
            .put(R.id.financedValue, "financedValue")
            .put(R.id.downPaymentValue, "downPaymentValue")
            .put(R.id.purchaseCostsValue, "purchaseCostsValue")
            .put(R.id.repairRemodelCostsValue, "repairRemodelCostsValue")
            .put(R.id.totalCashNeededValue, "totalCashNeededValue")
            .put(R.id.pricePerSizeValue, "pricePerSizeValue")
            .put(R.id.rentValue, "rentValue")
            .put(R.id.vancancyValue, "vancancyValue")
            .put(R.id.operatingIncomeValue, "operatingIncomeValue")
            .put(R.id.operatingExpensesValue, "operatingExpensesValue")
            .put(R.id.netOperatingIncomeValue, "netOperatingIncomeValue")
            .put(R.id.mortgageValue, "mortgageValue")
            .put(R.id.cashFlowValue, "cashFlowValue")
            .put(R.id.afterTaxCashFlowValue, "afterTaxCashFlowValue")
            .put(R.id.capitalizationRateValue, "capitalizationRateValue")
            .put(R.id.cashOnCashValue, "cashOnCashValue")
            .put(R.id.rentToValueValue, "rentToValueValue")
            .put(R.id.grossRentMultiplierValue, "grossRentMultiplierValue")
            .build();

    @Before
    public void setUp()
    {
        // Output logs emitted during tests so they may be accessed
        ShadowLog.stream = System.out;
        db = new DBHelper(RuntimeEnvironment.application);
    }

    @After
    public void tearDown()
    {
        db.close();
    }

    @Test
    public void clickBackFinishes()
    {
        ActivityController controller = startWithProperty(new Property());
        Activity activity = (Activity)controller.get();

        controller.start();
        controller.visible();
        controller.resume();

        assertTrue(shadowOf(activity).isFinishing() == false);
        shadowOf(activity).clickMenuItem(android.R.id.home);
        assertTrue(shadowOf(activity).isFinishing());

        // Check that can finish out the lifecycle
        controller.pause();
        controller.stop();
        controller.destroy();
    }

    @Test
    public void startWithoutBundle()
    {
        ActivityController controller = Robolectric.buildActivity(PropertySummaryActivity.class).create();
        Activity activity = (Activity)controller.get();

        controller.start();
        controller.visible();
        controller.resume();
        assertTrue(activity.isFinishing());

        String latestToast = ShadowToast.getTextOfLatestToast();
        assertNotNull(latestToast);
    }

    @Test
    public void startWithoutProperty()
    {
        Intent intent = new Intent();
        final Bundle bundle = new Bundle();
        intent.putExtras(bundle);

        ActivityController controller = Robolectric.buildActivity(PropertySummaryActivity.class, intent).create();
        Activity activity = (Activity)controller.get();

        controller.start();
        controller.visible();
        controller.resume();
        assertTrue(activity.isFinishing());

        String latestToast = ShadowToast.getTextOfLatestToast();
        assertNotNull(latestToast);
    }

    @Test
    public void startWithMissingProperty()
    {
        Intent intent = new Intent();
        final Bundle bundle = new Bundle();
        bundle.putLong("id", 0);
        intent.putExtras(bundle);

        ActivityController controller = Robolectric.buildActivity(PropertySummaryActivity.class, intent).create();
        Activity activity = (Activity)controller.get();

        controller.start();
        controller.visible();
        controller.resume();
        assertTrue(activity.isFinishing());

        String latestToast = ShadowToast.getTextOfLatestToast();
        assertNotNull(latestToast);
    }

    private ActivityController startWithProperty(Property property)
    {
        long id = db.insertProperty(property);

        Intent intent = new Intent();
        final Bundle bundle = new Bundle();
        bundle.putLong("id", id);
        intent.putExtras(bundle);

        ActivityController controller = Robolectric.buildActivity(PropertySummaryActivity.class, intent).create();
        Activity activity = (Activity)controller.get();

        controller.start();
        controller.visible();
        controller.resume();
        assertTrue(activity.isFinishing() == false);

        String latestToast = ShadowToast.getTextOfLatestToast();
        assertNull(latestToast);

        return controller;
    }

    private void checkFields(Activity activity, Map<Integer, String> expected)
    {
        for(Map.Entry<Integer, String> item : expected.entrySet())
        {
            TextView view = (TextView)activity.findViewById(item.getKey());
            String text = view.getText().toString();
            String fieldName = fieldNameLookup.get(item.getKey());

            assertEquals(fieldName + " not as expected", item.getValue(), text);
        }
    }

    @Test
    public void startWithDefaultProperty() throws Exception
    {
        Property property = new Property();

        ActivityController controller = startWithProperty(property);
        Activity activity = (Activity)controller.get();

        Map<Integer, String> expectedValues = new ImmutableMap.Builder<Integer, String>()
                .put(R.id.priceValue, "0")
                .put(R.id.financedValue, "0")
                .put(R.id.downPaymentValue, "0")
                .put(R.id.purchaseCostsValue, "0")
                .put(R.id.repairRemodelCostsValue, "0")
                .put(R.id.totalCashNeededValue, "0")
                .put(R.id.pricePerSizeValue, "0")
                .put(R.id.rentValue, "0")
                .put(R.id.vancancyValue, "0")
                .put(R.id.operatingIncomeValue, "0")
                .put(R.id.operatingExpensesValue, "0")
                .put(R.id.netOperatingIncomeValue, "0")
                .put(R.id.mortgageValue, "0")
                .put(R.id.cashFlowValue, "0")
                .put(R.id.afterTaxCashFlowValue, "0")
                .put(R.id.capitalizationRateValue, "0.0")
                .put(R.id.cashOnCashValue, "0.0")
                .put(R.id.rentToValueValue, "0.0")
                .put(R.id.grossRentMultiplierValue, "0.0")
                .build();

        checkFields(activity, expectedValues);
    }

    @Test
    public void startWithExampleProperty() throws Exception
    {
        Property property = new Property();

        property.purchasePrice = 123456;
        property.afterRepairsValue = 150000;
        property.useLoan = true;
        property.downPayment = 10;
        property.purchaseCosts = 4;
        property.repairRemodelCosts = 25000;
        property.propertySqft = 2342;
        property.grossRent = 1650;
        property.vacancy = 5;
        property.expenses = 15;
        property.interestRate = 5.125;
        property.loanDuration = 30;
        property.incomeTaxRate = 10;

        ActivityController controller = startWithProperty(property);
        Activity activity = (Activity)controller.get();

        Map<Integer, String> expectedValues = new ImmutableMap.Builder<Integer, String>()
                .put(R.id.priceValue, "123456")
                .put(R.id.financedValue, "111110")  // 90% of 123456
                .put(R.id.downPaymentValue, "12346")  // 10% of 123456 = 12345.6
                .put(R.id.purchaseCostsValue, "4938") // 4% of 123456 = 4938.24
                .put(R.id.repairRemodelCostsValue, "25000")
                .put(R.id.totalCashNeededValue, "42284") // 12345.6 + 4928.24 + 25000 = 42273.84
                .put(R.id.pricePerSizeValue, "53") // 123456/2342
                .put(R.id.rentValue, "19800") // 1650 * 12 = 19800
                .put(R.id.vancancyValue, "990") // 5% of 19800 = 990
                .put(R.id.operatingIncomeValue, "18810")  // 19800 - 990
                .put(R.id.operatingExpensesValue, "2970") // 15% of 19800 = 2970
                .put(R.id.netOperatingIncomeValue, "15840") // 19800 - 2970 - 990 = 15840
                .put(R.id.mortgageValue, "7260") // 604.98 * 12 = 7259.76
                .put(R.id.cashFlowValue, "8580") // 15840 - 7260 = 8580
                .put(R.id.afterTaxCashFlowValue, "7722") // 8580 * 90% = 7722
                .put(R.id.capitalizationRateValue, "12.8") // 1320 * 12 / 123456 = 0.128
                .put(R.id.cashOnCashValue, "20.3") // 715*12 / 42273.84 = 0.2029
                .put(R.id.rentToValueValue, "1.1") // 1650 / 150000 = 0.011
                .put(R.id.grossRentMultiplierValue, "6.2") // 123456 / (1650*12) = 6.23
                .build();

        checkFields(activity, expectedValues);
    }

    @Test
    public void testNoLoanProperty() throws Exception
    {
        Property property = new Property();

        property.purchasePrice = 123456;
        property.afterRepairsValue = 150000;
        property.useLoan = false;
        // Fill in loan fields, to check that they are ignored
        property.interestRate = 5.125;
        property.loanDuration = 30;
        property.downPayment = 10;
        property.purchaseCosts = 4;
        property.repairRemodelCosts = 25000;
        property.propertySqft = 2342;
        property.grossRent = 1650;
        property.vacancy = 5;
        property.expenses = 15;
        property.incomeTaxRate = 25;

        ActivityController controller = startWithProperty(property);
        Activity activity = (Activity)controller.get();

        Map<Integer, String> expectedValues = new ImmutableMap.Builder<Integer, String>()
                .put(R.id.priceValue, "123456")
                .put(R.id.financedValue, "0")  // nothing financed
                .put(R.id.downPaymentValue, "123456")  // 100% of 123456
                .put(R.id.purchaseCostsValue, "4938") // 4% of 123456 = 4938.24
                .put(R.id.repairRemodelCostsValue, "25000")
                .put(R.id.totalCashNeededValue, "153394") // 123456 + 4938.24 + 25000 = 153384.24
                .put(R.id.pricePerSizeValue, "53") // 123456/2342
                .put(R.id.rentValue, "19800") // 1650 * 12 = 19800
                .put(R.id.vancancyValue, "990") // 5% of 19800 = 990
                .put(R.id.operatingIncomeValue, "18810")  // 19800 - 990 = 18810
                .put(R.id.operatingExpensesValue, "2970") // 15% of 19800 = 2970
                .put(R.id.netOperatingIncomeValue, "15840") // 19800 - 990 - 2970 = 15840
                .put(R.id.mortgageValue, "0") // 0
                .put(R.id.cashFlowValue, "15840") // 19800 - 990 - 2970 = 15840
                .put(R.id.afterTaxCashFlowValue, "11880") // 15840 * 75% = 11880
                .put(R.id.capitalizationRateValue, "12.8") // 1320 * 12 / 123456 = 0.128
                .put(R.id.cashOnCashValue, "10.3") // 1320*12 / 153384.24 = 0.1032
                .put(R.id.rentToValueValue, "1.1") // 1650 / 150000 = 0.011
                .put(R.id.grossRentMultiplierValue, "6.2") // 123456 / (1650*12) = 6.23
                .build();

        checkFields(activity, expectedValues);
    }

    @Test
    public void testItemizingProperty() throws Exception
    {
        Property property = new Property();

        // Fill in the normal fields
        property.purchasePrice = 123456;
        property.afterRepairsValue = 150000;
        property.useLoan = true;
        property.downPayment = 10;
        property.purchaseCosts = 4;
        property.repairRemodelCosts = 25000;
        property.propertySqft = 2342;
        property.grossRent = 1650;
        property.vacancy = 5;
        property.expenses = 15;
        property.interestRate = 5.125;
        property.loanDuration = 30;
        property.incomeTaxRate = 10;
        // And also fill in the itemized fields, to see if they are used
        property.purchaseCostsItemized = new HashMap<>
            (
                new ImmutableMap.Builder<String, Integer>()
                .put("item 1", 100)
                .put("item 2", 123)
                .put("item 3", 2000)
                .put("item 4", 45)
                .put("item 5", 1250)
                .build()
            ); // = 3518
        property.repairRemodelCostsItemized = new HashMap<>
            (
                new ImmutableMap.Builder<String, Integer>()
                .put("item 1", 10000)
                .put("item 2", 2500)
                .put("item 3", 101)
                .put("item 4", 1200)
                .build()
            ); // = 13801
        property.expensesItemized = new HashMap<>
            (
                new ImmutableMap.Builder<String, Integer>()
                .put("item 1", 45)
                .put("item 2", 100)
                .put("item 3", 12)
                .put("item 4", 20)
                .build()
            ); // = 177

        ActivityController controller = startWithProperty(property);
        Activity activity = (Activity)controller.get();

        Map<Integer, String> expectedValues = new ImmutableMap.Builder<Integer, String>()
                .put(R.id.priceValue, "123456")
                .put(R.id.financedValue, "111110")  // 90% of 123456
                .put(R.id.downPaymentValue, "12346")  // 10% of 123456 = 12345.6
                .put(R.id.purchaseCostsValue, "3518") // 3518 (see map)
                .put(R.id.repairRemodelCostsValue, "13801") // 13801 (see map)
                .put(R.id.totalCashNeededValue, "29665") // 12345.6 + 3518 + 13801 = 29664.6
                .put(R.id.pricePerSizeValue, "53") // 123456/2342
                .put(R.id.rentValue, "19800") // 1650 * 12 = 19800
                .put(R.id.vancancyValue, "990") // 5% of 19800 = 990
                .put(R.id.operatingIncomeValue, "18810")  // 19800 - 990 = 18810
                .put(R.id.operatingExpensesValue, "2124") // 177 * 12 = 2124
                .put(R.id.netOperatingIncomeValue, "16686") // 18810 - 2124 = 16686
                .put(R.id.mortgageValue, "7260") // 604.98 * 12 = 7259.76
                .put(R.id.cashFlowValue, "9426") // 16686 - 7260 = 9426
                .put(R.id.afterTaxCashFlowValue, "8484") // 9426 * 90% = 8483.4
                .put(R.id.capitalizationRateValue, "13.5") // 16686 / 123456 = 0.135
                .put(R.id.cashOnCashValue, "31.8") // 9426 / 29664.6 = 0.3177
                .put(R.id.rentToValueValue, "1.1") // 1650 / 150000 = 0.011
                .put(R.id.grossRentMultiplierValue, "6.2") // 123456 / (1650*12) = 6.23
                .build();

        checkFields(activity, expectedValues);
    }
}
