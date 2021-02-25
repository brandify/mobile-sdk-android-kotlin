package com.brandify.brandifySDKDemo.favorites

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import com.brandify.BrandifyMobileSDK.models.BFListOfLocations
import com.brandify.BrandifyMobileSDK.models.BFLocation
import com.brandify.brandifySDKDemo.R
import com.brandify.brandifySDKDemo.baseActivity.BaseActivity
import com.brandify.brandifySDKDemo.locator.LocatorDetailsActivity

class FavoritesActivity : BaseActivity() {
    private var favoritesList: BFListOfLocations? = null

    //Locator Buttons
    private var viewTitle: TextView? = null
    private var favoritesListFragment: FavoritesListFragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setInitialViews()
    }

    private fun setInitialViews() {
        favoritesListFragment = FavoritesListFragment()
        favoritesListFragment?.listAdapter = FavoritesListAdapter(this, favoritesList)
        favoritesListFragment?.setListAdapter(favoritesListFragment?.listAdapter)
        viewTitle = findViewById<View>(R.id.view_title) as TextView
        viewTitle?.text = "Favorites"
        setInitialMainFrameContent()
    }

    private fun setInitialMainFrameContent() {
        if (favoritesListFragment != null) {
            supportFragmentManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.mainContentsFrameLayout, favoritesListFragment!!).commit()
        }
    }

    fun updateListView(listOfFavorites: BFListOfLocations?) {
        favoritesList = listOfFavorites
        val adapter = favoritesListFragment?.getListAdapter() as FavoritesListAdapter?
        adapter?.favoritesList = favoritesList
        adapter?.notifyDataSetChanged()
    }

    fun showDetails(location: BFLocation) {
        selectedLocation = location
        val intent = Intent(thisClass(), LocatorDetailsActivity::class.java)
        intent.putExtra("activity", "FavoritesActivity")
        startActivity(intent)
    }

    override fun finish() {}
    override val content: Int
        get() = R.layout.activity_favorites

    override fun thisClass(): Activity {
        return this
    }

    companion object {
        @JvmStatic
        var selectedLocation: BFLocation? = null
    }
}