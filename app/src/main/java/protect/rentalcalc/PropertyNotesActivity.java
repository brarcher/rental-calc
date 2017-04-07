package protect.rentalcalc;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class PropertyNotesActivity extends AppCompatActivity
{
    private static final String TAG = "RentalCalc";

    private DBHelper _db;
    private Property _property;
    private EditText _notes;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.property_notes_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        _db = new DBHelper(this);

        final Bundle b = getIntent().getExtras();

        if (b == null || b.containsKey("id") == false)
        {
            Toast.makeText(this, "No property found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        final long propertyId = b.getLong("id");
        _property = _db.getProperty(propertyId);

        if (_property == null)
        {
            Toast.makeText(this, "No property found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        _notes = (EditText)findViewById(R.id.notes);
        _notes.setText(_property.notes);
    }

    private void doSave()
    {
        Property updatedProperty = new Property(_property);
        updatedProperty.notes = _notes.getText().toString();

        Log.i(TAG, "Updating notes for property " + updatedProperty.id);
        _db.updateProperty(updatedProperty);

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
