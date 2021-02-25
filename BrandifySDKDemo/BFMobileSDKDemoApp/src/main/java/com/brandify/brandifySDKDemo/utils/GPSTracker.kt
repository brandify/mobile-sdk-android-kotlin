/*
Code Source: http://gabesechansoftware.com/location-tracking/
*/
package com.brandify.brandifySDKDemo.utils

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log

class GPSTracker(private val mContext: Context?) : Service(), LocationListener {
    // Flag for GPS status
    private var isGPSEnabled = false

    // Flag for network status
    var isNetworkEnabled = false

    // Flag for GPS status
    private var canGetLocation = false
    var locationValue: Location? = null
    var latitudeValue = 0.0
    var longitudeValue = 0.0

    @SuppressLint("MissingPermission")
    fun getLocation(): Location? {
        try {
            if (mContext != null) {
                val locationManager = mContext.getSystemService(LOCATION_SERVICE) as LocationManager

                // Getting GPS status
                isGPSEnabled = locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER)

                // Getting network status
                isNetworkEnabled = locationManager
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                if (!isGPSEnabled && !isNetworkEnabled) {
                    // No network provider is enabled
                } else {
                    canGetLocation = true
                    if (isNetworkEnabled) {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this)
                        Log.d("Network", "Network")
                        locationValue = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        if (locationValue != null) {
                            latitudeValue = locationValue!!.latitude
                            longitudeValue = locationValue!!.longitude
                        }
                    }
                    // If GPS enabled, get latitude/longitude using GPS Services
                    if (isGPSEnabled) {
                        if (locationValue == null) {
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this)
                            Log.d("GPS Enabled", "GPS Enabled")
                            locationValue = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER)
                            if (locationValue != null) {
                                latitudeValue = locationValue!!.latitude
                                longitudeValue = locationValue!!.longitude
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return locationValue
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app.
     */
    fun stopUsingGPS() {
        if (mContext != null) {
            val locationManager = mContext.getSystemService(LOCATION_SERVICE) as LocationManager
            locationManager.removeUpdates(this@GPSTracker)
        }
    }

    /**
     * Function to get latitude
     */
    fun getLatitude(): Double {
        if (locationValue != null) {
            latitudeValue = locationValue!!.latitude
        }

        // return latitude
        return latitudeValue
    }

    /**
     * Function to get longitude
     */
    fun getLongitude(): Double {
        if (locationValue != null) {
            longitudeValue = locationValue!!.longitude
        }

        // return longitude
        return longitudeValue
    }

    /**
     * Function to check GPS/Wi-Fi enabled
     *
     * @return boolean
     */
    fun canGetLocation(): Boolean {
        return canGetLocation
    }

    /**
     * Function to show settings alert dialog.
     * On pressing the Settings button it will launch Settings Options.
     */
    fun showSettingsAlert() {
        val alertDialog = AlertDialog.Builder(mContext)

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings")

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?")

        // On pressing the Settings button.
        alertDialog.setPositiveButton("Settings") { _, _ ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            mContext?.startActivity(intent)
        }

        // On pressing the cancel button
        alertDialog.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        // Showing Alert Message
        alertDialog.show()
    }

    override fun onLocationChanged(location: Location) {}
    override fun onProviderDisabled(provider: String) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    override fun onBind(arg0: Intent): IBinder? {
        return null
    }

    companion object {
        // The minimum distance to change Updates in meters
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10 // 10 meters

        // The minimum time between updates in milliseconds
        private const val MIN_TIME_BW_UPDATES = (1000 * 60 * 1 // 1 minute
                ).toLong()
    }

    init {
        getLocation()
    }
}