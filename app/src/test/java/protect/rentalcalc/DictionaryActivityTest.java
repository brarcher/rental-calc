package protect.rentalcalc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.ShadowToast;

import io.github.kexanie.library.MathView;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class DictionaryActivityTest
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
    public void clickBackFinishes()
    {
        ActivityController controller = startWithRequest(new DictionaryItem(R.string.addressTitle, R.string.addressTitle));
        Activity activity = (Activity)controller.get();

        assertTrue(shadowOf(activity).isFinishing() == false);
        shadowOf(activity).clickMenuItem(android.R.id.home);
        assertTrue(shadowOf(activity).isFinishing());

        // Check that can finish out the lifecycle
        controller.pause();
        controller.stop();
        controller.destroy();
    }

    @Test
    public void startWithoutBundle()
    {
        ActivityController controller = Robolectric.buildActivity(DictionaryActivity.class).create();
        Activity activity = (Activity)controller.get();

        controller.start();
        controller.visible();
        controller.resume();
        assertTrue(activity.isFinishing());

        String latestToast = ShadowToast.getTextOfLatestToast();
        assertNotNull(latestToast);
    }

    @Test
    public void startWithoutRequest()
    {
        Intent intent = new Intent();
        final Bundle bundle = new Bundle();
        intent.putExtras(bundle);

        ActivityController controller = Robolectric.buildActivity(DictionaryActivity.class, intent).create();
        Activity activity = (Activity)controller.get();

        controller.start();
        controller.visible();
        controller.resume();
        assertTrue(activity.isFinishing());

        String latestToast = ShadowToast.getTextOfLatestToast();
        assertNotNull(latestToast);
    }

    private ActivityController startWithRequest(DictionaryItem item)
    {
        Intent intent = new Intent();
        final Bundle bundle = new Bundle();
        bundle.putInt("title", item.titleId);
        bundle.putInt("definition", item.definitionId);
        if(item.formulaId != null)
        {
            bundle.putInt("formula", item.formulaId);
        }
        intent.putExtras(bundle);

        ActivityController controller = Robolectric.buildActivity(DictionaryActivity.class, intent).create();
        Activity activity = (Activity)controller.get();

        controller.start();
        controller.visible();
        controller.resume();
        assertTrue(activity.isFinishing() == false);

        String latestToast = ShadowToast.getTextOfLatestToast();
        assertNull(latestToast);

        return controller;
    }

    private void checkContents(Activity  activity, DictionaryItem item)
    {
        String title = activity.getResources().getString(item.titleId);
        assertEquals(title, activity.getTitle().toString());

        String body = activity.getResources().getString(item.definitionId);
        TextView defView = (TextView)activity.findViewById(R.id.definition);
        assertEquals(body, defView.getText().toString());

        int [] formulaViews = new int[]{
                R.id.formulaTitleBorder,
                R.id.formulaTitleRow,
                R.id.formulaTextBorderStart,
                R.id.formulaTextRow};

        for(int id : formulaViews)
        {
            View view = activity.findViewById(id);
            assertEquals(item.formulaId != null ? View.VISIBLE : View.GONE, view.getVisibility());
        }

        if(item.formulaId != null)
        {
            String formula = activity.getResources().getString(item.formulaId);
            MathView mathView = (MathView)activity.findViewById(R.id.formula);
            assertEquals(formula, mathView.getText());
        }
    }

    @Test
    public void startWithDefinition()
    {
        DictionaryItem item = new DictionaryItem(R.string.addressTitle, R.string.addressTitle);
        ActivityController controller = startWithRequest(item);
        Activity activity = (Activity)controller.get();

        checkContents(activity, item);
    }

    @Test
    public void startWithDefinitionAndFormula()
    {
        DictionaryItem item = new DictionaryItem(R.string.addressTitle, R.string.addressTitle, R.string.about);
        ActivityController controller = startWithRequest(item);
        Activity activity = (Activity)controller.get();

        checkContents(activity, item);
    }
}
