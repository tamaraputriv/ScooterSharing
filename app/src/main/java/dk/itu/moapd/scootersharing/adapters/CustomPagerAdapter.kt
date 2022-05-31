package dk.itu.moapd.scootersharing.adapters

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import dk.itu.moapd.scootersharing.fragments.CurrentRideFragment
import dk.itu.moapd.scootersharing.fragments.ListFragment
import dk.itu.moapd.scootersharing.fragments.MapFragment

/**
 * A class to customize an adapter with a `FragmentStateAdapter` to manage a set of fragments
 * showing in different pages.
 */
class CustomPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    /**
     * A set of private constants used in this class.
     */
    companion object {
        private val TAG = CustomPagerAdapter::class.qualifiedName
        private const val NUM_PAGES = 3
    }

    /**
     * Returns the total number of pages in the `ViewPager`.
     *
     * @return The total number of pages managed by this adapter.
     */
    override fun getItemCount() = NUM_PAGES

    /**
     * Provide a new Fragment associated with the specified position.
     *
     * The adapter will be responsible for the Fragment lifecycle:
     * * The Fragment will be used to display an item.
     * * The Fragment will be destroyed when it gets too far from the viewport, and its state will
     *      be saved. When the item is close to the viewport again, a new Fragment will be
     *      requested, and a previously saved state will be used to initialize it.
     *
     * @param position The position of current `Fragment` in the `ViewPager`
     */
    override fun createFragment(position: Int): Fragment {
        Log.i(TAG, "Creating Fragment $position")
        return when (position) {
            0 -> ListFragment()
            1 -> MapFragment()
            2 -> CurrentRideFragment()
            else -> MapFragment()
        }
    }

}