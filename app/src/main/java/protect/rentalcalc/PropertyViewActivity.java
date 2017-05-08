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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class PropertyViewActivity extends AppCompatActivity
{
    private static final String TAG = "RentalCalc";
    private DBHelper _db;

    private EditText _nicknameField;
    private EditText _streetField;
    private EditText _cityField;
    private EditText _stateField;
    private EditText _zipField;
    private Spinner _typeSpinner;
    private Spinner _bedsSpinner;
    private Spinner _bathsSpinner;
    private EditText _sqftField;
    private EditText _lotField;
    private EditText _yearField;
    private Spinner _parkingSpinner;
    private EditText _zoningField;
    private EditText _mlsField;

    private Property _existingProperty;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.property_add_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        _db = new DBHelper(this);

        _nicknameField = (EditText)findViewById(R.id.nickname);
        _streetField = (EditText)findViewById(R.id.street);
        _cityField = (EditText)findViewById(R.id.city);
        _stateField = (EditText)findViewById(R.id.state);
        _zipField = (EditText)findViewById(R.id.zip);
        _typeSpinner = (Spinner)findViewById(R.id.type);
        _bedsSpinner = (Spinner)findViewById(R.id.beds);
        _bathsSpinner = (Spinner)findViewById(R.id.baths);
        _sqftField = (EditText)findViewById(R.id.sqft);
        _lotField = (EditText)findViewById(R.id.lot);
        _yearField = (EditText)findViewById(R.id.year);
        _parkingSpinner = (Spinner)findViewById(R.id.parking);
        _zoningField = (EditText)findViewById(R.id.zoning);
        _mlsField = (EditText)findViewById(R.id.mls);

        final Bundle b = getIntent().getExtras();
        _existingProperty = (b != null && b.containsKey("id")) ? _db.getProperty(b.getLong("id")) : null;

        final List<String> typeValues = ImmutableList.of(
                getString(PropertyType.BLANK.stringId),
                getString(PropertyType.COMMERCIAL.stringId),
                getString(PropertyType.CONDO.stringId),
                getString(PropertyType.HOUSE.stringId),
                getString(PropertyType.LAND.stringId),
                getString(PropertyType.MULTI_FAMILY.stringId),
                getString(PropertyType.MANUFACTURED.stringId),
                getString(PropertyType.OTHER.stringId)
        );
        final ArrayAdapter<String> typeValueAdapter = new ArrayAdapter<>(this, R.layout.spinner_textview, typeValues);
        _typeSpinner.setAdapter(typeValueAdapter);

        final ImmutableList.Builder<String> bedValueBuilder = ImmutableList.builder();
        for(int bed = 1; bed <= 14; bed++)
        {
            bedValueBuilder.add(Integer.toString(bed));
        }
        bedValueBuilder.add("15+");
        final List<String> bedValues = bedValueBuilder.build();
        ArrayAdapter<String> bedValueAdapter = new ArrayAdapter<>(this, R.layout.spinner_textview, bedValues);
        _bedsSpinner.setAdapter(bedValueAdapter);

        ImmutableList.Builder<String> bathValueBuilder = ImmutableList.builder();
        for(int bath = 1; bath <= 9; bath++)
        {
            bathValueBuilder.add(Integer.toString(bath));
            bathValueBuilder.add(Integer.toString(bath) + ".5");
        }
        bathValueBuilder.add("10+");
        final List<String> bathValues = bathValueBuilder.build();
        ArrayAdapter<String> bathValueAdapter = new ArrayAdapter<>(this, R.layout.spinner_textview, bathValues);
        _bathsSpinner.setAdapter(bathValueAdapter);

        final List<String> parkingValues = ImmutableList.of(
                getString(ParkingType.BLANK.stringId),
                getString(ParkingType.CAR_PORT.stringId),
                getString(ParkingType.GARAGE.stringId),
                getString(ParkingType.OFF_STREET.stringId),
                getString(ParkingType.ON_STREET.stringId),
                getString(ParkingType.NONE.stringId),
                getString(ParkingType.PRIVATE_LOT.stringId)
        );
        ArrayAdapter<String> parkingValueAdapter = new ArrayAdapter<>(this, R.layout.spinner_textview, parkingValues);
        _parkingSpinner.setAdapter(parkingValueAdapter);

        // Pre-populate the values if we have a property to display
        if(_existingProperty != null)
        {
            _nicknameField.setText(_existingProperty.nickname);
            _streetField.setText(_existingProperty.addressStreet);
            _cityField.setText(_existingProperty.addressCity);
            _stateField.setText(_existingProperty.addressState);
            _zipField.setText(_existingProperty.addressZip);
            _typeSpinner.setSelection(_existingProperty.propertyType);
            int bedIndex = bedValues.indexOf(_existingProperty.propertyBeds);
            _bedsSpinner.setSelection(bedIndex >= 0 ? bedIndex : 0);
            int bathIndex = bathValues.indexOf(_existingProperty.propertyBaths);
            _bathsSpinner.setSelection(bathIndex >= 0 ? bathIndex : 0);
            if(_existingProperty.propertySqft > 0)
            {
                _sqftField.setText(Integer.toString(_existingProperty.propertySqft));
            }
            if(_existingProperty.propertyLot > 0)
            {
                _lotField.setText(Integer.toString(_existingProperty.propertyLot));
            }
            if(_existingProperty.propertyYear > 0)
            {
                _yearField.setText(Integer.toString(_existingProperty.propertyYear));
            }
            _parkingSpinner.setSelection(_existingProperty.propertyParking);
            _zoningField.setText(_existingProperty.propertyZoning);
            _mlsField.setText(_existingProperty.propertyMls);
        }
    }

    private void doSave()
    {
        final String nickname = _nicknameField.getText().toString();
        if(nickname.isEmpty())
        {
            Toast.makeText(PropertyViewActivity.this, "Nickname missing but required", Toast.LENGTH_LONG).show();
            return;
        }

        final String street = _streetField.getText().toString();
        final String city = _cityField.getText().toString();
        final String state = _stateField.getText().toString();
        final String zip = _zipField.getText().toString();

        final PropertyType proopertyType = PropertyType.fromString(PropertyViewActivity.this, (String)_typeSpinner.getSelectedItem());
        final int propertyTypePosition = proopertyType.ordinal();

        final String beds = (String)_bedsSpinner.getSelectedItem();
        final String baths = (String)_bathsSpinner.getSelectedItem();
        final String sqftStr = _sqftField.getText().toString();
        int sqft = 0;
        if(sqftStr.isEmpty() == false)
        {
            try
            {
                sqft = Integer.parseInt(sqftStr);
            }
            catch(NumberFormatException e)
            {
                Toast.makeText(PropertyViewActivity.this, "Square footage not an number: " + sqftStr, Toast.LENGTH_LONG).show();
                return;
            }
        }

        final String lotStr = _lotField.getText().toString();
        int lot = 0;
        if(lotStr.isEmpty() == false)
        {
            try
            {
                lot = Integer.parseInt(lotStr);
            }
            catch(NumberFormatException e)
            {
                Toast.makeText(PropertyViewActivity.this, "Lot size not an number: " + lotStr, Toast.LENGTH_LONG).show();
                return;
            }
        }

        final String yearStr = _yearField.getText().toString();
        int year = 0;
        if(yearStr.isEmpty() == false)
        {
            try
            {
                year = Integer.parseInt(yearStr);
            }
            catch(NumberFormatException e)
            {
                Toast.makeText(PropertyViewActivity.this, "Year not an number: " + yearStr, Toast.LENGTH_LONG).show();
                return;
            }
        }

        final ParkingType parkingType = ParkingType.fromString(PropertyViewActivity.this, (String)_parkingSpinner.getSelectedItem());
        final int parkingPosition = parkingType.ordinal();

        final String zoning = _zoningField.getText().toString();
        final String mls = _mlsField.getText().toString();

        final long id = (_existingProperty != null) ? _existingProperty.id : 0;

        Property newProperty = (_existingProperty != null) ? new Property(_existingProperty) : new Property();
        newProperty.id = id;
        newProperty.nickname = nickname;
        newProperty.addressStreet = street;
        newProperty.addressCity = city;
        newProperty.addressState = state;
        newProperty.addressZip = zip;
        newProperty.propertyType = propertyTypePosition;
        newProperty.propertyBeds = beds;
        newProperty.propertyBaths = baths;
        newProperty.propertySqft = sqft;
        newProperty.propertyLot = lot;
        newProperty.propertyYear = year;
        newProperty.propertyParking = parkingPosition;
        newProperty.propertyZoning = zoning;
        newProperty.propertyMls = mls;

        if(_existingProperty == null)
        {
            Log.i(TAG, "Adding property");
            long newId = _db.insertProperty(newProperty);

            // Load the overview activity so that the user can start working
            // with the new property.
            Intent i = new Intent(this, PropertyOverviewActivity.class);
            final Bundle b = new Bundle();
            b.putLong("id", newId);
            i.putExtras(b);
            startActivity(i);
        }
        else
        {
            Log.i(TAG, "Updating property " + newProperty.id);
            _db.updateProperty(newProperty);
        }

        finish();
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
