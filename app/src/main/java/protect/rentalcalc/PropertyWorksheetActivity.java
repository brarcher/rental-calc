package protect.rentalcalc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class PropertyWorksheetActivity extends AppCompatActivity
{
    private static final String TAG = "RentalCalc";

    private static final int FOR_RESULT_ITEMIZE = 1;
    private Map<String, Integer> _currentForResultItemize;

    private DBHelper _db;

    private EditText _price;
    private EditText _afterRepairsValue;
    private ToggleButton _financing;
    private EditText _downPayment;
    private EditText _interestRate;
    private EditText _loanDuration;
    private EditText _purchaseCost;
    private View _purchaseCostPercentageLayout;
    private TextView _purchaseCostItemize;
    private View _purchaseCostItemizeCurrency;
    private EditText _repairCost;
    private TextView _repairCostItemize;
    private EditText _rent;
    private EditText _otherIncome;
    private EditText _totalExpenses;
    private View _totalExpensesPercentLayout;
    private View _totalExpensesItemizeLayout;
    private TextView _totalExpensesItemizeCurrency;
    private TextView _totalExpensesItemize;
    private EditText _vacancy;
    private EditText _appreciation;
    private EditText _incomeIncrease;
    private EditText _expensesIncrease;
    private EditText _sellingCosts;
    private EditText _landValue;
    private EditText _incomeTaxRate;

    private Property _property;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.property_worksheet_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        _db = new DBHelper(this);

        final Bundle b = getIntent().getExtras();

        if(b == null || b.containsKey("id") == false)
        {
            Toast.makeText(this, "No property found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        final long propertyId = b.getLong("id");
        _property = _db.getProperty(propertyId);

        if(_property == null)
        {
            Toast.makeText(this, "No property found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        _price = (EditText)findViewById(R.id.price);
        _afterRepairsValue = (EditText)findViewById(R.id.afterRepairsValue);
        _financing = (ToggleButton)findViewById(R.id.financing);
        _downPayment = (EditText)findViewById(R.id.downPayment);
        _interestRate = (EditText)findViewById(R.id.interestRate);
        _loanDuration = (EditText)findViewById(R.id.loanDuration);
        _purchaseCost = (EditText)findViewById(R.id.purchaseCost);
        _purchaseCostItemize = (TextView)findViewById(R.id.purchaseCostItemize);
        _purchaseCostPercentageLayout = findViewById(R.id.purchaseCostPercentageLayout);
        _purchaseCostItemizeCurrency = findViewById(R.id.purchaseCostItemizeCurrency);
        _repairCost = (EditText)findViewById(R.id.repairCost);
        _repairCostItemize = (TextView)findViewById(R.id.repairCostItemize);
        _rent = (EditText)findViewById(R.id.rent);
        _otherIncome = (EditText)findViewById(R.id.otherIncome);
        _totalExpenses = (EditText)findViewById(R.id.totalExpenses);
        _totalExpensesPercentLayout = findViewById(R.id.totalExpensesPercentLayout);
        _totalExpensesItemizeLayout = findViewById(R.id.totalExpensesItemizeLayout);
        _totalExpensesItemizeCurrency = (TextView)findViewById(R.id.totalExpensesItemizeCurrency);
        _totalExpensesItemize = (TextView)findViewById(R.id.totalExpensesItemize);
        _vacancy = (EditText)findViewById(R.id.vacancy);
        _appreciation = (EditText)findViewById(R.id.appreciation);
        _incomeIncrease = (EditText)findViewById(R.id.incomeIncrease);
        _expensesIncrease = (EditText)findViewById(R.id.expensesIncrease);
        _sellingCosts = (EditText)findViewById(R.id.sellingCosts);
        _landValue = (EditText)findViewById(R.id.landValue);
        _incomeTaxRate = (EditText)findViewById(R.id.incomeTaxRate);

        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                List<View> views = ImmutableList.of(
                    findViewById(R.id.loanBorder),
                    findViewById(R.id.loanRow),
                    findViewById(R.id.interestBorder),
                    findViewById(R.id.interestRow),
                    findViewById(R.id.downPaymentBorder),
                    findViewById(R.id.downPaymentRow)
                );

                for(View view : views)
                {
                    view.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                }
            }
        };
        _financing.setOnCheckedChangeListener(listener);

        _price.setText(String.format(Locale.US, "%d", _property.purchasePrice));
        _afterRepairsValue.setText(String.format(Locale.US, "%d", _property.afterRepairsValue));
        _financing.setChecked(_property.useLoan);
        listener.onCheckedChanged(_financing, _property.useLoan);
        _downPayment.setText(String.format(Locale.US, "%d", _property.downPayment));
        _interestRate.setText(String.format(Locale.US, "%.3f", _property.interestRate));
        _loanDuration.setText(String.format(Locale.US, "%d", _property.loanDuration));
        _purchaseCost.setText(String.format(Locale.US, "%d", _property.purchaseCosts));
        _repairCost.setText(String.format(Locale.US, "%d", _property.repairRemodelCosts));
        _rent.setText(String.format(Locale.US, "%d", _property.grossRent));
        _otherIncome.setText(String.format(Locale.US, "%d", _property.otherIncome));
        _totalExpenses.setText(String.format(Locale.US, "%d", _property.expenses));
        _vacancy.setText(String.format(Locale.US, "%d", _property.vacancy));
        _appreciation.setText(String.format(Locale.US, "%d", _property.appreciation));
        _incomeIncrease.setText(String.format(Locale.US, "%d", _property.incomeIncrease));
        _expensesIncrease.setText(String.format(Locale.US, "%d", _property.expenseIncrease));
        _sellingCosts.setText(String.format(Locale.US, "%d", _property.sellingCosts));
        _landValue.setText(String.format(Locale.US, "%d", _property.landValue));
        _incomeTaxRate.setText(String.format(Locale.US, "%d", _property.incomeTaxRate));

        // Once the price is set, if the after repairs value is not yet populated,
        // fill it in with the price. It is a good first guess.
        _price.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if(hasFocus == false)
                {
                    String price = _price.getText().toString();
                    if(price.isEmpty() == false && price.equals("0") == false &&
                            _afterRepairsValue.getText().toString().equals("0"))
                    {
                        _afterRepairsValue.setText(price);
                    }
                }
            }
        });

        // Setup help texts
        Map<Integer, DictionaryItem> dictionaryLookups = new ImmutableMap.Builder<Integer, DictionaryItem>()
            .put(R.id.purchasePriceHelp, new DictionaryItem(R.string.purchasePriceHelpTitle, R.string.purchasePriceDefinition))
            .put(R.id.afterRepairsHelp, new DictionaryItem(R.string.afterRepairsValueHelpTitle, R.string.afterRepairsValueDefinition))
            .put(R.id.purchaseCostsHelp, new DictionaryItem(R.string.purchaseCostsHelpTitle, R.string.purchaseCostsDefinition))
            .put(R.id.repairRemodelCostsHelp, new DictionaryItem(R.string.repairRemodelHelpTitle, R.string.repairRemodelDefinition))
            .put(R.id.grossRentHelp, new DictionaryItem(R.string.grossRentHelpTitle, R.string.grossRentDefinition))
            .put(R.id.otherIncomeHelp, new DictionaryItem(R.string.otherIncomeHelpTitle, R.string.otherIncomeDefinition))
            .put(R.id.expensesHelp, new DictionaryItem(R.string.operatingExpensesHelpTitle, R.string.operatingExpensesDefinition))
            .put(R.id.vacancyHelp, new DictionaryItem(R.string.vacancyHelpTitle, R.string.vacancyDefinition, R.string.vacancyFormula))
            .put(R.id.appreciationHelp, new DictionaryItem(R.string.appreciationHelpTitle, R.string.appreciationDefinition))
            .put(R.id.incomeIncreaseHelp, new DictionaryItem(R.string.incomeIncreaseHelpTitle, R.string.incomeIncreaseDefinition))
            .put(R.id.expensesIncreaseHelp, new DictionaryItem(R.string.expensesIncreaseHelpTitle, R.string.expensesIncreaseDefinition))
            .put(R.id.sellingCostsHelp, new DictionaryItem(R.string.sellingCostsHelpTitle, R.string.sellingCostsDefinition))
            .put(R.id.landValueHelp, new DictionaryItem(R.string.landValueHelpTitle, R.string.landValueDefinition))
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

        // Setup itemize activity links
        Map<Integer, ItemizeOptionItem> itemizeOptionLookups = new ImmutableMap.Builder<Integer, ItemizeOptionItem>()
            .put(R.id.purchaseCostsItemize, new ItemizeOptionItem(R.string.purchaseCostsItemizeTitle, R.string.purchaseCostsItemizeHelp, _property.purchaseCostsItemized))
            .put(R.id.repairCostsItemize, new ItemizeOptionItem(R.string.repairCostsItemizeTitle, R.string.repairCostsItemizeHelp, _property.repairRemodelCostsItemized))
            .put(R.id.expensesItemize, new ItemizeOptionItem(R.string.expensesItemizeTitle, R.string.expensesItemizeHelp, _property.expensesItemized))
            .build();

        for(final Map.Entry<Integer, ItemizeOptionItem> entry : itemizeOptionLookups.entrySet())
        {
            View view = findViewById(entry.getKey());
            view.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ItemizeOptionItem itemizations = entry.getValue();

                    _currentForResultItemize = itemizations.items;

                    Bundle bundle = new Bundle();
                    bundle.putInt("title", itemizations.titleId);
                    bundle.putInt("description", itemizations.descriptionId);
                    bundle.putSerializable("items", itemizations.items);
                    Intent i = new Intent(getApplicationContext(), ItemizeActivity.class);
                    i.putExtras(bundle);
                    startActivityForResult(i, FOR_RESULT_ITEMIZE);
                }
            });
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        _purchaseCostPercentageLayout.setVisibility(_property.purchaseCostsItemized.isEmpty() ? View.VISIBLE : View.GONE);
        _purchaseCostItemizeCurrency.setVisibility(_property.purchaseCostsItemized.isEmpty() ? View.GONE : View.VISIBLE);
        _purchaseCostItemize.setVisibility(_property.purchaseCostsItemized.isEmpty() ? View.GONE : View.VISIBLE);
        int purchaseCostItemized = CalcUtil.sumMapItems(_property.purchaseCostsItemized);
        _purchaseCostItemize.setText(String.format(Locale.US, "%d", purchaseCostItemized));

        _repairCost.setVisibility(_property.repairRemodelCostsItemized.isEmpty() ? View.VISIBLE : View.GONE);
        _repairCostItemize.setVisibility(_property.repairRemodelCostsItemized.isEmpty() ? View.GONE : View.VISIBLE);
        int repairCostItemized = CalcUtil.sumMapItems(_property.repairRemodelCostsItemized);
        _repairCostItemize.setText(String.format(Locale.US, "%d", repairCostItemized));

        _totalExpensesPercentLayout.setVisibility(_property.expensesItemized.isEmpty() ? View.VISIBLE : View.GONE);
        _totalExpensesItemizeCurrency.setVisibility(_property.expensesItemized.isEmpty() ? View.GONE : View.VISIBLE);
        _totalExpensesItemizeLayout.setVisibility(_property.expensesItemized.isEmpty() ? View.GONE : View.VISIBLE);
        int expensesItemized = CalcUtil.sumMapItems(_property.expensesItemized);
        _totalExpensesItemize.setText(String.format(Locale.US, "%d", expensesItemized));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == FOR_RESULT_ITEMIZE)
        {
            if (resultCode == RESULT_OK)
            {
                // New items were entered. Remove old items and save off new items.
                Bundle extras = data.getExtras();
                HashMap<String, Integer> newItems = (HashMap<String, Integer>)extras.getSerializable("items");

                _currentForResultItemize.clear();
                if(newItems != null)
                {
                    _currentForResultItemize.putAll(newItems);
                }
            }
        }
    }

    private void doSave()
    {
        Property updatedProperty = new Property(_property);
        updatedProperty.purchasePrice = extractInt(_price, 0);
        updatedProperty.afterRepairsValue = extractInt(_afterRepairsValue, 0);
        updatedProperty.useLoan = _financing.isChecked();
        updatedProperty.downPayment = extractInt(_downPayment, 0);
        updatedProperty.interestRate = extractDouble(_interestRate, 0);
        updatedProperty.loanDuration = extractInt(_loanDuration, 0);
        updatedProperty.purchaseCosts = extractInt(_purchaseCost, 0);
        updatedProperty.repairRemodelCosts = extractInt(_repairCost, 0);
        updatedProperty.grossRent = extractInt(_rent, 0);
        updatedProperty.otherIncome = extractInt(_otherIncome, 0);
        updatedProperty.expenses = extractInt(_totalExpenses, 0);
        updatedProperty.vacancy = extractInt(_vacancy, 0);
        updatedProperty.appreciation = extractInt(_appreciation, 0);
        updatedProperty.incomeIncrease = extractInt(_incomeIncrease, 0);
        updatedProperty.expenseIncrease = extractInt(_expensesIncrease, 0);
        updatedProperty.sellingCosts = extractInt(_sellingCosts, 0);
        updatedProperty.landValue = extractInt(_landValue, 0);
        updatedProperty.incomeTaxRate = extractInt(_incomeTaxRate, 0);

        Log.i(TAG, "Updating property " + updatedProperty.id);
        _db.updateProperty(updatedProperty);

        finish();
    }

    private int extractInt(EditText view, int defaultValue)
    {
        String string = view.getText().toString();
        if(string.isEmpty() == false)
        {
            try
            {
                return Integer.parseInt(string);
            }
            catch(NumberFormatException e)
            {
                Log.w(TAG, "Failed to parse " + string, e);
            }
        }

        return defaultValue;
    }

    private double extractDouble(EditText view, double defaultValue)
    {
        String string = view.getText().toString();
        if(string.isEmpty() == false)
        {
            try
            {
                return Double.parseDouble(string);
            }
            catch(NumberFormatException e)
            {
                Log.w(TAG, "Failed to parse " + string, e);
            }
        }

        return defaultValue;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.save_menu, menu);
        return super.onCreateOptionsMenu(menu);
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

        if(id == R.id.action_save)
        {
            doSave();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy()
    {
        _db.close();
        super.onDestroy();
    }
}

class ItemizeOptionItem
{
    final Integer titleId;
    final Integer descriptionId;
    final HashMap<String, Integer> items;

    ItemizeOptionItem(int title, int description, HashMap<String, Integer> itemizations)
    {
        titleId = title;
        descriptionId = description;
        items = itemizations;
    }
}