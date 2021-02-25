package com.brandify.brandifySDKDemo.beacon

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.brandify.brandifySDKDemo.R
import java.util.*

//This class represents the blueprint for the hamburger menu list view items in order to populate them
class BeaconListViewAdapter(
        private var context: Context?,
        private var beaconItems: ArrayList<BeaconItem>?) : BaseAdapter() {

    //Implement the BaseAdapter methods
    override fun getCount(): Int {
        return beaconItems?.size ?: 0
    }

    override fun getItem(position: Int): Any? {
        return beaconItems?.get(position)
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = if (convertView == null) {
            //Set the layout for reach menu item in the list
            val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(R.layout.beacon_item, null)
        } else {
            convertView
        }
        val title = view.findViewById<View>(R.id.beaconUUID) as TextView
        val major = view.findViewById<View>(R.id.beaconMajor) as TextView
        val minor = view.findViewById<View>(R.id.beaconMinor) as TextView
        val distance = view.findViewById<View>(R.id.beaconDistance) as TextView
        val rssi = view.findViewById<View>(R.id.beaconRSSI) as TextView
        val txPower = view.findViewById<View>(R.id.beaconTX) as TextView
        val btName = view.findViewById<View>(R.id.beaconBTName) as TextView
        val btAddress = view.findViewById<View>(R.id.beaconBTAddress) as TextView
        val beaconItem = beaconItems?.get(position)
        val beacon = beaconItem?.beacon
        val region = beaconItem?.region
        if (beacon != null) {
            ("UUID: " + beacon.id1.toString()).also { title.text = it }
            ("Major: " + beacon.id2.toString()).also { major.text = it }
            ("Minor: " + beacon.id3.toString()).also { minor.text = it }
            ("Distance (m): " + beacon.distance.toString()).also { distance.text = it }
            ("RSSI: " + beacon.rssi.toString()).also { rssi.text = it }
            ("Tx Power: " + beacon.txPower.toString()).also { txPower.text = it }
            ("Bluetooth Name: " + beacon.bluetoothName).also { btName.text = it }
            ("Bluetooth Address: " + beacon.bluetoothAddress).also { btAddress.text = it }
        }
        if (region != null) {
            (region.id1.toString() + ":" + region.id2.toString() + ":" + region.id3).also { title.text = it }
        }
        return view
    }
} //END OF HamburgerMenuListViewAdapter
