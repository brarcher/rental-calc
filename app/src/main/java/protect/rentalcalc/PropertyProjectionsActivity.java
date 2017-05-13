package protect.rentalcalc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PropertyProjectionsActivity extends AppCompatActivity
{
    private static final String TAG = "RentalCalc";

    private TextView _rentValue;
    private TextView _vancancyValue;
    private TextView _operatingIncomeValue;
    private TextView _operatingExpensesValue;
    private TextView _netOperatingIncomeValue;
    private TextView _mortgageValue;
    private TextView _cashFlowValue;
    private TextView _afterTaxCashFlowValue;
    private TextView _propertyValueValue;
    private TextView _loanBalanceValue;
    private TextView _totalEquityValue;
    private TextView _depreciationValue;
    private TextView _mortgageInterestValue;
    private TextView _capitalizationRateValue;
    private TextView _cashOnCashValue;
    private TextView _rentToValueValue;
    private TextView _grossRentMultiplierValue;
    private TextView _projectionText;
    private SeekBar _yearSeeker;
    private List<PropertyCalculation> _calculations;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.property_projections_layout);
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

        _calculations = CalcUtil.calculateForYears(property, 40);

        _rentValue = (TextView)findViewById(R.id.rentValue);
        _vancancyValue = (TextView)findViewById(R.id.vancancyValue);
        _operatingIncomeValue = (TextView)findViewById(R.id.operatingIncomeValue);
        _operatingExpensesValue = (TextView)findViewById(R.id.operatingExpensesValue);
        _netOperatingIncomeValue = (TextView)findViewById(R.id.netOperatingIncomeValue);
        _mortgageValue = (TextView)findViewById(R.id.mortgageValue);
        _cashFlowValue = (TextView)findViewById(R.id.cashFlowValue);
        _afterTaxCashFlowValue = (TextView)findViewById(R.id.afterTaxCashFlowValue);
        _propertyValueValue = (TextView)findViewById(R.id.propertyValueValue);
        _loanBalanceValue = (TextView)findViewById(R.id.loanBalanceValue);
        _totalEquityValue = (TextView)findViewById(R.id.totalEquityValue);
        _depreciationValue = (TextView)findViewById(R.id.depreciationValue);
        _mortgageInterestValue = (TextView)findViewById(R.id.mortgageInterestValue);
        _propertyValueValue = (TextView)findViewById(R.id.propertyValueValue);
        _loanBalanceValue = (TextView)findViewById(R.id.loanBalanceValue);
        _totalEquityValue = (TextView)findViewById(R.id.totalEquityValue);
        _depreciationValue = (TextView)findViewById(R.id.depreciationValue);
        _mortgageInterestValue = (TextView)findViewById(R.id.mortgageInterestValue);
        _capitalizationRateValue = (TextView)findViewById(R.id.capitalizationRateValue);
        _cashOnCashValue = (TextView)findViewById(R.id.cashOnCashValue);
        _rentToValueValue = (TextView)findViewById(R.id.rentToValueValue);
        _grossRentMultiplierValue = (TextView)findViewById(R.id.grossRentMultiplierValue);

        _projectionText = (TextView)findViewById(R.id.projectionText);
        updateDisplay(0);

        _yearSeeker = (SeekBar)findViewById(R.id.yearSeeker);
        _yearSeeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int position, boolean fromUser)
            {
                Log.d(TAG, "Updating projection year to " + position);
                updateDisplay(position);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {
                // noop
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                // noop
            }
        });

        // Setup help texts
        Map<Integer, DictionaryItem> dictionaryLookups = new ImmutableMap.Builder<Integer, DictionaryItem>()
                .put(R.id.grossRentHelp, new DictionaryItem(R.string.grossRentHelpTitle, R.string.grossRentDefinition))
                .put(R.id.vacancyHelp, new DictionaryItem(R.string.vacancyHelpTitle, R.string.vacancyDefinition, R.string.vacancyFormula))
                .put(R.id.operatingIncomeHelp, new DictionaryItem(R.string.operatingIncomeHelpTitle, R.string.operatingIncomeDefinition, R.string.operatingIncomeFormula))
                .put(R.id.operatingExpensesHelp, new DictionaryItem(R.string.operatingExpensesHelpTitle, R.string.operatingExpensesDefinition))
                .put(R.id.netOperatingIncomeHelp, new DictionaryItem(R.string.netOperatingIncomeHelpTitle, R.string.netOperatingIncomeDefinition, R.string.netOperatingIncomeFormula))
                .put(R.id.cashFlowHelp, new DictionaryItem(R.string.cashFlowHelpTitle, R.string.cashFlowDefinition, R.string.cashFlowFormula))
                .put(R.id.propertyValueHelp, new DictionaryItem(R.string.propertyValueHelpHelpTitle, R.string.propertyValueHelpDefinition))
                .put(R.id.totalEquityHelp, new DictionaryItem(R.string.totalEquityHelpTitle, R.string.totalEquityHelpDefinition, R.string.totalEquityHelpFormula))
                .put(R.id.depreciationHelp, new DictionaryItem(R.string.depreciationHelpTitle, R.string.depreciationHelpDefinition))
                .put(R.id.mortgageInterestHelp, new DictionaryItem(R.string.mortgageInterestHelpTitle, R.string.mortgageInterestHelpDefinition))
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

    private void updateDisplay(int position)
    {
        // The passed value starts a 0, but we count the years starting at 1
        int year = position+1;

        PropertyCalculation calc = _calculations.get(position);

        // OPERATION
        _rentValue.setText(String.format(Locale.US, "%d", (int)Math.round(calc.grossRent)));
        _vancancyValue.setText(String.format(Locale.US, "%d", (int)Math.round(calc.vacancy)));
        _operatingIncomeValue.setText(String.format(Locale.US, "%d", (int)Math.round(calc.operatingIncome)));
        _operatingExpensesValue.setText(String.format(Locale.US, "%d", (int)Math.round(calc.totalExpenses)));
        _netOperatingIncomeValue.setText(String.format(Locale.US, "%d", (int)Math.round(calc.netOperatingIncome)));
        _mortgageValue.setText(String.format(Locale.US, "%d", (int)Math.round(calc.loanPayments)));
        _cashFlowValue.setText(String.format(Locale.US, "%d", (int)Math.round(calc.cashFlow)));
        _afterTaxCashFlowValue.setText(String.format(Locale.US, "%d", (int)Math.round(calc.afterTaxCashFlow)));

        // EQUITY
        _propertyValueValue.setText(String.format(Locale.US, "%d", (int)Math.round(calc.propertyValue)));
        _loanBalanceValue.setText(String.format(Locale.US, "%d",  (int)Math.round(calc.loanBalance)));
        _totalEquityValue.setText(String.format(Locale.US, "%d",  (int)Math.round(calc.totalEquity)));

        // TAXES
        _depreciationValue.setText(String.format(Locale.US, "%d",  (int)Math.round(calc.depreciation)));
        _mortgageInterestValue.setText(String.format(Locale.US, "%d", (int)Math.round(calc.loanInterest)));

        // RETURNS
        _capitalizationRateValue.setText(String.format(Locale.US, "%.1f", calc.capitalization));
        _cashOnCashValue.setText(String.format(Locale.US, "%.1f", calc.cashOnCash));
        _rentToValueValue.setText(String.format(Locale.US, "%.1f", calc.rentToValue));
        _grossRentMultiplierValue.setText(String.format(Locale.US, "%.1f", calc.grossRentMultiplier));

        _projectionText.setText(String.format(getString(R.string.projectionText), year));
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
