package protect.rentalcalc;


import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.ImmutableList;

import java.util.List;

import io.github.kexanie.library.MathView;

public class DictionaryActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dictionary_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final Bundle b = getIntent().getExtras();

        if (b == null || b.containsKey("title") == false || b.containsKey("definition") == false)
        {
            Toast.makeText(this, "No definition found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        final int titleId = b.getInt("title");
        setTitle(titleId);

        final int definitionId = b.getInt("definition");
        TextView definitionView = (TextView)findViewById(R.id.definition);
        definitionView.setText(definitionId);

        if(b.containsKey("formula"))
        {
            final int formulaId = b.getInt("formula");
            String tex = getResources().getString(formulaId);
            MathView mathView = (MathView)findViewById(R.id.formula);
            mathView.setText(tex);

            // String tex = "$$\\sum_{i=0}^n i^2 = \\frac{(n^2+n)(2n+1)}{6}$$";
        }
        else
        {
            List<Integer> viewsToHide = ImmutableList.of
            (
                R.id.formulaTitleBorder,
                R.id.formulaTitleRow,
                R.id.formulaTextBorderStart,
                R.id.formulaTextRow
            );
            for(Integer id : viewsToHide)
            {
                findViewById(id).setVisibility(View.GONE);
            }
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
