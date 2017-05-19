package protect.rentalcalc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

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
public class PropertyProjectionsActivityTest
{
    private DBHelper db;

    private final Map<Integer, String> fieldNameLookup = new ImmutableMap.Builder<Integer, String>()
            .put(R.id.rentValue, "rentValue")
            .put(R.id.vancancyValue, "vancancyValue")
            .put(R.id.operatingIncomeValue, "operatingIncomeValue")
            .put(R.id.operatingExpensesValue, "operatingExpensesValue")
            .put(R.id.netOperatingIncomeValue, "netOperatingIncomeValue")
            .put(R.id.mortgageValue, "mortgageValue")
            .put(R.id.cashFlowValue, "cashFlowValue")
            .put(R.id.afterTaxCashFlowValue, "afterTaxCashFlowValue")
            .put(R.id.propertyValueValue, "propertyValueValue")
            .put(R.id.loanBalanceValue, "loanBalanceValue")
            .put(R.id.totalEquityValue, "totalEquityValue")
            .put(R.id.depreciationValue, "depreciationValue")
            .put(R.id.mortgageInterestValue, "mortgageInterestValue")
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
        ActivityController controller = Robolectric.buildActivity(PropertyProjectionsActivity.class).create();
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

        ActivityController controller = Robolectric.buildActivity(PropertyProjectionsActivity.class, intent).create();
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

        ActivityController controller = Robolectric.buildActivity(PropertyProjectionsActivity.class, intent).create();
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

        ActivityController controller = Robolectric.buildActivity(PropertyProjectionsActivity.class, intent).create();
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
        SeekBar yearSeeker = (SeekBar)activity.findViewById(R.id.yearSeeker);

        // The first shown year should be year 1, or index 0.
        assertEquals(0, yearSeeker.getProgress());

        Map<Integer, String> expectedValues = new ImmutableMap.Builder<Integer, String>()
                .put(R.id.rentValue, "0")
                .put(R.id.vancancyValue, "0")
                .put(R.id.operatingIncomeValue, "0")
                .put(R.id.operatingExpensesValue, "0")
                .put(R.id.netOperatingIncomeValue, "0")
                .put(R.id.mortgageValue, "0")
                .put(R.id.cashFlowValue, "0")
                .put(R.id.afterTaxCashFlowValue, "0")
                .put(R.id.propertyValueValue, "0")
                .put(R.id.loanBalanceValue, "0")
                .put(R.id.totalEquityValue, "0")
                .put(R.id.depreciationValue, "0")
                .put(R.id.mortgageInterestValue, "0")
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

        property.purchasePrice = 100000;
        property.afterRepairsValue = 150000;
        property.useLoan = true;
        property.downPayment = 20;
        property.purchaseCosts = 4;
        property.repairRemodelCosts = 25000;
        property.grossRent = 1500;
        property.vacancy = 5;
        property.expenses = 15;
        property.interestRate = 4;
        property.loanDuration = 10;
        property.landValue = 10000;
        property.incomeIncrease = 2;
        property.expenseIncrease = 3;
        property.appreciation = 4;
        property.incomeTaxRate = 25;

        ActivityController controller = startWithProperty(property);
        Activity activity = (Activity)controller.get();

        Map<Integer, String> expectedValues = new ImmutableMap.Builder<Integer, String>()
                .put(R.id.rentValue, "18000") // 1500 * 12 = 18000
                .put(R.id.vancancyValue, "900") // 18000 * 5% = 900
                .put(R.id.operatingIncomeValue, "17100") // 18000 - 900 = 17100
                .put(R.id.operatingExpensesValue, "2700") // 18000 * 15% = 2700
                .put(R.id.netOperatingIncomeValue, "14400") // 18000 - 900 - 2700 = 14400
                .put(R.id.mortgageValue, "9720") // 809.96 * 12 = 9719.52
                .put(R.id.cashFlowValue, "4680") // 18000 - 900 - 2700 - 9719.52 = 4680.48
                .put(R.id.afterTaxCashFlowValue, "3510") // 4680 * 75% = 3510
                .put(R.id.propertyValueValue, "150000")
                .put(R.id.loanBalanceValue, "73360") // calculated from spreadsheet: 73359.60
                .put(R.id.totalEquityValue, "76640") // 150000 - 73359.60 = 76640.40
                .put(R.id.depreciationValue, "3418") // (100000 + 4%*100000 - 10000) / 27.5 = 3418.18
                .put(R.id.mortgageInterestValue, "3079") // from spreasheet: 3079.14
                .put(R.id.capitalizationRateValue, "14.4") // 14400 / 100000
                .put(R.id.cashOnCashValue, "9.6") // 3680 / (20000 + 100000*4% + 25000) = .0956
                .put(R.id.rentToValueValue, "1.0") // 1000 / 100000
                .put(R.id.grossRentMultiplierValue, "5.6") // 100000 / 18000 = 5.55
                .build();

