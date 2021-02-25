package com.brandify.brandifySDKDemo.beacon

import org.altbeacon.beacon.Beacon
import org.altbeacon.beacon.Region

class BeaconItem : Comparable<BeaconItem> {
    @JvmField
    var beacon: Beacon? = null

    @JvmField
    var region: Region? = null
    var id = "na"
    override fun compareTo(other: BeaconItem): Int {
        return if (beacon == null || other.beacon == null) {
            -1
        } else {
            beacon!!.rssi - other.beacon!!.rssi
        }
    }
}