package protect.rentalcalc;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;

public class PictureViewActivity  extends AppCompatActivity
{
    private static final String TAG = "RentalCalc";

    private String imageFilename = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.picture_view_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final Bundle b = getIntent().getExtras();

        if (b == null || b.containsKey("file") == false)
        {
            Toast.makeText(this, "No image found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        imageFilename = b.getString("file");

        final ImageView imageView = (ImageView) findViewById(R.id.imageView);
        Glide.with(this).load(imageFilename).fitCenter().into(imageView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.picture_view_menu, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.action_share);

        // Fetch ShareActionProvider
        ShareActionProvider shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        if (shareActionProvider == null)
        {
            Log.w(TAG, "Failed to find share action provider");
            return false;
        }

        if(imageFilename == null)
        {
            Log.w(TAG, "No receipt to share");
            return false;
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);

        // Determine mimetype of image
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFilename, opt);
        shareIntent.setType(opt.outMimeType);

        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(imageFilename)));
        shareActionProvider.setShareIntent(shareIntent);

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
            builder.setTitle(R.string.deletePictureTitle);
            builder.setMessage(R.string.deletePictureConfirmation);
            builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    Log.e(TAG, "Requesting to delete picture: " + imageFilename);

                    Intent i = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("delete", imageFilename);
                    i.putExtras(bundle);
                    setResult(RESULT_OK, i);

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
}
