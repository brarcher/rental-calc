package protect.rentalcalc;

import android.Manifest;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;


public class PropertyPicturesActivity extends AppCompatActivity
{
    private static final String TAG = "RentalCalc";

    private static final int SELECT_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_VIEW_IMAGE = 3;
    private static final int PERMISSIONS_REQUEST_CAMERA = 4;

    private static final int JPEG_QUALITY_LEVEL = 80;

    private File _imageCaptureFile;

    private DBHelper _db;
    private Property _property;
    private LinkedList<Picture> _pictures;
    private PictureAdapter _adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.property_pictures_layout);
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

        _pictures = new LinkedList<>();
        for(File file : _property.pictures)
        {
            _pictures.add(new Picture(file));
        }

        GridView grid = (GridView)findViewById(R.id.grid);
        _adapter = new PictureAdapter(this, _pictures, REQUEST_VIEW_IMAGE);
        grid.setAdapter(_adapter);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSIONS_REQUEST_CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        if(requestCode == PERMISSIONS_REQUEST_CAMERA)
        {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                // Nothing to do, was granted
            }
            else
            {
                // Camera permission rejected, inform user that
                // no receipt can be taken.
                Toast.makeText(getApplicationContext(), R.string.noCameraPermissionError,
                        Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.picture_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if(id == R.id.action_add)
        {
            final PackageManager packageManager = getPackageManager();
            if(packageManager == null)
            {
                Log.e(TAG, "Failed to get package manager, cannot take picture");
                Toast.makeText(getApplicationContext(), R.string.pictureCaptureError,
                        Toast.LENGTH_LONG).show();
                return true;
            }

            new AlertDialog.Builder(this)
                    .setCancelable(true)
                    .setTitle(R.string.addNewPhotoTitle)
                    .setNegativeButton(R.string.importExistingPhoto, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();

                            Intent intent = new Intent();
                            // Show only images, no videos
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);

                            if(intent.resolveActivity(packageManager) == null)
                            {
                                Log.e(TAG, "Could not find an activity to import a picture");
                                Toast.makeText(getApplicationContext(), R.string.pictureCaptureError, Toast.LENGTH_LONG).show();
                                return;
                            }

                            String title = getResources().getString(R.string.selectPhotoTitle);

                            // Always show the chooser (if there are multiple options available)
                            startActivityForResult(Intent.createChooser(intent, title), SELECT_IMAGE_REQUEST);
                        }
                    })
                    .setPositiveButton(R.string.captureNewPhoto, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();

                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                            if(intent.resolveActivity(packageManager) == null)
                            {
                                Log.e(TAG, "Could not find an activity to take a picture");
                                Toast.makeText(getApplicationContext(), R.string.pictureCaptureError, Toast.LENGTH_LONG).show();
                                return;
                            }

                            _imageCaptureFile = getNewImageLocation();
                            if(_imageCaptureFile != null)
                            {
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(_imageCaptureFile));
                                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                            }
                        }
                    })
                    .show();
        }

        if(id == android.R.id.home)
        {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveProperty()
    {
        _property.pictures.clear();

        for(Picture picture : _pictures)
        {
            _property.pictures.add(new File(picture.uri.getPath()));
        }

        boolean result = _db.updateProperty(_property);
        if(result == false)
        {
            Log.w(TAG, "Failed to update pictures for property: " + _property.nickname);
        }
    }

    private File getNewImageLocation()
    {
        File imageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if(imageDir == null)
        {
            Log.e(TAG, "Failed to locate directory for pictures");
            Toast.makeText(this, R.string.pictureCaptureError, Toast.LENGTH_LONG).show();
            return null;
        }

        if(imageDir.exists() == false)
        {
            if(imageDir.mkdirs() == false)
            {
                Log.e(TAG, "Failed to create receipts image directory");
                Toast.makeText(this, R.string.pictureCaptureError, Toast.LENGTH_LONG).show();
                return null;
            }
        }

        UUID imageFilename = UUID.randomUUID();
        File pictureFile = new File(imageDir, imageFilename.toString() + ".jpg");

        return pictureFile;
    }

    private File saveImageWithQuality(Bitmap image, int quality, String rotation)
    {
        File savedImage = getNewImageLocation();

        try
        {
            if (savedImage == null)
            {
                throw new IOException("Could not create location for image file");
            }

            boolean created = savedImage.createNewFile();
            if (created == false)
            {
                throw new IOException("Could not create empty image file");
            }

            FileOutputStream fOut = new FileOutputStream(savedImage);
            boolean fileWritten = image.compress(Bitmap.CompressFormat.JPEG, quality, fOut);
            fOut.flush();
            fOut.close();

            if (fileWritten == false)
            {
                throw new IOException("Could not down compress file");
            }

            ExifInterface exifData = new ExifInterface(savedImage.getAbsolutePath());
            exifData.setAttribute(ExifInterface.TAG_ORIENTATION, rotation);
            exifData.saveAttributes();

            Log.i(TAG, "Image file " + savedImage.getAbsolutePath() + " saved at quality " + quality + " and rotation " + rotation);

            return savedImage;
        }
        catch(IOException e)
        {
            Log.e(TAG, "Failed to encode image", e);
            Toast.makeText(this, R.string.pictureCaptureError, Toast.LENGTH_LONG).show();

            if(savedImage != null)
            {
                boolean result = savedImage.delete();
                if(result == false)
                {
                    Log.w(TAG, "Failed to delete image file: " + savedImage.getAbsolutePath());
                }
            }
        }

        return null;
    }

    private void saveImageInBackground(final Bitmap image, final String rotation)
    {
        Log.i(TAG, "Saving image in background");

        AsyncTask<Void, Void, File> imageSaver = new AsyncTask<Void, Void, File>()
        {
            ProgressDialog dialog;

            @Override
            protected void onPreExecute()
            {
                dialog = new ProgressDialog(PropertyPicturesActivity.this);
                dialog.setMessage(PropertyPicturesActivity.this.getResources().getString(R.string.savingPicture));
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }

            @Override
            protected File doInBackground(Void... params)
            {
                return saveImageWithQuality(image, JPEG_QUALITY_LEVEL, rotation);
            }

            @Override
            protected void onPostExecute(File result)
            {
                if(result != null)
                {
                    Log.i(TAG, "Image file saved: " + result.getAbsolutePath());

                    _pictures.add(new Picture(result));
                    _adapter.notifyDataSetChanged();
                    saveProperty();
                }
                else
                {
                    Log.e(TAG, "Failed to save image");
                    Toast.makeText(PropertyPicturesActivity.this, R.string.pictureCaptureError, Toast.LENGTH_LONG).show();
                }

                dialog.hide();
            }
        };

        imageSaver.execute();
    }

    String getImageRotation(String file)
    {
        String rotation = Integer.toString(ExifInterface.ORIENTATION_NORMAL);

        try
        {
            ExifInterface exifData = new ExifInterface(file);
            rotation = exifData.getAttribute(ExifInterface.TAG_ORIENTATION);
        }
        catch(IOException e)
        {
            Log.w(TAG, "Failed to read rotation data on file: " + file);
        }

        Log.d(TAG, "Read orientation from imported file: " + rotation);

        return rotation;
    }

    private int calcImageResizeScale(InputStream imageStream)
    {
        final int MAX_IMAGE_DIMENSION = 1024;

        // Determine only the width/height of the bitmap without loading the contents
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(imageStream, null, opts);

        // Determine the correct scale value. It should be a power of 2
        int resizeScale = 1;

        if (opts.outHeight > MAX_IMAGE_DIMENSION || opts.outWidth > MAX_IMAGE_DIMENSION)
        {
            resizeScale = (int)Math.pow(2, (int) Math.round(Math.log(MAX_IMAGE_DIMENSION / (double) Math.max(opts.outHeight, opts.outWidth)) / Math.log(0.5)));
        }

        Log.d(TAG, "Initial size: (" + opts.outHeight + "," + opts.outWidth + ")");
        Log.d(TAG, "Resize scale: " + resizeScale);

        return resizeScale;
    }

    private Bitmap loadBitmapWithResizeScale(InputStream imageStream, int resizeScale)
            throws IOException
    {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = resizeScale;
        Bitmap bitmap = BitmapFactory.decodeStream(imageStream, null, opts);
        if(bitmap == null)
        {
            throw new IOException("Failed to decode image stream");
        }

        Log.d(TAG, "Loaded size: (" + bitmap.getHeight() + "," + bitmap.getWidth() + ")");

        return bitmap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == SELECT_IMAGE_REQUEST)
        {
            if (resultCode == RESULT_OK && data != null && data.getData() != null)
            {
                Uri uri = data.getData();

                try
                {
                    InputStream imageStream = getContentResolver().openInputStream(uri);

                    if(imageStream == null)
                    {
                        throw new IOException("Could not load image stream");
                    }

                    int resizeScale = calcImageResizeScale(imageStream);
                    imageStream.close();

                    // The stream needs to be opened a second time, now to
                    // finally load in the bitmap
                    imageStream = getContentResolver().openInputStream(uri);

                    if(imageStream == null)
                    {
                        throw new IOException("Could not load image stream");
                    }

                    Bitmap bitmap = loadBitmapWithResizeScale(imageStream, resizeScale);
                    imageStream.close();

                    if(bitmap != null)
                    {
                        String rotation = getImageRotation(uri.getPath());
                        saveImageInBackground(bitmap, rotation);
                    }
                    else
                    {
                        throw new IOException("Failed to extract bitmap");
                    }
                }
                catch (IOException e)
                {
                    Log.w(TAG, "Failed to select image", e);
                    Toast.makeText(this, R.string.pictureCaptureError, Toast.LENGTH_LONG).show();
                }
            }
        }

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            try
            {
                InputStream imageStream = new FileInputStream(_imageCaptureFile.getPath());
                int resizeScale = calcImageResizeScale(imageStream);
                imageStream.close();

                // The stream needs to be opened a second time, now to
                // finally load in the bitmap
                imageStream = new FileInputStream(_imageCaptureFile.getPath());
                Bitmap bitmap = loadBitmapWithResizeScale(imageStream, resizeScale);
                imageStream.close();

                if(bitmap == null)
                {
                    throw new IOException("Failed to load image file");
                }

                String rotation = getImageRotation(_imageCaptureFile.getAbsolutePath());
                saveImageInBackground(bitmap, rotation);
            }
            catch(IOException e)
            {
                Log.w(TAG, "Failed to capture image to save", e);
                Toast.makeText(this, R.string.pictureCaptureError, Toast.LENGTH_LONG).show();
            }

        }

        if(requestCode == REQUEST_VIEW_IMAGE && resultCode == RESULT_OK && data != null)
        {
            Bundle extras = data.getExtras();
            if(extras != null && extras.containsKey("delete"))
            {
                String filePath = extras.getString("delete");
                File file = new File(filePath);

                for(Picture picture : _pictures)
                {
                    File toCheck = new File(picture.uri.getPath());
                    if(file.equals(toCheck))
                    {
                        _pictures.remove(picture);

                        _adapter.notifyDataSetChanged();
                        saveProperty();

                        Log.i(TAG, "Removed picture: " + filePath);

                        break;
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy()
    {
        _db.close();
        super.onDestroy();
    }
}

class Picture
{
    Uri uri;

    Picture(File file)
    {
        uri = Uri.parse(file.toURI().toString());
    }
}

class PictureAdapter extends ArrayAdapter<Picture>
{
    private static final String TAG = "RentalCalc";
    private Activity _activity;
    private final int REQUEST_VIEW_IMAGE_TAG;

    PictureAdapter(Activity activity, List<Picture> items, int requestViewImageTag)
    {
        super(activity, 0, items);
        _activity = activity;
        REQUEST_VIEW_IMAGE_TAG = requestViewImageTag;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view;

        if (convertView == null)
        {
            view = LayoutInflater.from(_activity).inflate(R.layout.picture_grid_item, parent, false);
        }
        else
        {
            view = convertView;
        }

        ImageView imageView = (ImageView)view.findViewById(R.id.image);

        imageView.setImageURI(null);

        final Picture picture = getItem(position);

        imageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(picture != null)
                {
                    Bundle bundle = new Bundle();
                    bundle.putString("file", picture.uri.getPath());
                    Intent i = new Intent(_activity, PictureViewActivity.class);
                    i.putExtras(bundle);
                    _activity.startActivityForResult(i, REQUEST_VIEW_IMAGE_TAG);
                }
            }
        });

        if(picture != null)
        {
            Glide.with(_activity).load(picture.uri).centerCrop().into(imageView);
        }

        return imageView;
    }
}
