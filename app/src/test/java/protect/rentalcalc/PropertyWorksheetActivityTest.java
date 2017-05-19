package protect.rentalcalc;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.common.collect.ImmutableList;
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
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.ShadowToast;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static protect.rentalcalc.TestHelper.checkIntField;
import static protect.rentalcalc.TestHelper.checkDoubleField;
import static protect.rentalcalc.TestHelper.preloadProperty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;
import static protect.rentalcalc.TestHelper.preloadPropertyWithItemize;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class PropertyWorksheetActivityTest
{
    private DBHelper db;
    private final HashMap<String, Integer> EMPTY_MAP = new HashMap<>();
    private final HashMap<String, Integer> FILLED_MAP = new HashMap<>
        (new ImmutableMap.Builder<String, Integer>()
            .put("test value 1", 12345)
            .put("test value 2", 23456)
            .put("test value 3", 34567)
            .put("test value 4", 45678)
            .put("test value 5", 56789)
            .put("test value 6", 67890)
            .build()
        );
    private final HashMap<String, Integer> FILLED_MAP2 = new HashMap<>
        (new ImmutableMap.Builder<String, Integer>()
                .put("second test value 1", 12345)
                .put("second test value 2", 23456)
                .put("second test value 3", 34567)
                .put("second test value 4", 45678)
                .build()
        );

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

        assertTrue(shadowOf(activity).isFinishing() == false);
        shadowOf(activity).clickMenuItem(android.R.id.home);
        assertTrue(shadowOf(activity).isFinishing());

        // Check that can finish out the lifecycle
        controller.pause();
        controller.stop();
        controller.destroy();
    }

    @Test
    public void checkActionBar() throws Exception
    {
        ActivityController controller = startWithProperty(new Property());
        Activity activity = (Activity)controller.get();

        final Menu menu = shadowOf(activity).getOptionsMenu();
        assertNotNull(menu);

        assertEquals(menu.size(), 1);

        MenuItem item = menu.findItem(R.id.action_save);
        assertNotNull(item);
        assertEquals("Save", item.getTitle().toString());
    }

    @Test
    public void startWithoutBundle()
    {
        ActivityController controller = Robolectric.buildActivity(PropertyWorksheetActivity.class).create();
        Activity activity = (Activity)controller.get();

        controller.start();
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

        ActivityController controller = Robolectric.buildActivity(PropertyWorksheetActivity.class, intent).create();
        Activity activity = (Activity)controller.get();

        controller.start();
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

        ActivityController controller = Robolectric.buildActivity(PropertyWorksheetActivity.class, intent).create();
        Activity activity = (Activity)controller.get();

        controller.start();
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

        ActivityController controller = Robolectric.buildActivity(PropertyWorksheetActivity.class, intent).create();
        Activity activity = (Activity)controller.get();

        controller.start();
        controller.visible();
        controller.resume();
        assertTrue(activity.isFinishing() == false);

        String latestToast = ShadowToast.getTextOfLatestToast();
        assertNull(latestToast);

        return controller;
    }

    private void checkFields(Activity activity, Property property)
    {
        EditText price = (EditText)activity.findViewById(R.id.price);
        EditText afterRepairsValue = (EditText)activity.findViewById(R.id.afterRepairsValue);
        ToggleButton financing = (ToggleButton)activity.findViewById(R.id.financing);
        EditText downPayment = (EditText)activity.findViewById(R.id.downPayment);
        EditText interestRate = (EditText)activity.findViewById(R.id.interestRate);
        EditText loanDuration = (EditText)activity.findViewById(R.id.loanDuration);
        EditText purchaseCost = (EditText)activity.findViewById(R.id.purchaseCost);
        EditText repairCost = (EditText)activity.findViewById(R.id.repairCost);
        EditText rent = (EditText)activity.findViewById(R.id.rent);
        EditText otherIncome = (EditText)activity.findViewById(R.id.otherIncome);
        EditText totalExpenses = (EditText)activity.findViewById(R.id.totalExpenses);
        EditText vacancy = (EditText)activity.findViewById(R.id.vacancy);
        EditText appreciation = (EditText)activity.findViewById(R.id.appreciation);
        EditText incomeIncrease = (EditText)activity.findViewById(R.id.incomeIncrease);
        EditText expensesIncrease = (EditText)activity.findViewById(R.id.expensesIncrease);
        EditText sellingCosts = (EditText)activity.findViewById(R.id.sellingCosts);
        EditText landValue = (EditText)activity.findViewById(R.id.landValue);
        EditText incomeTaxRate = (EditText)activity.findViewById(R.id.incomeTaxRate);
        TextView purchaseCostItemize = (TextView)activity.findViewById(R.id.purchaseCostItemize);
        TextView repairCostItemize = (TextView)activity.findViewById(R.id.repairCostItemize);
        TextView totalExpensesItemize = (TextView)activity.findViewById(R.id.totalExpensesItemize);

        checkIntField(property.purchasePrice, price.getText().toString());
        checkIntField(property.afterRepairsValue, afterRepairsValue.getText().toString());
        assertEquals(property.useLoan, financing.isChecked());
        checkIntField(property.downPayment, downPayment.getText().toString());
        checkDoubleField(property.interestRate, interestRate.getText().toString());
        checkIntField(property.loanDuration, loanDuration.getText().toString());
        checkIntField(property.purchaseCosts, purchaseCost.getText().toString());
        checkIntField(property.purchaseCosts, purchaseCost.getText().toString());
        checkIntField(property.repairRemodelCosts, repairCost.getText().toString());
        checkIntField(property.grossRent, rent.getText().toString());
        checkIntField(property.otherIncome, otherIncome.getText().toString());
        checkIntField(property.expenses, totalExpenses.getText().toString());
        checkIntField(property.vacancy, vacancy.getText().toString());
        checkIntField(property.appreciation, appreciation.getText().toString());
        checkIntField(property.incomeIncrease, incomeIncrease.getText().toString());
        checkIntField(property.expenseIncrease, expensesIncrease.getText().toString());
        checkIntField(property.sellingCosts, sellingCosts.getText().toString());
        checkIntField(property.landValue, landValue.getText().toString());
        checkIntField(property.incomeTaxRate, incomeTaxRate.getText().toString());
        checkIntField(CalcUtil.sumMapItems(property.purchaseCostsItemized), purchaseCostItemize.getText().toString());
        checkIntField(CalcUtil.sumMapItems(property.repairRemodelCostsItemized), repairCostItemize.getText().toString());
        checkIntField(CalcUtil.sumMapItems(property.expensesItemized), totalExpensesItemize.getText().toString());
    }

    private void setFields(Activity activity, Property property)
    {
        EditText price = (EditText)activity.findViewById(R.id.price);
        EditText afterRepairsValue = (EditText)activity.findViewById(R.id.afterRepairsValue);
        ToggleButton financing = (ToggleButton)activity.findViewById(R.id.financing);
        EditText downPayment = (EditText)activity.findViewById(R.id.downPayment);
        EditText interestRate = (EditText)activity.findViewById(R.id.interestRate);
        EditText loanDuration = (EditText)activity.findViewById(R.id.loanDuration);
        EditText purchaseCost = (EditText)activity.findViewById(R.id.purchaseCost);
        EditText repairCost = (EditText)activity.findViewById(R.id.repairCost);
        EditText rent = (EditText)activity.findViewById(R.id.rent);
        EditText otherIncome = (EditText)activity.findViewById(R.id.otherIncome);
        EditText totalExpenses = (EditText)activity.findViewById(R.id.totalExpenses);
        EditText vacancy = (EditText)activity.findViewById(R.id.vacancy);
        EditText appreciation = (EditText)activity.findViewById(R.id.appreciation);
        EditText incomeIncrease = (EditText)activity.findViewById(R.id.incomeIncrease);
        EditText expensesIncrease = (EditText)activity.findViewById(R.id.expensesIncrease);
        EditText sellingCosts = (EditText)activity.findViewById(R.id.sellingCosts);
        EditText landValue = (EditText)activity.findViewById(R.id.landValue);
        EditText incomeTaxRate = (EditText)activity.findViewById(R.id.incomeTaxRate);

        price.setText(String.format(Locale.US, "%d", property.purchasePrice));
        afterRepairsValue.setText(String.format(Locale.US, "%d", property.afterRepairsValue));
        financing.setChecked(property.useLoan);
        downPayment.setText(String.format(Locale.US, "%d", property.downPayment));
        interestRate.setText(String.format(Locale.US, "%.2f", property.interestRate));
        loanDuration.setText(String.format(Locale.US, "%d", property.loanDuration));
        purchaseCost.setText(String.format(Locale.US, "%d", property.purchaseCosts));
        repairCost.setText(String.format(Locale.US, "%d", property.repairRemodelCosts));
        rent.setText(String.format(Locale.US, "%d", property.grossRent));
        otherIncome.setText(String.format(Locale.US, "%d", property.otherIncome));
        totalExpenses.setText(String.format(Locale.US, "%d", property.expenses));
        vacancy.setText(String.format(Locale.US, "%d", property.vacancy));
        appreciation.setText(String.format(Locale.US, "%d", property.appreciation));
        incomeIncrease.setText(String.format(Locale.US, "%d", property.incomeIncrease));
        expensesIncrease.setText(String.format(Locale.US, "%d", property.expenseIncrease));
        sellingCosts.setText(String.format(Locale.US, "%d", property.sellingCosts));
        landValue.setText(String.format(Locale.US, "%d", property.landValue));
        incomeTaxRate.setText(String.format(Locale.US, "%d", property.incomeTaxRate));
    }

    @Test
    public void startWithPropertyNotItemizeCheckInitialLoad() throws Exception
    {
        Property property = new Property();
        preloadProperty(property);

        ActivityController controller = startWithProperty(property);
        Activity activity = (Activity)controller.get();

        checkFields(activity, property);
    }

    @Test
    public void startWithPropertyWithItemizeCheckInitialLoad() throws Exception
    {
        Property property = new Property();
        preloadPropertyWithItemize(property);

        ActivityController controller = startWithProperty(property);
        Activity activity = (Activity)controller.get();

        checkFields(activity, property);
    }

    @Test
    public void startWithPropertyNotItemizeChangeValues() throws Exception
    {
        Property property = new Property();
        preloadProperty(property);

        ActivityController controller = startWithProperty(property);
        Activity activity = (Activity)controller.get();

        checkFields(activity, property);

        property = new Property();
        setFields(activity, property);

        // Check that the values are still valid after a pause and resume.
        controller.pause();
        controller.resume();

        checkFields(activity, property);
    }

    private void compareRelevantPropertyFields(Property a, Property b)
    {
        assertEquals(a.purchasePrice, b.purchasePrice);
        assertEquals(a.afterRepairsValue, b.afterRepairsValue);
        assertEquals(a.useLoan, b.useLoan);
        assertEquals(a.downPayment, b.downPayment);
        assertEquals(a.interestRate, b.interestRate, 0.01);
        assertEquals(a.loanDuration, b.loanDuration);
        assertEquals(a.purchaseCosts, b.purchaseCosts);
        assertEquals(a.repairRemodelCosts, b.repairRemodelCosts);
        assertEquals(a.grossRent, b.grossRent);
        assertEquals(a.otherIncome, b.otherIncome);
        assertEquals(a.expenses, b.expenses);
        assertEquals(a.vacancy, b.vacancy);
        assertEquals(a.appreciation, b.appreciation);
        assertEquals(a.incomeIncrease, b.incomeIncrease);
        assertEquals(a.expenseIncrease, b.expenseIncrease);
        assertEquals(a.sellingCosts, b.sellingCosts);
        assertEquals(a.landValue, b.landValue);
        assertEquals(a.incomeTaxRate, b.incomeTaxRate);
        assertEquals(a.purchaseCostsItemized, b.purchaseCostsItemized);
        assertEquals(a.repairRemodelCostsItemized, b.repairRemodelCostsItemized);
        assertEquals(a.expensesItemized, b.expensesItemized);
    }

    @Test
    public void startWithPropertyNotItemizeAndSave() throws Exception
    {
        Property property = new Property();

        ActivityController controller = startWithProperty(property);
        Activity activity = (Activity)controller.get();

        checkFields(activity, property);

        preloadProperty(property);
        setFields(activity, property);
        checkFields(activity, property);

        assertNotNull(shadowOf(activity).getOptionsMenu().findItem(R.id.action_save));
        shadowOf(activity).clickMenuItem(R.id.action_save);

        assertTrue(activity.isFinishing());

        Property updatedProperty = db.getProperty(DatabaseTestHelper.FIRST_ID);

        compareRelevantPropertyFields(property, updatedProperty);
    }

    @Test
    public void startWithPropertyNotItemizeAndCancel() throws Exception
    {
        Property property = new Property();

        ActivityController controller = startWithProperty(property);
        Activity activity = (Activity)controller.get();

        checkFields(activity, property);

        preloadProperty(property);
        setFields(activity, property);
        checkFields(activity, property);

        shadowOf(activity).clickMenuItem(android.R.id.home);
        assertTrue(activity.isFinishing());

        Property updatedProperty = db.getProperty(DatabaseTestHelper.FIRST_ID);

        compareRelevantPropertyFields(new Property(), updatedProperty);
    }

    private void checkOpenActivity(ActivityController controller, int viewId, HashMap<String, Integer> items)
    {
        Activity activity = (Activity)controller.get();
        activity.findViewById(viewId).performClick();

        assertTrue(activity.isFinishing() == false);

        ShadowActivity.IntentForResult next = shadowOf(activity).getNextStartedActivityForResult();

        ComponentName componentName = next.intent.getComponent();
        String name = componentName.flattenToShortString();
        assertEquals("protect.rentalcalc/.ItemizeActivity", name);

        Bundle extras = next.intent.getExtras();
        assertNotNull(extras);
        assertTrue(extras.containsKey("title"));
        assertTrue(extras.getInt("title") > 0);
        assertTrue(extras.containsKey("description"));
        assertTrue(extras.getInt("description") > 0);
        assertTrue(extras.containsKey("items"));
        assertEquals(items, extras.getSerializable("items"));

        // As the next activity is started, this activity is paused
        controller.pause();
    }

    @Test
    public void launchItemizations() throws Exception
    {
        Property property = new Property();
        ActivityController controller = startWithProperty(property);
        Activity activity = (Activity)controller.get();
        ShadowActivity shadowActivity = shadowOf(activity);

        Map<Integer, String> nameLookup = ImmutableMap.of
        (
            R.id.purchaseCostsItemize, "purchaseCostsItemize",
            R.id.repairCostsItemize, "repairCostsItemize",
            R.id.expensesItemize, "expensesItemize"
        );

        List<Pair<Integer, HashMap<String, Integer>>> tests = new ImmutableList.Builder<Pair<Integer, HashMap<String, Integer>>>()
                .add(new Pair<>(R.id.purchaseCostsItemize, property.purchaseCostsItemized))
                .add(new Pair<>(R.id.repairCostsItemize, property.repairRemodelCostsItemized))
                .add(new Pair<>(R.id.expensesItemize, property.expensesItemized))
                .build();

        for(Pair<Integer, HashMap<String, Integer>> test : tests)
        {
            int viewId = test.first;
            HashMap<String, Integer> propertyMap = test.second;

            Log.d("Test", "Test on " + nameLookup.get(viewId));


            // Test: empty map, launch, cancel, map still empty

            checkOpenActivity(controller, viewId, EMPTY_MAP);

            shadowActivity.receiveResult(new Intent(RuntimeEnvironment.application, ItemizeActivity.class), Activity.RESULT_CANCELED, null);

            // Now that the new activity has finished, this activity resumes
            controller.resume();

            // No items should have been added
            checkFields(activity, property);


            // Test: empty map, launch, returned empty, map becomes empty

            checkOpenActivity(controller, viewId, EMPTY_MAP);

            Bundle bundle = new Bundle();
            bundle.putSerializable("items", EMPTY_MAP);
            shadowActivity.receiveResult(new Intent(RuntimeEnvironment.application, ItemizeActivity.class), Activity.RESULT_OK, new Intent().putExtras(bundle));

            // Now that the new activity has finished, this activity resumes
            controller.resume();

            // No items
            checkFields(activity, property);


            // Test: empty map, launch, returned items, map now has items

            checkOpenActivity(controller, viewId, EMPTY_MAP);

            bundle = new Bundle();
            bundle.putSerializable("items", FILLED_MAP);
            shadowActivity.receiveResult(new Intent(RuntimeEnvironment.application, ItemizeActivity.class), Activity.RESULT_OK, new Intent().putExtras(bundle));

            // Now that the new activity has finished, this activity resumes
            controller.resume();

            // Now has items
            propertyMap.putAll(FILLED_MAP);
            checkFields(activity, property);


            // Test: filled map, launch, return items, map has different

            checkOpenActivity(controller, viewId, FILLED_MAP);

            bundle = new Bundle();
            bundle.putSerializable("items", FILLED_MAP2);
            shadowActivity.receiveResult(new Intent(RuntimeEnvironment.application, ItemizeActivity.class), Activity.RESULT_OK, new Intent().putExtras(bundle));

            // Now that the new activity has finished, this activity resumes
            controller.resume();

            // Now has different items
            propertyMap.clear();
            propertyMap.putAll(FILLED_MAP2);
            checkFields(activity, property);


            // Test: filled map, launch, cancel, items remain

            checkOpenActivity(controller, viewId, FILLED_MAP2);

            shadowActivity.receiveResult(new Intent(RuntimeEnvironment.application, ItemizeActivity.class), Activity.RESULT_CANCELED, null);

            // Now that the new activity has finished, this activity resumes
            controller.resume();

            // Still has same items
            checkFields(activity, property);
        }
    }
}