        checkFields(activity, expectedValues);

        // Skip ahead and check the fifth (5) year
        SeekBar yearSeeker = (SeekBar)activity.findViewById(R.id.yearSeeker);
        yearSeeker.setProgress(5-1);

        expectedValues = new ImmutableMap.Builder<Integer, String>()
                .put(R.id.rentValue, "19484") // 1500 * 12 * 1.02^4 = 19483.77
                .put(R.id.vancancyValue, "974") // 19483.77 * 5% = 974.18
                .put(R.id.operatingIncomeValue, "18510") // 19483.77 - 974.18 = 18509.59
                .put(R.id.operatingExpensesValue, "3039") // 18000 * 15% * 1.03^4 = 3038.87
                .put(R.id.netOperatingIncomeValue, "15471") // 19483.77 - 974.18 - 3038.87 = 15470.72
                .put(R.id.mortgageValue, "9720") // 809.96 * 12 = 9719.52
                .put(R.id.cashFlowValue, "5751") // 15470.72 - 9719.52 = 5751.20
                .put(R.id.afterTaxCashFlowValue, "4313") // 4313.25 * 75% = 4313.4
                .put(R.id.propertyValueValue, "175479") // 150000 * 1.04^4 = 175478.78
                .put(R.id.loanBalanceValue, "43980") // calculated from spreadsheet: 43980.30
                .put(R.id.totalEquityValue, "131499") // 175478.78 - 43980.30 = 131498.48
                .put(R.id.depreciationValue, "3418") // (100000 + 4%*100000 - 10000) / 27.5 = 3418.18
                .put(R.id.mortgageInterestValue, "1929") // from spreasheet: 1929.03
                .put(R.id.capitalizationRateValue, "15.5") // 15470.72 / 100000 = 0.1547
                .put(R.id.cashOnCashValue, "11.7") // 5751.20 / (20000 + 100000*4% + 25000) = .1173
                .put(R.id.rentToValueValue, "0.9") // 19484/12 / 175479 = 0.0092
                .put(R.id.grossRentMultiplierValue, "5.1") // 100000 / 19483.77 = 5.13
                .build();

        checkFields(activity, expectedValues);

        // Check the 11th year, the mortgage should be done
        yearSeeker.setProgress(11-1);

        expectedValues = new ImmutableMap.Builder<Integer, String>()
                .put(R.id.rentValue, "21942") // 1500 * 12 * 1.02^10 = 21941.89
                .put(R.id.vancancyValue, "1097") // 21941.89 * 5% = 1097.09
                .put(R.id.operatingIncomeValue, "20845") // 21941.89 - 1097.09 = 20844.80
                .put(R.id.operatingExpensesValue, "3629") // 18000 * 15% * 1.03^10 = 3628.57
                .put(R.id.netOperatingIncomeValue, "17216") // 20844.80 - 3628.57 = 17216.23
                .put(R.id.mortgageValue, "0") // mortgage is done
                .put(R.id.cashFlowValue, "17216")
                .put(R.id.afterTaxCashFlowValue, "12912")
                .put(R.id.propertyValueValue, "222037") // 150000 * 1.04^10 = 222036.64
                .put(R.id.loanBalanceValue, "0")
                .put(R.id.totalEquityValue, "222037")
                .put(R.id.depreciationValue, "3418") // (100000 + 4%*100000 - 10000) / 27.5 = 3418.18
                .put(R.id.mortgageInterestValue, "0")
                .put(R.id.capitalizationRateValue, "17.2") // 17216.23 / 100000 = 0.1721
                .put(R.id.cashOnCashValue, "35.1") // 17216.23 / (20000 + 100000*4% + 25000) = .3513
                .put(R.id.rentToValueValue, "0.8") // 21941.89/12 / 222037 = 0.0082
                .put(R.id.grossRentMultiplierValue, "4.6") // 100000 / 21941.89 = 4.55
                .build();

        checkFields(activity, expectedValues);

        // Skip ahead and check the 28th year, depreciation should be 1/2
        yearSeeker = (SeekBar)activity.findViewById(R.id.yearSeeker);
        yearSeeker.setProgress(28-1);

        expectedValues = new ImmutableMap.Builder<Integer, String>()
                .put(R.id.depreciationValue, "1709") // (100000 + 4%*100000 - 10000) / 27.5 / 2 = 1709.09
                .build();

        checkFields(activity, expectedValues);

        // Skip ahead and check the 29th year, depreciation should be done
        yearSeeker = (SeekBar)activity.findViewById(R.id.yearSeeker);
        yearSeeker.setProgress(29-1);

