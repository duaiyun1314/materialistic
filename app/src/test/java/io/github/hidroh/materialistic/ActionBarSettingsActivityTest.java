package io.github.hidroh.materialistic;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowPreferenceManager;
import org.robolectric.util.ActivityController;

import io.github.hidroh.materialistic.data.AlgoliaClient;
import io.github.hidroh.materialistic.test.ShadowSearchRecentSuggestions;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

@Config(shadows = ShadowSearchRecentSuggestions.class)
@RunWith(RobolectricTestRunner.class)
public class ActionBarSettingsActivityTest {
    private ActionBarSettingsActivity activity;
    private ActivityController<ActionBarSettingsActivity> controller;

    @Before
    public void setUp() {
        TestApplication.applicationGraph.inject(this);
        controller = Robolectric.buildActivity(ActionBarSettingsActivity.class);
        activity = controller.create().start().resume().visible().get();
    }

    @Test
    public void testClearRecentSearches() {
        ShadowSearchRecentSuggestions.historyClearCount = 0;
        assertNotNull(shadowOf(activity).getOptionsMenu().findItem(R.id.menu_clear_recent));
        shadowOf(activity).clickMenuItem(R.id.menu_clear_recent);
        AlertDialog alertDialog = ShadowAlertDialog.getLatestAlertDialog();
        assertNotNull(alertDialog);
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
        assertEquals(1, ShadowSearchRecentSuggestions.historyClearCount);
    }

    @Test
    public void testPrefSearch() {
        assertTrue(AlgoliaClient.sSortByTime);
        String key = activity.getString(R.string.pref_search_sort);
        // change
        ShadowPreferenceManager.getDefaultSharedPreferences(activity)
                .edit()
                .putString(key, activity.getString(R.string.pref_search_sort_value_default))
                .commit();
        // trigger listener
        activity.getSharedPreferences("io.github.hidroh.materialistic_preferences", Context.MODE_PRIVATE)
                .edit()
                .putString(key, activity.getString(R.string.pref_search_sort_value_default))
                .commit();
        assertFalse(AlgoliaClient.sSortByTime);
    }

    @Test
    public void testPrefTheme() {
        String key = activity.getString(R.string.pref_theme);
        // trigger listener
        activity.getSharedPreferences("io.github.hidroh.materialistic_preferences", Context.MODE_PRIVATE)
                .edit()
                .putString(key, activity.getString(R.string.pref_theme_value_dark))
                .commit();
        assertNotNull(shadowOf(activity).getNextStartedActivity());
    }

    @Test
    public void testPrefFont() {
        String key = activity.getString(R.string.pref_text_size);
        // trigger listener
        activity.getSharedPreferences("io.github.hidroh.materialistic_preferences", Context.MODE_PRIVATE)
                .edit()
                .putString(key, "1")
                .commit();
        assertNotNull(shadowOf(activity).getNextStartedActivity());
    }

    @After
    public void tearDown() {
        AlgoliaClient.sSortByTime = true;
        controller.pause().stop().destroy();
    }
}
