package com.brandify.brandifySDKDemo.beacon

import android.app.Activity
import android.os.Bundle
import android.os.RemoteException
import android.util.Log
import android.view.View
import android.widget.ListView
import com.brandify.brandifySDKDemo.R
import com.brandify.brandifySDKDemo.baseActivity.BaseActivity
import org.altbeacon.beacon.BeaconConsumer
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.BeaconParser
import org.altbeacon.beacon.Region
import java.util.*

class BeaconActivity : BaseActivity(), BeaconConsumer {
    private var beaconManager: BeaconManager? = null
    private var beaconListView: ListView? = null
    private val beaconItems = ArrayList<BeaconItem>()
    private var beaconActivity: Activity? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        beaconActivity = this
        beaconListView = findViewById<View>(R.id.beaconListView) as ListView
        val adapter = BeaconListViewAdapter(this, beaconItems)
        beaconListView?.adapter = adapter
        beaconManager = BeaconManager.getInstanceForApplication(this)

        // To detect proprietary beacons, you must add a line like below corresponding to your beacon
        // type.  Do a web search for "setBeaconLayout" to get the proper expression.
        beaconManager?.beaconParsers?.add(BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"))
        //m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24
        //m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25
        //m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24

        beaconManager?.bind(this)
    }

    override val content: Int
        get() = R.layout.activity_beacon

    override fun thisClass(): Activity {
        return this
    }

    override fun onDestroy() {
        super.onDestroy()
        beaconManager?.unbind(this)
    }

    override fun onBeaconServiceConnect() {
        beaconManager?.setRangeNotifier { beacons, _ ->
            if (beacons.isNotEmpty()) {
                Log.i("RangingBeacons", "The first beacon I see is about " + beacons.iterator().next().distance + " meters away.")
                beaconItems.clear()
                val iterator = beacons.iterator()
                while (iterator.hasNext()) {
                    val beacon = iterator.next()
                    val beaconItem = BeaconItem()
                    beaconItem.beacon = beacon
                    beaconItems.add(beaconItem)
                }
                //sort by rssi
                beaconItems.sort()

                //run the adapter notification on the main thread - where the view resides
                beaconActivity?.runOnUiThread {
                    val adapter = beaconListView?.adapter as BeaconListViewAdapter
                    adapter.notifyDataSetChanged()
                }
            }
        }
        try {
            beaconManager?.startRangingBeaconsInRegion(Region("TheBrandifyBeaconMonitor", null, null, null))
        } catch (e: RemoteException) {
            Log.e("Error", "Beacon monitor exception: " + e.message)
        }
    }
}