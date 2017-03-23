package protect.rentalcalc;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.google.common.collect.ImmutableList;

import java.util.List;


public class PropertyWorksheetActivity extends AppCompatActivity
{
    private static final String TAG = "RentalCalc";
    private DBHelper _db;

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

        ToggleButton financing = (ToggleButton)findViewById(R.id.financing);
        financing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                List<View> views = ImmutableList.of(
                    findViewById(R.id.loanBorder),
                    findViewById(R.id.loanRow),
                    findViewById(R.id.interestBorder),
                    findViewById(R.id.interestRow),
                    findViewById(R.id.purchaseBorder),
                    findViewById(R.id.purchaseRow)
                );

                for(View view : views)
                {
                    view.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                }
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
