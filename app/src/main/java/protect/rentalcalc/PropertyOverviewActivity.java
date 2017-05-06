package protect.rentalcalc;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class PropertyOverviewActivity extends AppCompatActivity
{
    private static final String TAG = "RentalCalc";

    private DBHelper _db;
    private Property _property;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.property_overview_layout);
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
    public void onResume()
    {
        super.onResume();

        final Bundle b = getIntent().getExtras();

        if(b == null || b.containsKey("id") == false)
        {
            Toast.makeText(this, "No property found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        long propertyId = b.getLong("id");
        _property = _db.getProperty(propertyId);

        if(_property == null)
        {
            Toast.makeText(this, "No property found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }


        final TextView nickname = (TextView)findViewById(R.id.nickname);
        nickname.setText(_property.nickname);

        final TextView street = (TextView)findViewById(R.id.street);
        street.setText(_property.addressStreet);

        final TextView stateZip = (TextView)findViewById(R.id.stateZip);
        stateZip.setText(String.format(Locale.US, "%s%s%s%s%s",
                _property.addressCity,
                (_property.addressCity .isEmpty() == false) && (_property.addressState.isEmpty() == false ||  _property.addressZip.isEmpty() == false) ? ", " : "",
                _property.addressState,
                (_property.addressState .isEmpty() == false) && (_property.addressZip.isEmpty() == false) ? " " : "",
                _property.addressZip));

        final TextView price = (TextView)findViewById(R.id.price);
        price.setText(String.format(Locale.US, "%dK", _property.purchasePrice/1000));

        final Bundle argBundle = new Bundle();
        argBundle.putLong("id", _property.id);

        final View propertyView = findViewById(R.id.propertyView);
        propertyView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(getApplicationContext(), PropertyViewActivity.class);
                i.putExtras(argBundle);
                startActivity(i);
            }
        });

        final View propertyWorksheet = findViewById(R.id.propertyWorksheet);
        propertyWorksheet.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(getApplicationContext(), PropertyWorksheetActivity.class);
                i.putExtras(argBundle);
                startActivity(i);
            }
        });

        final View propertyNotes = findViewById(R.id.propertyNotes);
        propertyNotes.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(getApplicationContext(), PropertyNotesActivity.class);
                i.putExtras(argBundle);
                startActivity(i);
            }
        });

        final View propertySummary = findViewById(R.id.propertySummary);
        propertySummary.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(getApplicationContext(), PropertySummaryActivity.class);
                i.putExtras(argBundle);
                startActivity(i);
            }
        });

        final View propertyProjections = findViewById(R.id.propertyProjections);
        propertyProjections.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(getApplicationContext(), PropertyProjectionsActivity.class);
                i.putExtras(argBundle);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.properties_overview_menu, menu);
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

        if(id == R.id.action_delete)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.deletePropertyTitle);
            builder.setMessage(R.string.deletePropertyConfirmation);
            builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    Log.e(TAG, "Deleting property: " + _property.id);

                    _db.deleteProperty(_property.id);

                    finish();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

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
