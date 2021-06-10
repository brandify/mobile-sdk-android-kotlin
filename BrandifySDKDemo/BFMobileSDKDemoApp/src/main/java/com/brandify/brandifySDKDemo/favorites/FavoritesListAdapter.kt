package com.brandify.brandifySDKDemo.favorites

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.brandify.BrandifyMobileSDK.models.BFListOfLocations
import com.brandify.BrandifyMobileSDK.models.BFLocation
import com.brandify.brandifySDKDemo.R
import com.brandify.brandifySDKDemo.utils.BFUtils

class FavoritesListAdapter(
    private var parentActivity: FavoritesActivity,
    var favoritesList: BFListOfLocations?
) : ArrayAdapter<BFLocation?>(parentActivity.applicationContext, -1) {
    internal class ViewHolder {
        var pinImage: ImageView? = null
        var titleText: TextView? = null
        var addressText: TextView? = null
        var cityText: TextView? = null
        var phoneText: TextView? = null
    }

    override fun getCount(): Int {
        return favoritesList?.locationList?.size ?: 0
    }

    override fun getView(pos: Int, convertViewParam: View?, parent: ViewGroup): View {
        var convertView: View = convertViewParam ?: View(parent.context)
        val location = favoritesList!!.locationList[pos]
        val holder: ViewHolder
        if (convertViewParam == null) {
            val inflater = LayoutInflater.from(parentActivity.applicationContext)
            convertView = inflater.inflate(R.layout.favorites_list_cell, null)
            holder = ViewHolder()
            holder.pinImage = convertView.findViewById<View>(R.id.favorites_list_pin) as ImageView
            holder.titleText = convertView.findViewById<View>(R.id.favorites_list_title) as TextView
            holder.addressText =
                convertView.findViewById<View>(R.id.favorites_list_address) as TextView
            holder.cityText = convertView.findViewById<View>(R.id.favorites_list_city) as TextView
            holder.phoneText = convertView.findViewById<View>(R.id.favorites_list_phone) as TextView
            convertView.tag = holder
            convertView.setOnClickListener { parentActivity.showDetails(location) }
        } else {
            holder = convertView.tag as ViewHolder
        }
        holder.pinImage?.setImageBitmap(
            BFUtils.writeTextOnDrawable(
                parentActivity.applicationContext,
                R.drawable.green_pin,
                (location.sequence + 1).toString()
            )
        )
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
            (holder.cityText?.text.toString() + ", " + location.state).also {
                holder.cityText?.text = it
            }
        }
        if (location.postalCode != null) {
            (holder.cityText?.text.toString() + " " + location.postalCode).also {
                holder.cityText?.text = it
            }
        }
        if (location.phone != null) {
            holder.phoneText?.text = location.phone
        }
        return convertView
    }
}