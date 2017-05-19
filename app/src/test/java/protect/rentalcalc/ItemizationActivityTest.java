package protect.rentalcalc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.google.common.collect.ImmutableMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowListView;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.ShadowToast;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class ItemizationActivityTest
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
        ActivityController controller = Robolectric.buildActivity(ItemizeActivity.class).create();
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
        bundle.putInt("description", R.string.app_name);
        bundle.putSerializable("items", new HashMap<String, Integer>());
        intent.putExtras(bundle);

        ActivityController controller = Robolectric.buildActivity(ItemizeActivity.class, intent).create();
        Activity activity = (Activity)controller.get();

        controller.start();
        assertTrue(activity.isFinishing());

        String latestToast = ShadowToast.getTextOfLatestToast();
        assertNotNull(latestToast);
    }

    @Test
    public void startWithoutDescription()
    {
        Intent intent = new Intent();

        final Bundle bundle = new Bundle();
        bundle.putInt("title", R.string.app_name);
        bundle.putSerializable("items", new HashMap<String, Integer>());
        intent.putExtras(bundle);

        ActivityController controller = Robolectric.buildActivity(ItemizeActivity.class, intent).create();
        Activity activity = (Activity)controller.get();

        controller.start();
        assertTrue(activity.isFinishing());

        String latestToast = ShadowToast.getTextOfLatestToast();
        assertNotNull(latestToast);
    }

    @Test
    public void startWithoutMap()
    {
        Intent intent = new Intent();

        final Bundle bundle = new Bundle();
        bundle.putInt("title", R.string.app_name);
        bundle.putInt("description", R.string.app_name);
        intent.putExtras(bundle);

        ActivityController controller = Robolectric.buildActivity(ItemizeActivity.class, intent).create();
        Activity activity = (Activity)controller.get();

        controller.start();
        assertTrue(activity.isFinishing());

        String latestToast = ShadowToast.getTextOfLatestToast();
        assertNotNull(latestToast);
    }

    private ActivityController startWithMap(Map<String, Integer> map)
    {
        Intent intent = new Intent();

        final Bundle bundle = new Bundle();
        bundle.putInt("title", R.string.app_name);
        bundle.putInt("description", R.string.app_name);
        bundle.putSerializable("items", new HashMap<>(map));
        intent.putExtras(bundle);

        ActivityController controller = Robolectric.buildActivity(ItemizeActivity.class, intent).create();
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
    public void clickBackFinishes()
    {
        ActivityController controller = startWithMap(new HashMap<String, Integer>());
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

    private void checkReturnedMap(Activity activity, Map<String, Integer> expected)
    {
        ShadowActivity shadowActivity = shadowOf(activity);

        assertEquals(Activity.RESULT_OK, shadowActivity.getResultCode());
        Intent intent = shadowActivity.getResultIntent();
        assertNotNull(intent);
        Bundle bundle = intent.getExtras();
        assertNotNull(bundle);
        assertTrue(bundle.containsKey("items"));
        HashMap<String, Integer> map = (HashMap<String, Integer>)bundle.getSerializable("items");
        assertNotNull(map);

        assertEquals(expected.size(), map.size());

        for(Map.Entry<String, Integer> item : expected.entrySet())
        {
            assertTrue("Did not contain: " + item.getKey(), map.containsKey(item.getKey()));
            assertEquals("Value did not match: " + item.getValue(), item.getValue(), map.get(item.getKey()));
        }
    }

    @Test
    public void doneNoItems()
    {
        HashMap<String, Integer> emptyMap = new HashMap<>();
        ActivityController controller = startWithMap(emptyMap);
        Activity activity = (Activity)controller.get();
        ShadowActivity shadowActivity = shadowOf(activity);

        assertTrue(shadowActivity.isFinishing() == false);
        shadowActivity.clickMenuItem(R.id.action_done);
        assertTrue(shadowActivity.isFinishing());

        // Check that can finish out the lifecycle
        controller.pause();
        controller.stop();
        controller.destroy();

        checkReturnedMap(activity, emptyMap);
    }

    @Test
    public void doneBlankItem()
    {
        HashMap<String, Integer> emptyMap = new HashMap<>();
        ActivityController controller = startWithMap(emptyMap);
        Activity activity = (Activity)controller.get();
        ShadowActivity shadowActivity = shadowOf(activity);

        ListView list = (ListView)activity.findViewById(R.id.list);
        assertNotNull(list);

        ShadowListView shadowList = shadowOf(list);
        shadowList.populateItems();

        assertEquals(0, list.getCount());
        shadowActivity.clickMenuItem(R.id.action_add);
        assertEquals(1, list.getCount());

        assertTrue(shadowActivity.isFinishing() == false);
        shadowActivity.clickMenuItem(R.id.action_done);
        assertTrue(shadowActivity.isFinishing());

        // Check that can finish out the lifecycle
        controller.pause();
        controller.stop();
        controller.destroy();

        checkReturnedMap(activity, emptyMap);
    }

    private void addItem(Activity activity, String name, Integer value)
    {
        ShadowActivity shadowActivity = shadowOf(activity);
        ListView list = (ListView)activity.findViewById(R.id.list);
        assertNotNull(list);

        int initialCount = list.getCount();

        shadowActivity.clickMenuItem(R.id.action_add);
        ItemizationAdapter adapter = (ItemizationAdapter)list.getAdapter();
        Itemization listItem = adapter.getItem(initialCount);
        assertNotNull(listItem);
        listItem.name = name;
        listItem.value = value;

        assertEquals(initialCount+1, list.getCount());
    }

    private void addItems(Activity activity, Map<String, Integer> items)
    {
        ListView list = (ListView)activity.findViewById(R.id.list);
        assertNotNull(list);

        ShadowListView shadowList = shadowOf(list);
        shadowList.populateItems();

        int initialCount = list.getCount();

        for(Map.Entry<String, Integer> item : items.entrySet())
        {
            addItem(activity, item.getKey(), item.getValue());
        }

        assertEquals(initialCount + items.size(), list.getCount());
    }

    @Test
    public void filledOutItemsNotSaved()
    {
        HashMap<String, Integer> emptyMap = new HashMap<>();
        ActivityController controller = startWithMap(emptyMap);
        Activity activity = (Activity)controller.get();
        ShadowActivity shadowActivity = shadowOf(activity);

        Map<String, Integer> addedValues = ImmutableMap.of
        (
            "test name", 12345
        );

        addItems(activity, addedValues);

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
    public void filledOutItemsClickedDone()
    {
        HashMap<String, Integer> emptyMap = new HashMap<>();
        ActivityController controller = startWithMap(emptyMap);
        Activity activity = (Activity)controller.get();
        ShadowActivity shadowActivity = shadowOf(activity);

        Map<String, Integer> addedValues = ImmutableMap.of
        (
            "test name", 12345,
            "test name 2", 44444,
            "test name 3", 75412,
            "test name 4", 951,
            "test name 5", 1
        );

        addItems(activity, addedValues);

        assertTrue(shadowActivity.isFinishing() == false);
        shadowActivity.clickMenuItem(R.id.action_done);
        assertTrue(shadowActivity.isFinishing());

        // Check that can finish out the lifecycle
        controller.pause();
        controller.stop();
        controller.destroy();

        checkReturnedMap(activity, addedValues);
    }

    @Test
    public void filledOutItemsWithDuplicatesClickedDone()
    {
        HashMap<String, Integer> emptyMap = new HashMap<>();
        ActivityController controller = startWithMap(emptyMap);
        Activity activity = (Activity)controller.get();
        ShadowActivity shadowActivity = shadowOf(activity);

        final String TEST_NAME = "test name";
        final int TEST_VALUE = 12345;

        for(int index = 0; index < 10; index++)
        {
            addItem(activity, TEST_NAME, TEST_VALUE);
        }

        assertTrue(shadowActivity.isFinishing() == false);
        shadowActivity.clickMenuItem(R.id.action_done);
        assertTrue(shadowActivity.isFinishing());

        controller.pause();
        controller.stop();
        controller.destroy();

        // As all the items were duplicates, only one item
        // should result.
        Map<String, Integer> expectedValues = ImmutableMap.of
        (
            TEST_NAME, TEST_VALUE
        );

        checkReturnedMap(activity, expectedValues);
    }

    @Test
    public void startWithItemsClickedDone()
    {
        Map<String, Integer> initialValues = ImmutableMap.of
        (
            "test name", 12345,
            "test name 2", 44444,
            "test name 3", 75412,
            "test name 4", 951,
            "test name 5", 1
        );

        ActivityController controller = startWithMap(initialValues);
        Activity activity = (Activity)controller.get();
        ShadowActivity shadowActivity = shadowOf(activity);

        assertTrue(shadowActivity.isFinishing() == false);
        shadowActivity.clickMenuItem(R.id.action_done);
        assertTrue(shadowActivity.isFinishing());

        // Check that can finish out the lifecycle
        controller.pause();
        controller.stop();
        controller.destroy();

        checkReturnedMap(activity, initialValues);
    }

    @Test
    public void startWithItemsAddedItemsClickedDone()
    {
        Map<String, Integer> initialValues = ImmutableMap.of
        (
                "test name 1", 12345,
                "test name 2", 44444,
                "test name 3", 75412,
                "test name 4", 951,
                "test name 5", 1
        );

        ActivityController controller = startWithMap(initialValues);
        Activity activity = (Activity)controller.get();

        Map<String, Integer> newValues = ImmutableMap.of
        (
            "test name 6", 401,
            "test name 7", 2,
            "test name 8", 6834534,
            "test name 9", 1234,
            "test name 10", 198765
        );
        addItems(activity, newValues);

        ShadowActivity shadowActivity = shadowOf(activity);
        assertTrue(shadowActivity.isFinishing() == false);
        shadowActivity.clickMenuItem(R.id.action_done);
        assertTrue(shadowActivity.isFinishing());

        // Check that can finish out the lifecycle
        controller.pause();
        controller.stop();
        controller.destroy();

        Map<String, Integer> finalValues = new ImmutableMap.Builder<String, Integer>()
                .putAll(initialValues)
                .putAll(newValues)
                .build();

        checkReturnedMap(activity, finalValues);
    }
}
