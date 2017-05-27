package protect.rentalcalc;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.webkit.WebView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.shadows.ShadowWebView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class PictureViewActivityTest
{
    @Before
    public void setUp()
    {
        // Output logs emitted during tests so they may be accessed
        ShadowLog.stream = System.out;
    }

    @Test
    public void startWithoutBundle()
    {
        ActivityController controller = Robolectric.buildActivity(PictureViewActivity.class).create();
        Activity activity = (Activity)controller.get();

        controller.start();
        assertTrue(activity.isFinishing());

        String latestToast = ShadowToast.getTextOfLatestToast();
        assertNotNull(latestToast);
    }

    @Test
    public void startWithoutTitle()
    {
        Intent intent = new Intent();

        final Bundle bundle = new Bundle();
        intent.putExtras(bundle);

        ActivityController controller = Robolectric.buildActivity(PictureViewActivity.class, intent).create();
        Activity activity = (Activity)controller.get();

        controller.start();
        assertTrue(activity.isFinishing());

        String latestToast = ShadowToast.getTextOfLatestToast();
        assertNotNull(latestToast);
    }

    private ActivityController startWithFile(File file) throws IOException
    {
        Intent intent = new Intent();

        final Bundle bundle = new Bundle();
        bundle.putString("file", file.getAbsolutePath());
        intent.putExtras(bundle);

        ActivityController controller = Robolectric.buildActivity(PictureViewActivity.class, intent).create();
        Activity activity = (Activity)controller.get();

        controller.start();
        controller.visible();
        controller.resume();
        assertTrue(activity.isFinishing() == false);

        String latestToast = ShadowToast.getTextOfLatestToast();
        assertNull(latestToast);

        return controller;
    }

    @Test
    public void clickBackFinishes() throws IOException
    {
        File file = new File(Environment.getExternalStorageDirectory(), "test.jpg");
        file.createNewFile();

        ActivityController controller = startWithFile(file);
        Activity activity = (Activity)controller.get();
        ShadowActivity shadowActivity = shadowOf(activity);

        assertTrue(shadowActivity.isFinishing() == false);
        shadowActivity.clickMenuItem(android.R.id.home);
        assertTrue(shadowActivity.isFinishing());

        // Check that can finish out the lifecycle
        controller.pause();
        controller.stop();
        controller.destroy();

        // The done menu item was not clicked, so the result should be that
        // the activity was canceled
        assertEquals(Activity.RESULT_CANCELED, shadowActivity.getResultCode());
        assertNull(shadowActivity.getResultIntent());
    }

    @Test
    public void clickDeleteFinishes() throws IOException
    {
        File file = new File(Environment.getExternalStorageDirectory(), "test.jpg");
        file.createNewFile();

        ActivityController controller = startWithFile(file);
        Activity activity = (Activity)controller.get();
        ShadowActivity shadowActivity = shadowOf(activity);

        assertTrue(shadowActivity.isFinishing() == false);
        boolean result = shadowActivity.clickMenuItem(R.id.action_delete);
        assertTrue(result);

        // Note, cannot finish the test because the v7 AlertDialog
        // currently does not have shadow support.
    }
}
