package protect.rentalcalc;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.collect.ImmutableMap;

import java.util.Calendar;
import java.util.Map;

public class PropertiesListActivity extends AppCompatActivity
{
    private static final String TAG = "RentalCalc";

    private DBHelper _db;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        _db = new DBHelper(this);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        final ListView propertyList = (ListView) findViewById(R.id.list);
        final TextView helpText = (TextView) findViewById(R.id.helpText);

        if (_db.getPropertyCount() > 0)
        {
            propertyList.setVisibility(View.VISIBLE);
            helpText.setVisibility(View.GONE);
        }
        else
        {
            propertyList.setVisibility(View.GONE);
            helpText.setVisibility(View.VISIBLE);
        }

        final Cursor properties = _db.getProperties();
        final PropertyCursorAdapter adapter = new PropertyCursorAdapter(this, properties);
        propertyList.setAdapter(adapter);

        propertyList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Cursor selected = (Cursor)parent.getItemAtPosition(position);
                if(selected == null)
                {
                    Log.w(TAG, "Clicked transaction at position " + position + " is null");
                    return;
                }

                Property property = Property.toProperty(selected);

                Intent i = new Intent(view.getContext(), PropertyOverviewActivity.class);
                final Bundle b = new Bundle();
                b.putLong("id", property.id);
                i.putExtras(b);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.properties_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if(id == R.id.action_add)
        {
            Intent i = new Intent(getApplicationContext(), PropertyViewActivity.class);
            startActivity(i);
        }

        if(id == R.id.action_about)
        {
            displayAboutDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void displayAboutDialog()
    {
        final Map<String, String> USED_LIBRARIES = ImmutableMap.of
        (
            "Guava", "https://github.com/google/guava",
            "MathView", "https://github.com/kexanie/MathView"
        );

        final Map<String, String> USED_ASSETS = ImmutableMap.of
        (
            "House by Mani Amini", "https://thenounproject.com/term/house/41030/",
            "Chat Question by Shmidt Sergey", "https://thenounproject.com/term/question,/562370/",
            "Save by Bernar Novalyi", "https://thenounproject.com/term/save/716011",
            "For Sale by parkjisun", "https://thenounproject.com/term/for-sale/716241/"
        );

        StringBuilder libs = new StringBuilder().append("<ul>");
        for (Map.Entry<String, String> entry : USED_LIBRARIES.entrySet())
        {
            libs.append("<li><a href=\"").append(entry.getValue()).append("\">").append(entry.getKey()).append("</a></li>");
        }
        libs.append("</ul>");

        StringBuilder resources = new StringBuilder().append("<ul>");
        for (Map.Entry<String, String> entry : USED_ASSETS.entrySet())
        {
            resources.append("<li><a href=\"").append(entry.getValue()).append("\">").append(entry.getKey()).append("</a></li>");
        }
        resources.append("</ul>");

        String appName = getString(R.string.app_name);
        int year = Calendar.getInstance().get(Calendar.YEAR);

        String version = "?";
        try
        {
            PackageManager manager = getPackageManager();
            if(manager != null)
            {
                PackageInfo pi = manager.getPackageInfo(getPackageName(), 0);
                version = pi.versionName;
            }
            else
            {
                Log.w(TAG, "Package name not found, PackageManager unavailable");
            }
        }
        catch (PackageManager.NameNotFoundException e)
        {
            Log.w(TAG, "Package name not found", e);
        }

        WebView wv = new WebView(this);
        String html =
                "<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" />" +
                        "<img src=\"file:///android_res/mipmap/ic_launcher.png\" alt=\"" + appName + "\"/>" +
                        "<h1>" +
                        String.format(getString(R.string.about_title_fmt),
                                "<a href=\"" + getString(R.string.app_webpage_url)) + "\">" +
                        appName +
                        "</a>" +
                        "</h1><p>" +
                        appName +
                        " " +
                        String.format(getString(R.string.debug_version_fmt), version) +
                        "</p><p>" +
                        String.format(getString(R.string.app_revision_fmt),
                                "<a href=\"" + getString(R.string.app_revision_url) + "\">" +
                                        getString(R.string.app_revision_url) +
                                        "</a>") +
                        "</p><hr/><p>" +
                        String.format(getString(R.string.app_copyright_fmt), year) +
                        "</p><hr/><p>" +
                        getString(R.string.app_license) +
                        "</p><hr/><p>" +
                        String.format(getString(R.string.app_libraries), appName, libs.toString() +
                        "</p><hr/><p>" +
                        String.format(getString(R.string.app_resources), appName, resources.toString()));


        wv.loadDataWithBaseURL("file:///android_res/drawable/", html, "text/html", "utf-8", null);
        new AlertDialog.Builder(this)
                .setView(wv)
                .setCancelable(true)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    protected void onDestroy()
    {
        _db.close();
        super.onDestroy();
    }
}
