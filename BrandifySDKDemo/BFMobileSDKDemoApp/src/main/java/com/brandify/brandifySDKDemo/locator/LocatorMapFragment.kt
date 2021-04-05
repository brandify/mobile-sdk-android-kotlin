package com.brandify.brandifySDKDemo.locator

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.brandify.BrandifyMobileSDK.common.connection.BFContext
import com.brandify.BrandifyMobileSDK.models.BFListOfLocations
import com.brandify.BrandifyMobileSDK.models.BFLocation
import com.brandify.BrandifyMobileSDK.models.BFRunnable
import com.brandify.BrandifyMobileSDK.models.BFSearchLocation
import com.brandify.BrandifyMobileSDK.providers.BFGetListDP
import com.brandify.BrandifyMobileSDK.providers.BFLocatorDP
import com.brandify.brandifySDKDemo.R
import com.brandify.brandifySDKDemo.utils.BFUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.squareup.picasso.Picasso
import java.util.*

class LocatorMapFragment : Fragment() {
    private var markerLocationMap: HashMap<Marker, BFLocation>? = null
    private var locationMarkerMap: HashMap<BFLocation, Marker>? = null
    private var lockSearchOnPan = false
    private var lastLocation: Location? = null
    private var currentUserLocation: Location? = null
    private var centerPosMarker: Marker? = null
    private var returnedLocationList: BFListOfLocations? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.locator_map_fragment, container, false)
        returnedLocationList = (activity as LocatorActivity?)?.locationList
        currentUserLocation = BFUtils.getCurrentLocation(activity?.applicationContext)
        lastLocation = Location("")
        markerLocationMap = HashMap()
        locationMarkerMap = HashMap()
        val searchLoc = BFSearchLocation()
        searchLoc.radius.clear()
        searchLoc.radius.add(15)
        searchLoc.radius.add(30)
        searchLoc.radius.add(60)
        searchLoc.radius.add(120)
        searchLoc.limit = 15
        if (currentUserLocation != null) {
            searchLoc.latitude = currentUserLocation!!.latitude
            searchLoc.longitude = currentUserLocation!!.longitude
        }
        (activity as LocatorActivity?)?.locatorCurLocationButton?.setOnClickListener {
            currentUserLocation = BFUtils.getCurrentLocation(activity?.applicationContext)
            //re-execute search with current user coordinates
            val searchLocation = BFSearchLocation()
            if (currentUserLocation != null) {
                searchLocation.longitude = currentUserLocation!!.longitude
                searchLocation.latitude = currentUserLocation!!.latitude
            }
            Log.d(null, "LocatorMapFrag onCreateView lat/long:" + searchLocation.latitude + " " + searchLocation.longitude)
            getTargetLocations(searchLocation, true)
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        setMapIfNeeded()
    }

    private fun setMapIfNeeded() {
        mapFragment = this.childFragmentManager
                .findFragmentByTag(MAP_FRAGMENT_TAG) as SupportMapFragment?
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance()
            val fragmentTransaction = childFragmentManager.beginTransaction()
            if (mapFragment != null) {
                fragmentTransaction.add(R.id.map, mapFragment!!, MAP_FRAGMENT_TAG)
            }
            fragmentTransaction.commit()
        }
        setUpMap()
    }

    private fun setUpMap() {
        mapFragment?.getMapAsync { googleMap ->
            mMap = googleMap
            mMap?.setInfoWindowAdapter(object : InfoWindowAdapter {
                override fun getInfoWindow(marker: Marker): View? {
                    return null
                }

                override fun getInfoContents(marker: Marker): View? {
                    val inflater = LayoutInflater.from(activity?.applicationContext)
                    val infoView = inflater.inflate(R.layout.locator_info_window, null)
                    val selectedLocation = markerLocationMap?.get(marker)
                    if (selectedLocation != null) {
                        val storeNameView = infoView.findViewById<View>(R.id.store_name) as TextView
                        if (selectedLocation.name != null) {
                            storeNameView.text = selectedLocation.name
                        }
                        val titleView = infoView.findViewById<View>(R.id.title) as TextView
                        if (selectedLocation.address2 != null && selectedLocation.address2 != "") {
                            titleView.text = selectedLocation.address2
                        } else if (selectedLocation.address1 != null) {
                            titleView.text = selectedLocation.address1
                        }
                        val addressView = infoView.findViewById<View>(R.id.address) as TextView
                        if (selectedLocation.address2 != null && selectedLocation.address2 != "") {
                            addressView.text = selectedLocation.address2
                        } else if (selectedLocation.address1 != null) {
                            addressView.text = selectedLocation.address1
                        }
                        if (selectedLocation.state != null) {
                            addressView.text = String.format("%s, %s", addressView.text, selectedLocation.state)
                        }
                        if (selectedLocation.postalCode != null) {
                            addressView.text = String.format("%s %s", addressView.text, selectedLocation.postalCode)
                        }
                        val phoneView = infoView.findViewById<View>(R.id.phone) as TextView
                        if (selectedLocation.phone != null) {
                            phoneView.text = selectedLocation.phone
                        }
                        val distanceView = infoView.findViewById<View>(R.id.distance) as TextView
                        if (selectedLocation.distance != null) {
                            distanceView.text = String.format("%s%s", selectedLocation.distance, distanceView.text)
                        }
                        val storeImage = infoView.findViewById<View>(R.id.store_image) as ImageView
                        if (selectedLocation.storeImageURL != null) {
                            val url = selectedLocation.storeImageURL
                            Picasso.with(activity?.applicationContext).load(url).into(storeImage)
                        }
                    } else {
                        return null
                    }
                    return infoView
                }
            })
            mMap?.setOnInfoWindowClickListener { marker ->
                if (markerLocationMap != null && markerLocationMap?.containsKey(marker) == true) {
                    selectedLocForDetail = markerLocationMap!![marker]
                    val intent = Intent(activity, LocatorDetailsActivity::class.java)
                    intent.putExtra("activity", "LocatorMapFragment")
                    startActivity(intent)
                }
            }
            mMap?.setOnCameraChangeListener { cameraPosition ->
                if (mMap != null && lastLocation != null
                        && lastLocation?.latitude != 0.0 && lastLocation?.longitude != 0.0
                        && !lockSearchOnPan) {
                    //Distance in miles
                    val cameraLatLng = mMap!!.cameraPosition.target
                    val cameraLocation = Location("")
                    cameraLocation.latitude = cameraLatLng.latitude
                    cameraLocation.longitude = cameraLatLng.longitude
                    val distance = lastLocation!!.distanceTo(cameraLocation) / 1609.34
                    val panMilesBeforeRefresh = 5.0
                    if (distance > panMilesBeforeRefresh) {
                        val searchLocation = BFSearchLocation()
                        searchLocation.geoip = false
                        searchLocation.latitude = cameraPosition.target.latitude
                        searchLocation.longitude = cameraPosition.target.longitude
                        val eastMapPoint = Location("")
                        eastMapPoint.latitude = mMap!!.projection.visibleRegion.farLeft.latitude
                        eastMapPoint.longitude = mMap!!.projection.visibleRegion.farLeft.longitude
                        val westMapPoint = Location("")
                        westMapPoint.latitude = mMap!!.projection.visibleRegion.farRight.latitude
                        westMapPoint.longitude = mMap!!.projection.visibleRegion.farRight.longitude
                        val currentDistWideInMeters = eastMapPoint.distanceTo(westMapPoint).toDouble()
                        val milesWide = currentDistWideInMeters / 1609.34
                        searchLocation.radius.clear()
                        searchLocation.radius.add(milesWide.toInt())
                        getTargetLocations(searchLocation, false)
                    }
                }
                lastLocation?.latitude = cameraPosition.target.latitude
                lastLocation?.longitude = cameraPosition.target.longitude
            }
            //getTargetLocations(searchLoc, true);
        }
    }

    private fun getTargetLocations(searchLocation: BFSearchLocation, forceRecenter: Boolean) {
        prepareSearch(searchLocation, forceRecenter)
        val locatorDP = BFLocatorDP()
        val context = BFContext(activity?.applicationContext)
        val myLocation = HashMap<String, String>()
        myLocation["latitude"] = "36.020262"
        myLocation["longitude"] = "-86.791295"
        val chooseClosest = HashMap<String, String>()
        chooseClosest["latitude"] = "36.020262"
        chooseClosest["longitude"] = "-86.791295"
        if (searchLocation.locatorRequestFormdataConfigAdditions == null) {
            searchLocation.locatorRequestFormdataConfigAdditions = HashMap()
        }
        searchLocation.locatorRequestFormdataConfigAdditions["mylocation"] = myLocation
        searchLocation.locatorRequestFormdataConfigAdditions["chooseclosest"] = chooseClosest

        if (!TextUtils.isEmpty(searchLocation.addressLine)) {
            searchLocation.latitude = 0.toDouble()
            searchLocation.longitude = 0.toDouble()
        }

        locatorDP.search(context, searchLocation, true, generateCallback(searchLocation, forceRecenter))
    }

    private fun prepareSearch(searchLocation: BFSearchLocation, forceRecenter: Boolean) {
        //always remove the center point annotation
        if (centerPosMarker != null) {
            centerPosMarker?.remove()
        }

        //if conducting an address search - or geopip search, clear all annotations
        if (searchLocation.latitude == 0.0 && searchLocation.longitude == 0.0 || forceRecenter) {
            mMap?.clear()
            locationMarkerMap?.clear()
            markerLocationMap?.clear()
        } else {
            //user is panning - only remove those that are not in view any more
            val iter: MutableIterator<Map.Entry<Marker, BFLocation>>? = markerLocationMap?.entries?.iterator()
            if (iter != null) {
                while (iter.hasNext()) {
                    val entry = iter.next()
                    if (mMap?.projection?.visibleRegion?.latLngBounds?.contains(entry.key.position) == false) {
                        entry.key.remove()
                        iter.remove()
                    }
                    locationMarkerMap?.remove(entry.value)
                }
            }
        }
    }

    private fun generateCallback(searchLocation: BFSearchLocation, forceRecenter: Boolean): BFRunnable<BFListOfLocations?> {
        return object : BFRunnable<BFListOfLocations?>() {
            override fun run() {
                try {
                    //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentUserLocation.getLatitude(), currentUserLocation.getLongitude()), 10));
                    (activity as LocatorActivity?)?.updateListView(model)
                    returnedLocationList = (activity as LocatorActivity?)?.locationList

                    if (returnedLocationList != null) {
                        Log.d(null, "LocatorMapFrag.search lat/long:" + returnedLocationList?.latitude + " " + returnedLocationList?.longitude)
                        if (returnedLocationList?.latitude != 0.0 && returnedLocationList?.longitude != 0.0) {
                            val returnedCenterLoc = LatLng(returnedLocationList!!.latitude, returnedLocationList!!.longitude)
                            Log.d(null, "LocatorMapFrag.search centerPosition:$returnedCenterLoc")
                            if (activity != null) {
                                centerPosMarker = mMap?.addMarker(BFUtils.getCustomMarker(activity!!.applicationContext, R.drawable.red_pin_shadow, returnedCenterLoc, "", "", ""))
                            }
                        }
                        for (location in returnedLocationList!!.locationList) {
                            val pos = LatLng(location.latitude, location.longitude)
                            if (mMap != null) {
                                val marker = mMap!!.addMarker(BFUtils.getCustomMarker(activity!!.applicationContext, R.drawable.green_pin, pos, (location.sequence + 1).toString(), "", ""))
                                if (markerLocationMap != null && locationMarkerMap != null) {
                                    markerLocationMap!![marker] = location
                                    locationMarkerMap!![location] = marker
                                }
                            }
                        }
                        Log.d(null, "LocatorMapFrag.search map size:" + markerLocationMap?.size)
                        if (markerLocationMap?.size ?: 0 > 0 &&
                                (searchLocation.latitude == 0.0 && searchLocation.longitude == 0.0 ||
                                        forceRecenter)) {
                            lockSearchOnPan = true
                            //fit camera to markers
                            val builder = LatLngBounds.Builder()
                            builder.include(centerPosMarker?.position)
                            if (markerLocationMap != null) {
                                for (marker in markerLocationMap!!.keys) {
                                    builder.include(marker.position)
                                    //add the opposite position, relative to target (so centering to target fits all)
                                    if (centerPosMarker != null) {
                                        val dx = marker.position.longitude - centerPosMarker!!.position.longitude
                                        val dy = marker.position.latitude - centerPosMarker!!.position.latitude
                                        val opposite = LatLng(centerPosMarker!!.position.latitude - dy, centerPosMarker!!.position.longitude - dx)
                                        builder.include(opposite)
                                    }
                                }
                            }
                            val bounds = builder.build()
                            val padding = 50 // offset from edges of the map in pixels
                            val cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)
                            mMap?.animateCamera(cu)
                            val handler = Handler()
                            handler.postDelayed({ lockSearchOnPan = false }, 1000)
                        }
                    }
                } catch (ex: Exception) {
                    Log.e("Error", "Error while setting map targets: " + ex.message)
                }
            }
        }
    }

    fun openInfoView(location: BFLocation) {
        if (locationMarkerMap != null && locationMarkerMap?.containsKey(location) == true) {
            mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 10f))
            val selectedMarker = locationMarkerMap!![location]
            selectedMarker?.showInfoWindow()
        }
    }

    fun runSearch(searchText: String?, filters: HashMap<String, String>) {
        val searchLocation = BFSearchLocation()
        //To search by address
//        searchLocation.addressLine = searchText
//        searchLocation.filterProperties = filters
//        getTargetLocations(searchLocation, true)

        //To search by name
        searchLocation.filterProperties = filters
        getLocationsByName(searchLocation, searchText, true)

        (activity as LocatorActivity?)?.closeSearch()
    }

    private fun getLocationsByName(searchLocation: BFSearchLocation, searchText: String?, forceRecenter: Boolean) {
        prepareSearch(searchLocation, forceRecenter)

        val bfContext = BFContext(null)
        val inMap = HashMap<String, String>()
        inMap["ilike"] = "%$searchText%"
        searchLocation.getListProperties["name"] = inMap
        searchLocation.searchType = BFSearchLocation.BFSearchType.GETLIST
        val getListDP = BFGetListDP()
        getListDP.getListOfLocations(bfContext, searchLocation, generateCallback(searchLocation, forceRecenter))
    }

    companion object {
        private const val MAP_FRAGMENT_TAG = "map"
        private var mMap: GoogleMap? = null
        private var mapFragment: SupportMapFragment? = null

        @JvmStatic
        var selectedLocForDetail: BFLocation? = null
            private set
    }
}