package protect.rentalcalc;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowListView;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.ShadowToast;

import java.lang.reflect.Field;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class PropertiesListActivityTest
{
    private DBHelper db;

    @Before
    public void setUp()
    {
        // Output logs emitted during tests so they may be accessed
        ShadowLog.stream = System.out;
        db = new DBHelper(RuntimeEnvironment.application);
    }

    @After
    public void tearDown()
    {
        db.close();
    }


    @Test
    public void clickAddLaunchesActivity()
    {
        ActivityController controller = Robolectric.buildActivity(PropertiesListActivity.class).create();
        Activity activity = (Activity)controller.get();

        controller.start();
        controller.visible();
        controller.resume();

        shadowOf(activity).clickMenuItem(R.id.action_add);
        assertTrue(activity.isFinishing() == false);

        Intent next = shadowOf(activity).getNextStartedActivity();

        ComponentName componentName = next.getComponent();
        String name = componentName.flattenToShortString();
        assertEquals("protect.rentalcalc/.PropertyViewActivity", name);

        Bundle extras = next.getExtras();
        assertNull(extras);
    }

    @Test
    public void checkNoProperties()
    {
        ActivityController controller = Robolectric.buildActivity(PropertiesListActivity.class).create();
        Activity activity = (Activity)controller.get();

        controller.start();
        controller.visible();
        controller.resume();

        View helpText = activity.findViewById(R.id.helpText);
        assertEquals(View.VISIBLE, helpText.getVisibility());

        View list = activity.findViewById(R.id.list);
        assertEquals(View.GONE, list.getVisibility());
    }
}
