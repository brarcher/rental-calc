package protect.rentalcalc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

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

import java.util.Locale;

import static protect.rentalcalc.TestHelper.checkIntField;
import static protect.rentalcalc.TestHelper.checkDoubleField;
import static protect.rentalcalc.TestHelper.preloadProperty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class PropertyWorksheetActivityTest
{
    private DBHelper db;

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
        ActivityController controller = Robolectric.buildActivity(PropertyWorksheetActivity.class).create();
        Activity activity = (Activity)controller.get();

        controller.start();
        controller.visible();
        controller.resume();

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
        ActivityController controller = Robolectric.buildActivity(PropertyWorksheetActivity.class).create();
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

        ActivityController controller = Robolectric.buildActivity(PropertyWorksheetActivity.class, intent).create();
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

        ActivityController controller = Robolectric.buildActivity(PropertyWorksheetActivity.class, intent).create();
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
    }

    @Test
    public void startWithPropertyCheckInitialLoad() throws Exception
    {
        Property property = new Property();
        preloadProperty(property);

        ActivityController controller = startWithProperty(property);
        Activity activity = (Activity)controller.get();

        checkFields(activity, property);
    }

    @Test
    public void startWithPropertyChangeValues() throws Exception
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
    }

    @Test
    public void startWithPropertyAndSave() throws Exception
    {
        Property property = new Property();

        ActivityController controller = startWithProperty(property);
        Activity activity = (Activity)controller.get();

        checkFields(activity, property);

        preloadProperty(property);
        setFields(activity, property);
        checkFields(activity, property);

        Button saveButton = (Button)activity.findViewById(R.id.saveButton);
        saveButton.performClick();

        assertTrue(activity.isFinishing());

        Property updatedProperty = db.getProperty(DatabaseTestHelper.FIRST_ID);

        compareRelevantPropertyFields(property, updatedProperty);
    }
}