        expectedValues = new ImmutableMap.Builder<Integer, String>()
                .put(R.id.depreciationValue, "0") // depreciation should be finished
                .build();

        checkFields(activity, expectedValues);
    }

    @Test
    public void testNoLoanProperty() throws Exception
    {
        Property property = new Property();

        property.purchasePrice = 100000;
        property.afterRepairsValue = 150000;
        property.useLoan = false;
        // Fill in loan fields, to check that they are ignored
        property.interestRate = 4;
        property.loanDuration = 10;
        property.downPayment = 20;
        property.purchaseCosts = 4;
        property.repairRemodelCosts = 25000;
        property.grossRent = 1500;
        property.vacancy = 5;
        property.expenses = 15;
        property.landValue = 10000;
        property.incomeTaxRate = 25;


        ActivityController controller = startWithProperty(property);
        Activity activity = (Activity)controller.get();

        Map<Integer, String> expectedValues = new ImmutableMap.Builder<Integer, String>()
                .put(R.id.rentValue, "18000") // 1500 * 12 = 18000
                .put(R.id.vancancyValue, "900") // 18000 * 5% = 900
                .put(R.id.operatingIncomeValue, "17100") // 18000 - 900 = 17100
                .put(R.id.operatingExpensesValue, "2700") // 18000 * 15% = 2700
                .put(R.id.netOperatingIncomeValue, "14400") // 18000 - 900 - 2700 = 14400
                .put(R.id.mortgageValue, "0")
                .put(R.id.cashFlowValue, "14400") // 18000 - 900 - 2700 - 0 = 14400
                .put(R.id.afterTaxCashFlowValue, "10800") // 14400 * 75% = 10800
                .put(R.id.propertyValueValue, "150000")
                .put(R.id.loanBalanceValue, "0")
                .put(R.id.totalEquityValue, "150000")
                .put(R.id.depreciationValue, "3418") // (100000 + 4%*100000 - 10000) / 27.5 = 3418.18
                .put(R.id.mortgageInterestValue, "0")
                .put(R.id.capitalizationRateValue, "14.4") // 14400 / 100000
                .put(R.id.cashOnCashValue, "11.2") // 14400 / (100000 + 100000*4% + 25000) = 0.1116
                .put(R.id.rentToValueValue, "1.0") // 1000 / 100000
                .put(R.id.grossRentMultiplierValue, "5.6") // 100000 / 18000 = 5.55
                .build();

        checkFields(activity, expectedValues);
    }

    @Test
    public void testItemizingProperty() throws Exception
    {
        Property property = new Property();

        property.purchasePrice = 100000;
        property.afterRepairsValue = 150000;
        property.useLoan = true;
        property.downPayment = 20;
        property.purchaseCosts = 4;
        property.repairRemodelCosts = 25000;
        property.grossRent = 1500;
        property.vacancy = 5;
        property.expenses = 15;
        property.interestRate = 4;
        property.loanDuration = 10;
        property.landValue = 10000;
        property.incomeIncrease = 2;
        property.expenseIncrease = 3;
        property.appreciation = 4;
        property.incomeTaxRate = 25;
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
                .put(R.id.rentValue, "18000") // 1500 * 12 = 18000
                .put(R.id.vancancyValue, "900") // 18000 * 5% = 900
                .put(R.id.operatingIncomeValue, "17100") // 18000 - 900 = 17100
                .put(R.id.operatingExpensesValue, "2124") // 177*12 = 2124 (see map)
                .put(R.id.netOperatingIncomeValue, "14976") // 18000 - 900 - 2124 = 14976
                .put(R.id.mortgageValue, "9720") // 809.96 * 12 = 9719.52
                .put(R.id.cashFlowValue, "5256") // 18000 - 900 - 2124 - 9719.52 = 5256.48
                .put(R.id.afterTaxCashFlowValue, "3942") // 5256.48 * 75% = 3942.36
                .put(R.id.propertyValueValue, "150000")
                .put(R.id.loanBalanceValue, "73360") // calculated from spreadsheet: 73359.60
                .put(R.id.totalEquityValue, "76640") // 150000 - 73359.60 = 76640.40
                .put(R.id.depreciationValue, "3401") // (100000 + 3518 - 10000) / 27.5 = 3400.65
                .put(R.id.mortgageInterestValue, "3079") // calculated from spreadsheet: 3079.14
                .put(R.id.capitalizationRateValue, "15.0") // 14976 / 100000
                .put(R.id.cashOnCashValue, "14.1") // 5256 / (20000 + 3518 + 13801) = .1408
                .put(R.id.rentToValueValue, "1.0") // 1000 / 100000
                .put(R.id.grossRentMultiplierValue, "5.6") // 100000 / 18000 = 5.55
                .build();

        checkFields(activity, expectedValues);
    }
}
