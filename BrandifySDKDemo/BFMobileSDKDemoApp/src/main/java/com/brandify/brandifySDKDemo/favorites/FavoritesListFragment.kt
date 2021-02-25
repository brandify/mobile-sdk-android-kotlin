package com.brandify.brandifySDKDemo.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.ListFragment
import com.brandify.BrandifyMobileSDK.common.connection.BFContext
import com.brandify.BrandifyMobileSDK.models.BFListOfLocations
import com.brandify.BrandifyMobileSDK.models.BFRunnable
import com.brandify.BrandifyMobileSDK.models.BFSearchLocation
import com.brandify.BrandifyMobileSDK.providers.BFGetListDP
import com.brandify.brandifySDKDemo.R
import com.brandify.brandifySDKDemo.utils.BFUtils
import java.util.*

class FavoritesListFragment : ListFragment() {
    var favoritesList: BFListOfLocations? = null
    var listAdapter: FavoritesListAdapter? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.favorites_fragment, container, false)
    }

    override fun onResume() {
        super.onResume()
        fetchFavorites()
    }

    private fun fetchFavorites() {
        val bfUtils = BFUtils()
        val favorites = bfUtils.getFavoritesList(activity)
        if (favoritesList != null && favoritesList?.locationList != null) {
            favoritesList?.locationList?.clear()
        }
        if (!favorites.equals("", ignoreCase = true)) {
            getListOfFavorites(favorites)
        } else if (favorites == "") {
            (activity as FavoritesActivity?)?.updateListView(favoritesList)
            (getListAdapter() as FavoritesListAdapter?)?.notifyDataSetChanged()
            val toast = Toast.makeText(context, "" + "No favorites found.", Toast.LENGTH_SHORT)
            toast.show()
        }
    }

    private fun getListOfFavorites(favoriteKeys: String) {
        val bfContext = BFContext(null)
        val searchLocation = BFSearchLocation()
        val inMap = HashMap<String, String>()
        inMap["in"] = favoriteKeys
        searchLocation.getListProperties["clientkey"] = inMap
        searchLocation.searchType = BFSearchLocation.BFSearchType.GETLIST
        val getListDP = BFGetListDP()
        getListDP.getListOfLocations(bfContext, searchLocation, object : BFRunnable<BFListOfLocations?>() {
            override fun run() {
                if (model?.status?.code == 1) {
                    if (favoritesList == null) {
                        favoritesList = BFListOfLocations()
                    }
                    favoritesList?.locationList = model?.locationList
                    (activity as FavoritesActivity?)?.updateListView(favoritesList)
                    (getListAdapter() as FavoritesListAdapter?)?.notifyDataSetChanged()
                    if (favoritesList?.locationList?.size == 0) {
                        val toast = Toast.makeText(context, "" + "No favorites found.", Toast.LENGTH_SHORT)
                        toast.show()
                    }
                } else if (model?.status?.code == -1) {
                    val toast = Toast.makeText(context, "" + model?.status?.description, Toast.LENGTH_SHORT)
                    toast.show()
                }
            }
        })
    }

    override fun onListItemClick(l: ListView, v: View, pos: Int, id: Long) {
        val parentActivity = activity as FavoritesActivity?
        //Do something
    }
}