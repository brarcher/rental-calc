package protect.rentalcalc;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        final Bundle b = getIntent().getExtras();
        final Property existingProperty = (b != null && b.containsKey("id")) ? _db.getProperty(b.getInt("id")) : null;

        final EditText nicknameField = (EditText)findViewById(R.id.nickname);
        final EditText streetField = (EditText)findViewById(R.id.street);
        final EditText cityField = (EditText)findViewById(R.id.city);
        final EditText stateField = (EditText)findViewById(R.id.state);
        final EditText zipField = (EditText)findViewById(R.id.zip);
        final Spinner typeSpinner = (Spinner)findViewById(R.id.type);
        final Spinner bedsSpinner = (Spinner)findViewById(R.id.beds);
        final Spinner bathsSpinner = (Spinner)findViewById(R.id.baths);
        final EditText sqftField = (EditText)findViewById(R.id.sqft);
        final EditText lotField = (EditText)findViewById(R.id.lot);
        final EditText yearField = (EditText)findViewById(R.id.year);
        final Spinner parkingSpinner = (Spinner)findViewById(R.id.parking);
        final EditText zoningField = (EditText)findViewById(R.id.zoning);
        final EditText mlsField = (EditText)findViewById(R.id.mls);

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
        typeSpinner.setAdapter(typeValueAdapter);

        final ImmutableList.Builder<String> bedValueBuilder = ImmutableList.builder();
        for(int bed = 1; bed <= 14; bed++)
        {
            bedValueBuilder.add(Integer.toString(bed));
        }
        bedValueBuilder.add("15+");
        final List<String> bedValues = bedValueBuilder.build();
        ArrayAdapter<String> bedValueAdapter = new ArrayAdapter<>(this, R.layout.spinner_textview, bedValues);
        bedsSpinner.setAdapter(bedValueAdapter);

        ImmutableList.Builder<String> bathValueBuilder = ImmutableList.builder();
        for(int bath = 1; bath <= 9; bath++)
        {
            bathValueBuilder.add(Integer.toString(bath));
            bathValueBuilder.add(Integer.toString(bath) + ".5");
        }
        bathValueBuilder.add("10+");
        final List<String> bathValues = bathValueBuilder.build();
        ArrayAdapter<String> bathValueAdapter = new ArrayAdapter<>(this, R.layout.spinner_textview, bathValues);
        bathsSpinner.setAdapter(bathValueAdapter);

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
        parkingSpinner.setAdapter(parkingValueAdapter);

        // Pre-populate the values if we have a property to display
        if(existingProperty != null)
        {
            nicknameField.setText(existingProperty.nickname);
            streetField.setText(existingProperty.addressStreet);
            cityField.setText(existingProperty.addressCity);
            stateField.setText(existingProperty.addressState);
            zipField.setText(existingProperty.addressZip);
            typeSpinner.setSelection(existingProperty.propertyType);
            int bedIndex = bedValues.indexOf(existingProperty.propertyBeds);
            bedsSpinner.setSelection(bedIndex >= 0 ? bedIndex : 0);
            int bathIndex = bathValues.indexOf(existingProperty.propertyBaths);
            bathsSpinner.setSelection(bathIndex >= 0 ? bathIndex : 0);
            if(existingProperty.propertySqft > 0)
            {
                sqftField.setText(Integer.toString(existingProperty.propertySqft));
            }
            if(existingProperty.propertyLot > 0)
            {
                lotField.setText(Integer.toString(existingProperty.propertyLot));
            }
            if(existingProperty.propertyYear > 0)
            {
                yearField.setText(Integer.toString(existingProperty.propertyYear));
            }
            parkingSpinner.setSelection(existingProperty.propertyParking);
            zoningField.setText(existingProperty.propertyZoning);
            mlsField.setText(existingProperty.propertyMls);
        }

        final Button saveButton = (Button)findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                final String nickname = nicknameField.getText().toString();
                if(nickname.isEmpty())
                {
                    Toast.makeText(PropertyViewActivity.this, "Nickname missing but required", Toast.LENGTH_LONG).show();
                    return;
                }

                final String street = streetField.getText().toString();
                final String city = cityField.getText().toString();
                final String state = stateField.getText().toString();
                final String zip = zipField.getText().toString();

                final PropertyType proopertyType = PropertyType.fromString(PropertyViewActivity.this, (String)typeSpinner.getSelectedItem());
                final int propertyTypePosition = proopertyType.ordinal();

                final String beds = (String)bedsSpinner.getSelectedItem();
                final String baths = (String)bathsSpinner.getSelectedItem();
                final String sqftStr = sqftField.getText().toString();
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

                final String lotStr = lotField.getText().toString();
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

                final String yearStr = yearField.getText().toString();
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

                final ParkingType parkingType = ParkingType.fromString(PropertyViewActivity.this, (String)parkingSpinner.getSelectedItem());
                final int parkingPosition = parkingType.ordinal();

                final String zoning = zoningField.getText().toString();
                final String mls = mlsField.getText().toString();

                final int id = (existingProperty != null) ? existingProperty.id : 0;

                Property newProperty = new Property(id, nickname, street, city, state, zip,
                        propertyTypePosition, beds, baths, sqft, lot, year, parkingPosition, zoning, mls);

                if(existingProperty == null)
                {
                    Log.i(TAG, "Adding property");
                    _db.insertProperty(newProperty);
                }
                else
                {
                    Log.i(TAG, "Updating property " + newProperty.id);
                    _db.updateProperty(newProperty);
                }

                finish();
            }
        });

        final Button cancelButton = (Button)findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
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

    @Override
    protected void onDestroy()
    {
        _db.close();
        super.onDestroy();
    }
}
