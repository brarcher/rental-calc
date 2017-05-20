package protect.rentalcalc;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PropertySummaryActivity extends AppCompatActivity
{
    private static final String TAG = "RentalCalc";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.property_summary_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        DBHelper db = new DBHelper(this);

        final Bundle b = getIntent().getExtras();

        if (b == null || b.containsKey("id") == false)
        {
            Toast.makeText(this, "No property found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        final long propertyId = b.getLong("id");
        Property property = db.getProperty(propertyId);

        if (property == null)
        {
            Toast.makeText(this, "No property found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        TextView priceValue = (TextView)findViewById(R.id.priceValue);
        TextView financedValue = (TextView)findViewById(R.id.financedValue);
        TextView downPaymentValue = (TextView)findViewById(R.id.downPaymentValue);
        TextView purchaseCostsValue = (TextView)findViewById(R.id.purchaseCostsValue);
        TextView repairRemodelCostsValue = (TextView)findViewById(R.id.repairRemodelCostsValue);
        TextView totalCashNeededValue = (TextView)findViewById(R.id.totalCashNeededValue);
        TextView pricePerSizeValue = (TextView)findViewById(R.id.pricePerSizeValue);
        TextView rentValue = (TextView)findViewById(R.id.rentValue);
        TextView vancancyValue = (TextView)findViewById(R.id.vancancyValue);
        TextView operatingIncomeValue = (TextView)findViewById(R.id.operatingIncomeValue);
        TextView operatingExpensesValue = (TextView)findViewById(R.id.operatingExpensesValue);
        TextView netOperatingIncomeValue = (TextView)findViewById(R.id.netOperatingIncomeValue);
        TextView mortgageValue = (TextView)findViewById(R.id.mortgageValue);
        TextView cashFlowValue = (TextView)findViewById(R.id.cashFlowValue);
        TextView afterTaxCashFlowValue = (TextView)findViewById(R.id.afterTaxCashFlowValue);
        TextView capitalizationRateValue = (TextView)findViewById(R.id.capitalizationRateValue);
        TextView cashOnCashValue = (TextView)findViewById(R.id.cashOnCashValue);
        TextView rentToValueValue = (TextView)findViewById(R.id.rentToValueValue);
        TextView grossRentMultiplierValue = (TextView)findViewById(R.id.grossRentMultiplierValue);

        List<PropertyCalculation> calculations = CalcUtil.calculateForYears(property, 1);
        PropertyCalculation calc = calculations.get(0);

        // PURCAHSE COST

        priceValue.setText(String.format(Locale.US, "%d", property.purchasePrice));

        double downPercent;
        if(property.useLoan)
        {
            downPercent = ((double)property.downPayment)/100.0;
        }
        else
        {
            downPercent = 1.0;
        }

        double financedPercent = 1.0 - downPercent;
        double financed = property.purchasePrice * financedPercent;
        financedValue.setText(String.format(Locale.US, "%d", Math.round(financed)));

        double downPayment = downPercent * (double)property.purchasePrice;
        downPaymentValue.setText(String.format(Locale.US, "%d", Math.round(downPayment)));

        double purchaseCost = property.purchaseCosts*property.purchasePrice/100.0;
        if(property.purchaseCostsItemized.isEmpty() == false)
        {
            purchaseCost = CalcUtil.sumMapItems(property.purchaseCostsItemized);
        }

        purchaseCostsValue.setText(String.format(Locale.US, "%d", Math.round(purchaseCost)));

        int repairRemodelCosts = property.repairRemodelCosts;
        if(property.repairRemodelCostsItemized.isEmpty() == false)
        {
            repairRemodelCosts = CalcUtil.sumMapItems(property.repairRemodelCostsItemized);
        }

        repairRemodelCostsValue.setText(String.format(Locale.US, "%d", repairRemodelCosts));

        double totalCashNeeded = downPayment + purchaseCost + repairRemodelCosts;
        totalCashNeededValue.setText(String.format(Locale.US, "%d", Math.round(totalCashNeeded)));


        // VALUATION

        double pricePerSqft = 0;
        if(property.propertySqft > 0)
        {
            pricePerSqft = (double)property.purchasePrice / (double)property.propertySqft;
        }
        pricePerSizeValue.setText(String.format(Locale.US, "%d", Math.round(pricePerSqft)));


        // OPERATION
        rentValue.setText(String.format(Locale.US, "%d", (int)calc.grossRent));
        vancancyValue.setText(String.format(Locale.US, "%d", (int)Math.round(calc.vacancy)));
        operatingIncomeValue.setText(String.format(Locale.US, "%d", (int)Math.round(calc.operatingIncome)));
        operatingExpensesValue.setText(String.format(Locale.US, "%d", (int)Math.round(calc.totalExpenses)));
        netOperatingIncomeValue.setText(String.format(Locale.US, "%d", (int)Math.round(calc.netOperatingIncome)));
        mortgageValue.setText(String.format(Locale.US, "%d", (int)Math.round(calc.loanPayments)));
        cashFlowValue.setText(String.format(Locale.US, "%d", (int)Math.round(calc.cashFlow)));
        afterTaxCashFlowValue.setText(String.format(Locale.US, "%d", (int)Math.round(calc.afterTaxCashFlow)));


        // RETURNS
        capitalizationRateValue.setText(String.format(Locale.US, "%.1f", calc.capitalization));
        cashOnCashValue.setText(String.format(Locale.US, "%.1f", calc.cashOnCash));
        rentToValueValue.setText(String.format(Locale.US, "%.1f", calc.rentToValue));
        grossRentMultiplierValue.setText(String.format(Locale.US, "%.1f", calc.grossRentMultiplier));


        // Setup help texts
        Map<Integer, DictionaryItem> dictionaryLookups = new ImmutableMap.Builder<Integer, DictionaryItem>()
            .put(R.id.purchasePriceHelp, new DictionaryItem(R.string.purchasePriceHelpTitle, R.string.purchasePriceDefinition))
            .put(R.id.purchaseCostsHelp, new DictionaryItem(R.string.purchaseCostsHelpTitle, R.string.purchaseCostsDefinition))
            .put(R.id.repairRemodelCostsHelp, new DictionaryItem(R.string.repairRemodelHelpTitle, R.string.repairRemodelDefinition))
            .put(R.id.totalCashNeededHelp, new DictionaryItem(R.string.totalCashNeededTitle, R.string.totalCashNeededDefinition, R.string.totalCashNeededFormula))
            .put(R.id.grossRentHelp, new DictionaryItem(R.string.grossRentHelpTitle, R.string.grossRentDefinition))
            .put(R.id.vacancyHelp, new DictionaryItem(R.string.vacancyHelpTitle, R.string.vacancyDefinition, R.string.vacancyFormula))
            .put(R.id.operatingIncomeHelp, new DictionaryItem(R.string.operatingIncomeHelpTitle, R.string.operatingIncomeDefinition, R.string.operatingIncomeFormula))
            .put(R.id.operatingExpensesHelp, new DictionaryItem(R.string.operatingExpensesHelpTitle, R.string.operatingExpensesDefinition))
            .put(R.id.netOperatingIncomeHelp, new DictionaryItem(R.string.netOperatingIncomeHelpTitle, R.string.netOperatingIncomeDefinition, R.string.netOperatingIncomeFormula))
            .put(R.id.cashFlowHelp, new DictionaryItem(R.string.cashFlowHelpTitle, R.string.cashFlowDefinition, R.string.cashFlowFormula))
            .put(R.id.capitalizationRateHelp, new DictionaryItem(R.string.capitalizationRateHelpTitle, R.string.capitalizationRateDefinition, R.string.capitalizationRateFormula))
            .put(R.id.cashOnCashHelp, new DictionaryItem(R.string.cashOnCashHelpTitle, R.string.cashOnCashDefinition, R.string.cashOnCashFormula))
            .put(R.id.rentToValueHelp, new DictionaryItem(R.string.rentToValueHelpTitle, R.string.rentToValueDefinition, R.string.rentToValueFormula))
            .put(R.id.grossRentMultiplierHelp, new DictionaryItem(R.string.grossRentMultiplierHelpTitle, R.string.grossRentMultiplierDefinition, R.string.grossRentMultiplierFormula))
            .build();

        for(final Map.Entry<Integer, DictionaryItem> entry : dictionaryLookups.entrySet())
        {
            View view = findViewById(entry.getKey());
            view.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    DictionaryItem info = entry.getValue();
                    final Bundle bundle = new Bundle();
                    bundle.putInt("title", info.titleId);
                    bundle.putInt("definition", info.definitionId);
                    if(info.formulaId != null)
                    {
                        bundle.putInt("formula", info.formulaId);
                    }
                    Intent i = new Intent(getApplicationContext(), DictionaryActivity.class);
                    i.putExtras(bundle);
                    startActivity(i);
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if(id == android.R.id.home)
        {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
