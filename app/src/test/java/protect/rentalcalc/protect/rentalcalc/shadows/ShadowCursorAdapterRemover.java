package protect.rentalcalc.protect.rentalcalc.shadows;

import android.widget.CursorAdapter;

import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowBaseAdapter;

/**
 * The ShadowBaseAdapter in Robolectric prevents one from testing a subclass
 * of BaseAdapter (such as CursorAdapter). Using this shadow removes
 * the usage of the original ShadowBaseAdapter.
 *
 * This should be removed in a future Robolectric, but is needed for now.
 * See https://github.com/robolectric/robolectric/issues/3111
 */
@Implements(CursorAdapter.class)
public class ShadowCursorAdapterRemover extends ShadowBaseAdapter
{
    // don't implement anything
}
