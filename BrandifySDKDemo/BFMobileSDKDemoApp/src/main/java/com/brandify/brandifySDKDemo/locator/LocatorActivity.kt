package com.brandify.brandifySDKDemo.locator

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import com.brandify.BrandifyMobileSDK.models.BFListOfLocations
import com.brandify.BrandifyMobileSDK.models.BFLocation
import com.brandify.BrandifyMobileSDK.models.BFRunnable
import com.brandify.brandifySDKDemo.R
import com.brandify.brandifySDKDemo.baseActivity.BaseActivity
import com.brandify.brandifySDKDemo.utils.BFUtils
import com.brandify.brandifySDKDemo.waitlist.WaitlistActivity1
import java.util.*

class LocatorActivity : BaseActivity() {
    var locationList: BFListOfLocations? = BFListOfLocations()
    var listOfFavorites: MutableList<String> = ArrayList()

    //Locator Buttons
    var viewTitle: TextView? = null
    var locatorCurLocationButton: ImageButton? = null
    var locatorSearchButton: ImageButton? = null
    var locatorListViewButton: ImageButton? = null
    var locatorMapFragment: LocatorMapFragment? = null
    var locatorListFragment: LocatorListFragment? = null
    var locatorSearchFragment: LocatorSearchFragment? = null
    private var listAddedFlag = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //for debugging
        //deleteFile(FAVORITES_FILE_NAME);
        setInitialViews()
    }

    private fun setInitialViews() {
        listOfFavorites = fetchFavoritesFromDisk()
        locatorListFragment = LocatorListFragment()
        if (locationList != null) {
            locatorListFragment?.listAdapter = LocatorListAdapter(this, locationList!!)
        }
        locatorListFragment?.setListAdapter(locatorListFragment?.listAdapter)
        locatorMapFragment = LocatorMapFragment()
        locatorSearchFragment = LocatorSearchFragment()
        viewTitle = findViewById<View>(R.id.view_title) as TextView
        locatorSearchButton = findViewById<View>(R.id.locator_search_button_id) as ImageButton
        locatorCurLocationButton = findViewById<View>(R.id.locator_current_location_button_id) as ImageButton
        locatorListViewButton = findViewById<View>(R.id.locator_listview_button_id) as ImageButton
        locatorListViewButton?.setBackgroundResource(R.drawable.icon_list)
        viewTitle?.text = "Locator"
        setInitialMainFrameContent()
        locatorSearchButton?.setOnClickListener { openSearch() }
    }

    private fun setInitialMainFrameContent() {
        //Load the main product menu fragment
        if (locatorMapFragment != null) {
            supportFragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.mainContentsFrameLayout, locatorMapFragment!!).commit()
        }

        //add the search fragment as hidden - initially
        if (locatorSearchFragment != null) {
            val fTransaction = supportFragmentManager.beginTransaction()
            fTransaction.add(R.id.mainContentsFrameLayout, locatorSearchFragment!!).hide(locatorSearchFragment!!).commit()
            setButtonAsMapFragment()
        }
    }

    private fun mapToList() {
        setButtonAsListFragment()
        if (!listAddedFlag) {
            listAddedFlag = true
            if (locatorSearchFragment != null && locatorListFragment != null && locatorMapFragment != null) {
                val fTransaction = supportFragmentManager.beginTransaction().hide(locatorMapFragment!!).hide(locatorSearchFragment!!)
                fTransaction.add(R.id.mainContentsFrameLayout, locatorListFragment!!).show(locatorListFragment!!).commit()
            }
        } else {
            if (locationList != null) {
                (locatorListFragment?.getListAdapter() as LocatorListAdapter?)?.locationList = locationList!!
            }
            (locatorListFragment?.getListAdapter() as LocatorListAdapter?)?.notifyDataSetChanged()

            if (locatorSearchFragment != null && locatorListFragment != null && locatorMapFragment != null) {
                val fTransaction = supportFragmentManager.beginTransaction().hide(locatorMapFragment!!).hide(locatorSearchFragment!!)
                fTransaction.show(locatorListFragment!!).commit()
            }
        }
    }

    private fun setButtonAsListFragment() {
        //Setup Menu buttons
        locatorListViewButton?.setBackgroundResource(R.drawable.icon_map)
        locatorListViewButton?.setOnClickListener { listToMap() }
    }

    private fun listToMap() {
        setButtonAsMapFragment()
        if (locatorSearchFragment != null && locatorListFragment != null && locatorMapFragment != null) {
            val fTransaction = supportFragmentManager.beginTransaction().hide(locatorListFragment!!).hide(locatorSearchFragment!!)
            fTransaction.show(locatorMapFragment!!).commit()
        }
    }

    private fun openSearch() {
        if (locatorSearchFragment != null && locatorListFragment != null && locatorMapFragment != null) {
            val fTransaction = supportFragmentManager.beginTransaction().hide(locatorListFragment!!).hide(locatorMapFragment!!)
            fTransaction.show(locatorSearchFragment!!).commit()
        }
    }

    fun closeSearch() {
        listToMap()
    }

    fun runSearch(searchText: String?, filters: HashMap<String, String>) {
        locatorMapFragment?.runSearch(searchText, filters)
    }

    fun listToMapWithLocation(location: BFLocation?) {
        listToMap()
        if (location != null) {
            locatorMapFragment?.openInfoView(location)
        }
    }

    private fun setButtonAsMapFragment() {
        //Setup Menu buttons
        locatorListViewButton?.setBackgroundResource(R.drawable.icon_list)
        locatorListViewButton?.setOnClickListener {
            if (locationList != null) {
                mapToList()
            } else {
                val toast = Toast.makeText(applicationContext, "Locations not available", Toast.LENGTH_SHORT)
                toast.show()
            }
        }
    }

    fun updateListView(listOfLocations: BFListOfLocations?) {
        locationList = listOfLocations
        val adapter = locatorListFragment?.getListAdapter() as LocatorListAdapter?
        if (adapter != null) {
            if (locationList != null) {
                adapter.locationList = locationList!!
            }
            adapter.notifyDataSetChanged()
        }
    }

    fun updateFavoritesListView(favorites: MutableList<String>) {
        listOfFavorites = favorites
        val adapter = locatorListFragment?.getListAdapter() as LocatorListAdapter?
        if (adapter != null) {
            adapter.favoritesList = listOfFavorites
            adapter.notifyDataSetChanged()
        }
    }

    fun startWaitListFlow(location: BFLocation) {
        val intent = Intent(this.applicationContext, WaitlistActivity1::class.java)
        intent.putExtra("ClientKey", location.clientKey)
        var address = location.address1
        if (location.address2 != null) {
            address += location.address2
        }
        if (location.city != null) {
            address += """
                
                ${location.city}
                """.trimIndent()
        }
        if (location.state != null && location.state.isNotEmpty()) {
            address += ", " + location.state
        }
        if (location.postalCode != null) {
            address += " " + location.postalCode
        }
        intent.putExtra("LocationAddress", address)
        startActivityForResult(intent, 0)
    }

    fun addToFavorites(location: BFLocation?) {
        BFUtils().addToFavoritesList(application, location, object : BFRunnable<BFLocation?>() {
            override fun run() {
                if (model != null) {
                    listOfFavorites.add(model!!.clientKey)
                    updateFavoritesListView(listOfFavorites)
                }
            }
        })
    }

    fun removeFromFavorites(location: BFLocation?) {
        BFUtils().removeFromFavoritesList(application, location, object : BFRunnable<BFLocation?>() {
            override fun run() {
                if (model != null) {
                    listOfFavorites.clear()
                    listOfFavorites = fetchFavoritesFromDisk()
                    updateFavoritesListView(listOfFavorites)
                }
            }
        })
    }

    fun fetchFavoritesFromDisk(): MutableList<String> {
        val newList: MutableList<String> = ArrayList()
        val favs = BFUtils().getFavoritesList(application)
        val arrSt = favs.split(",").toTypedArray()
        for (s in arrSt) {
            if (!s.equals("", ignoreCase = true)) {
                newList.add(s)
            }
        }
        return newList
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //in any case, when the waitlist flow is complete, refresh list
        super.onActivityResult(requestCode, resultCode, data)
        val adapter = locatorListFragment?.getListAdapter() as LocatorListAdapter?
        if (adapter != null) {
            if (locationList != null) {
                adapter.locationList = locationList!!
            }
            adapter.notifyDataSetChanged()
        }
    }

    override fun finish() {}
    override val content: Int
        get() = R.layout.activity_locator

    override fun thisClass(): Activity {
        return this
    }
}