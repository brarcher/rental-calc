package protect.rentalcalc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class PropertyNotesActivityTest
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
        ActivityController controller = startWithNotes("");
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
    public void checkActionBar() throws Exception
    {
        ActivityController controller = startWithNotes("");
        Activity activity = (Activity)controller.get();

        final Menu menu = shadowOf(activity).getOptionsMenu();
        assertNotNull(menu);

        assertEquals(menu.size(), 1);

        MenuItem item = menu.findItem(R.id.action_save);
        assertNotNull(item);
        assertEquals("Save", item.getTitle().toString());
    }

    @Test
    public void startWithoutBundle()
    {
        ActivityController controller = Robolectric.buildActivity(PropertyNotesActivity.class).create();
        Activity activity = (Activity)controller.get();

        assertTrue(activity.isFinishing());

        String latestToast = ShadowToast.getTextOfLatestToast();
        assertNotNull(latestToast);
    }

    @Test
    public void startWithoutProperty()
    {
        Intent intent = new Intent();
        final Bundle bundle = new Bundle();
        intent.putExtras(bundle);

        ActivityController controller = Robolectric.buildActivity(PropertyNotesActivity.class, intent).create();
        Activity activity = (Activity)controller.get();

        assertTrue(activity.isFinishing());

        String latestToast = ShadowToast.getTextOfLatestToast();
        assertNotNull(latestToast);
    }

    @Test
    public void startWithMissingProperty()
    {
        Intent intent = new Intent();
        final Bundle bundle = new Bundle();
        bundle.putLong("id", 0);
        intent.putExtras(bundle);

        ActivityController controller = Robolectric.buildActivity(PropertyNotesActivity.class, intent).create();
        Activity activity = (Activity)controller.get();

        assertTrue(activity.isFinishing());

        String latestToast = ShadowToast.getTextOfLatestToast();
        assertNotNull(latestToast);
    }

    private ActivityController startWithNotes(String notes)
    {
        Property property = new Property();
        property.notes = notes;

        long id = db.insertProperty(property);

        Intent intent = new Intent();
        final Bundle bundle = new Bundle();
        bundle.putLong("id", id);
        intent.putExtras(bundle);

        ActivityController controller = Robolectric.buildActivity(PropertyNotesActivity.class, intent).create();
        Activity activity = (Activity)controller.get();

        assertTrue(activity.isFinishing() == false);

        controller.start();
        controller.visible();
        controller.resume();

        String latestToast = ShadowToast.getTextOfLatestToast();
        assertNull(latestToast);

        return controller;
    }

    @Test
    public void startWithNullNotes()
    {
        ActivityController controller = startWithNotes(null);
        Activity activity = (Activity)controller.get();

        EditText notes = (EditText)activity.findViewById(R.id.notes);
        assertEquals("", notes.getText().toString());
    }

    @Test
    public void startWithBlankNotes()
    {
        ActivityController controller = startWithNotes("");
        Activity activity = (Activity)controller.get();

        EditText notes = (EditText)activity.findViewById(R.id.notes);
        assertEquals("", notes.getText().toString());
    }

    @Test
    public void startWithNotes()
    {
        final String MESSAGE = "Multi\nLine\nMessage\n";
        ActivityController controller = startWithNotes(MESSAGE);
        Activity activity = (Activity)controller.get();

        EditText notes = (EditText)activity.findViewById(R.id.notes);
        assertEquals(MESSAGE, notes.getText().toString());
    }

    @Test
    public void updateNotes()
    {
        final String ORIGINAL_MESSAGE = "Multi\nLine\nMessage\n";
        ActivityController controller = startWithNotes(ORIGINAL_MESSAGE);
        Activity activity = (Activity)controller.get();

        EditText notes = (EditText)activity.findViewById(R.id.notes);
        final String NEW_MESSAGE = "This is the new message";
        notes.setText(NEW_MESSAGE);

        assertNotNull(shadowOf(activity).getOptionsMenu().findItem(R.id.action_save));
        shadowOf(activity).clickMenuItem(R.id.action_save);

        assertTrue(activity.isFinishing());

        Property property = db.getProperty(DatabaseTestHelper.FIRST_ID);
        assertNotNull(property);
        assertEquals(NEW_MESSAGE, property.notes);
    }

    @Test
    public void cancelDoesNotUpdateNotes()
    {
        final String ORIGINAL_MESSAGE = "Multi\nLine\nMessage\n";
        ActivityController controller = startWithNotes(ORIGINAL_MESSAGE);
        Activity activity = (Activity)controller.get();

        EditText notes = (EditText)activity.findViewById(R.id.notes);
        final String NEW_MESSAGE = "This is the new message";
        notes.setText(NEW_MESSAGE);

        shadowOf(activity).clickMenuItem(android.R.id.home);

        assertTrue(activity.isFinishing());

        Property property = db.getProperty(DatabaseTestHelper.FIRST_ID);
        assertNotNull(property);
        assertEquals(ORIGINAL_MESSAGE, property.notes);
    }
}
