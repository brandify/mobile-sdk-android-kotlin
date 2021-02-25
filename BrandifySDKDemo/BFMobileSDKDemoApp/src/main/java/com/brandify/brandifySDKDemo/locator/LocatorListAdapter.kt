package com.brandify.brandifySDKDemo.locator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.brandify.BrandifyMobileSDK.models.BFListOfLocations
import com.brandify.BrandifyMobileSDK.models.BFLocation
import com.brandify.BrandifyMobileSDK.providers.BFWaitListDP
import com.brandify.brandifySDKDemo.R
import com.brandify.brandifySDKDemo.utils.BFUtils
import com.squareup.picasso.Picasso
import java.util.*

class LocatorListAdapter(var parentActivity: LocatorActivity, var locationList: BFListOfLocations) : ArrayAdapter<BFLocation?>(parentActivity.applicationContext, -1) {
    internal class ViewHolder {
        var pinImage: ImageView? = null
        var distanceText: TextView? = null
        var titleText: TextView? = null
        var addressText: TextView? = null
        var cityText: TextView? = null
        var phoneText: TextView? = null
        var avgWaitTimeText: TextView? = null
        var storeImage: ImageView? = null
        var waitButton: Button? = null
        var favoritesButton: Button? = null
    }

    //private boolean isFavorite;
    @JvmField
    var favoritesList: List<String> = ArrayList()
    override fun getCount(): Int {
        return locationList.locationList.size
    }

    override fun getView(pos: Int, convertViewParam: View?, parent: ViewGroup): View? {
        var convertView = convertViewParam
        val location = locationList.locationList[pos]
        val isFavorite = BFUtils().searchFavorites(context, location.clientKey)
        val holder: ViewHolder
        if (convertView == null) {
            val inflater = LayoutInflater.from(parentActivity.applicationContext)
            convertView = inflater.inflate(R.layout.location_list_cell, null)
            holder = ViewHolder()
            holder.pinImage = convertView.findViewById<View>(R.id.locator_list_pin) as ImageView
            holder.distanceText = convertView.findViewById<View>(R.id.locator_list_distance) as TextView
            holder.titleText = convertView.findViewById<View>(R.id.locator_list_title) as TextView
            holder.addressText = convertView.findViewById<View>(R.id.locator_list_address) as TextView
            holder.cityText = convertView.findViewById<View>(R.id.locator_list_city) as TextView
            holder.phoneText = convertView.findViewById<View>(R.id.locator_list_phone) as TextView
            holder.storeImage = convertView.findViewById<View>(R.id.locator_list_storeimage) as ImageView
            holder.avgWaitTimeText = convertView.findViewById<View>(R.id.avgWaitTime) as TextView
            holder.waitButton = convertView.findViewById<View>(R.id.waitListButton) as Button
            holder.favoritesButton = convertView.findViewById<View>(R.id.favoritesButton) as Button
            holder.waitButton?.setOnClickListener { v ->
                val selectedLocation = v.tag as BFLocation
                parentActivity.startWaitListFlow(selectedLocation)
            }
            convertView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }
        if (!isFavorite) {
            holder.favoritesButton?.text = "Add to Favorites"
            holder.favoritesButton?.setOnClickListener { parentActivity.addToFavorites(location) }
        } else {
            holder.favoritesButton?.text = "Remove from Favorites"
            holder.favoritesButton?.setOnClickListener { parentActivity.removeFromFavorites(location) }
        }
        holder.waitButton?.tag = location
        val waitListDP = BFWaitListDP(parentActivity.applicationContext)
        holder.waitButton?.text = "Get on Waitlist"
        if (waitListDP.checkExistingDineTimeWaitlistEntriesForLocation(location.clientKey)) {
            holder.waitButton?.text = "Modify Waitlist"
        }
        holder.pinImage?.setImageBitmap(BFUtils.writeTextOnDrawable(parentActivity.applicationContext, R.drawable.green_pin, Integer.toString(location.sequence + 1)))
        if (location.distance != null) {
            holder.distanceText?.text = location.distance
        }
        if (location.name != null) {
            holder.titleText?.text = location.name
        }
        if (location.address1 != null) {
            holder.addressText?.text = location.address1
        }
        if (location.city != null) {
            holder.cityText?.text = location.city
        }
        if (location.state != null) {
            holder.cityText?.text = String.format("%s, %s", holder.cityText?.text, location.state)
        }
        if (location.postalCode != null) {
            holder.cityText?.text = String.format("%s %s", holder.cityText?.text, location.postalCode)
        }
        if (location.phone != null) {
            holder.phoneText?.text = location.phone
        }
        if (location.avgWaitTime != null) {
            holder.avgWaitTimeText?.visibility = View.VISIBLE
            holder.waitButton?.visibility = View.VISIBLE
            holder.avgWaitTimeText?.text = String.format("Average Wait Time: ~%s", location.avgWaitTime)
            val params = holder.favoritesButton?.layoutParams as RelativeLayout.LayoutParams
            params.addRule(RelativeLayout.BELOW, R.id.waitListButton)
        } else {
            holder.avgWaitTimeText?.visibility = View.INVISIBLE
            holder.waitButton?.visibility = View.INVISIBLE
            val params = holder.favoritesButton?.layoutParams as RelativeLayout.LayoutParams
            params.addRule(RelativeLayout.BELOW, R.id.list_content2)
        }
        if (location.storeImageURL != null) {
            try {
                Picasso.with(parentActivity.applicationContext).load(location.storeImageURL).into(holder.storeImage)
            } catch (ignored: Exception) {
            }
        }
        return convertView
    }

    fun getLocationAtPos(pos: Int): BFLocation {
        return locationList.locationList[pos]
    }
}