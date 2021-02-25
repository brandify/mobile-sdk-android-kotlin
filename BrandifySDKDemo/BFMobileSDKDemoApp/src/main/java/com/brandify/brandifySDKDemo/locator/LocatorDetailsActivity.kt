package com.brandify.brandifySDKDemo.locator

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.brandify.BrandifyMobileSDK.models.BFConfiguration
import com.brandify.BrandifyMobileSDK.models.BFLocation
import com.brandify.BrandifyMobileSDK.models.BFRunnable
import com.brandify.BrandifyMobileSDK.providers.BFConfigurationDP
import com.brandify.brandifySDKDemo.R
import com.brandify.brandifySDKDemo.utils.BFUtils
import com.brandify.brandifySDKDemo.favorites.FavoritesActivity
import com.brandify.brandifySDKDemo.locator.LocatorMapFragment.Companion.selectedLocForDetail
import com.squareup.picasso.Picasso
import java.util.*

class LocatorDetailsActivity : Activity() {
    private var selectedLocation: BFLocation? = null
    private var isFavorite = false
    private var previousActivity: String? = null
    private var favoriteIntent: String? = null
    private var config: BFConfiguration? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.locator_details_activity)
        previousActivity = intent.getStringExtra("activity")
        if (previousActivity.equals("FavoritesActivity", ignoreCase = true)) {
            this.selectedLocation = FavoritesActivity.selectedLocation
        } else {
            this.selectedLocation = selectedLocForDetail
        }
        isFavorite = BFUtils().searchFavorites(thisClass(), selectedLocation?.clientKey)
        val configDP = BFConfigurationDP()
        config = configDP.getConfiguration(false)

        //Setup UI Views
        setTitleAddress()
        setButtons()
        setStoreHours()
        setStoreInformation()
        setStoreImages()
    }

    private fun setTitleAddress() {
        val titleText = findViewById<View>(R.id.titleText) as TextView
        if (selectedLocation?.address1 != null) {
            titleText.text = selectedLocation?.address1!!.toUpperCase()
        }
        val addressText = findViewById<View>(R.id.addressText) as TextView
        if (selectedLocation?.address1 != null) {
            addressText.text = selectedLocation?.address1
        }
        val cityText = findViewById<View>(R.id.cityText) as TextView
        if (selectedLocation?.city != null) {
            cityText.text = selectedLocation?.city
        }
        if (selectedLocation?.state != null) {
            cityText.text = String.format("%s, %s", cityText.text, selectedLocation?.state)
        }
        if (selectedLocation?.postalCode != null) {
            cityText.text = String.format("%s %s", cityText.text, selectedLocation?.postalCode)
        }
    }

    private fun setButtons() {
        val exitButton = findViewById<View>(R.id.exit_button_id) as Button
        if (favoriteIntent != null) {
            exitButton.setOnClickListener {
                val intent = Intent(thisClass(), FavoritesActivity::class.java)
                startActivity(intent)
            }
        } else {
            exitButton.visibility = View.INVISIBLE
        }
        val phoneButton = findViewById<View>(R.id.phoneButton) as Button
        if (selectedLocation?.phone != null) {
            phoneButton.text = selectedLocation?.phone
        }
        val directionButton = findViewById<View>(R.id.directionButton) as Button
        directionButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW,
                    Uri.parse("geo:0,0?q=" + selectedLocation?.latitude + "," + selectedLocation?.longitude + "(" + selectedLocation?.name + ")"))
            startActivity(intent)
        }
        val sendTextButton = findViewById<View>(R.id.sendTextButton) as ImageButton
        sendTextButton.setOnClickListener {
            //TODO: Implement phone
        }
        val sendEmailButton = findViewById<View>(R.id.sendEmailButton) as ImageButton
        sendEmailButton.setOnClickListener {
            //TODO: Implement Email
        }
        val featCaliaButton = findViewById<View>(R.id.featCaliaButton) as ImageButton
        val featCaliaText = findViewById<View>(R.id.featCaliaText) as TextView
        featCaliaButton.visibility = View.GONE
        featCaliaText.visibility = View.GONE
        if (selectedLocation?.customProperties?.containsKey("caliaindicator") == true) {
            if (selectedLocation?.customProperties!!["caliaindicator"] as String? === "1") {
                if (config!!.logos.containsKey("calia")) {
                    val imageURL = config!!.logos["calia"]
                    Picasso.with(this.applicationContext).load(imageURL).into(featCaliaButton)
                    featCaliaButton.visibility = View.VISIBLE
                    featCaliaText.visibility = View.VISIBLE
                }
            }
        }
        val favoritesButton = findViewById<View>(R.id.favorite_Button) as Button
        if (isFavorite == false) {
            favoritesButton.text = "Add To Favorites"
        } else {
            favoritesButton.text = "Remove From Favorites"
        }
        favoritesButton.setOnClickListener {
            val bfUtils = BFUtils()
            if (isFavorite == false) {
                bfUtils.addToFavoritesList(application, selectedLocation, object : BFRunnable<BFLocation?>() {
                    override fun run() {
                        if (model != null) {
                            val intent = Intent(this@LocatorDetailsActivity, LocatorDetailsActivity::class.java)
                            intent.putExtra("activity", previousActivity)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                            finish()
                            startActivity(intent)
                        }
                    }
                })
            } else {
                bfUtils.removeFromFavoritesList(application, selectedLocation, object : BFRunnable<BFLocation?>() {
                    override fun run() {
                        if (model != null) {
                            val intent = Intent(this@LocatorDetailsActivity, LocatorDetailsActivity::class.java)
                            intent.putExtra("activity", previousActivity)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                            finish()
                            startActivity(intent)
                        }
                    }
                })
            }
        }
    }

    private fun setStoreHours() {
        val calendar = Calendar.getInstance()
        val today = calendar[Calendar.DAY_OF_WEEK]
        val mondayHoursText = findViewById<View>(R.id.monday_hours) as TextView
        val tuesdayHoursText = findViewById<View>(R.id.tuesday_hours) as TextView
        val wednesdayHoursText = findViewById<View>(R.id.wednesday_hours) as TextView
        val thursdayHoursText = findViewById<View>(R.id.thursday_hours) as TextView
        val fridayHoursText = findViewById<View>(R.id.friday_hours) as TextView
        val saturdayHoursText = findViewById<View>(R.id.saturday_hours) as TextView
        val sundayHoursText = findViewById<View>(R.id.sunday_hours) as TextView
        val mondayHoursLabel = findViewById<View>(R.id.monday_label) as TextView
        val tuesdayHoursLabel = findViewById<View>(R.id.tuesday_label) as TextView
        val wednesdayHoursLabel = findViewById<View>(R.id.wednesday_label) as TextView
        val thursdayHoursLabel = findViewById<View>(R.id.thursday_label) as TextView
        val fridayHoursLabel = findViewById<View>(R.id.friday_label) as TextView
        val saturdayHoursLabel = findViewById<View>(R.id.saturday_label) as TextView
        val sundayHoursLabel = findViewById<View>(R.id.sunday_label) as TextView
        val sundayOpen = selectedLocation?.getCustomProperty("sundayopen") as String?
        val sunddayClose = selectedLocation?.getCustomProperty("sundayclose") as String?
        val sundayOpenAllday = selectedLocation?.getCustomProperty("sundayopenallday") as String?
        val sundayClosedAllDay = selectedLocation?.getCustomProperty("sundayclosedallday") as String?
        val mondayOpen = selectedLocation?.getCustomProperty("mondayopen") as String?
        val mondayClose = selectedLocation?.getCustomProperty("mondayclose") as String?
        val mondayOpenAllday = selectedLocation?.getCustomProperty("mondayopenallday") as String?
        val mondayClosedAllDay = selectedLocation?.getCustomProperty("mondayclosedallday") as String?
        val tuesdayOpen = selectedLocation?.getCustomProperty("tuesdayopen") as String?
        val tuesdayClose = selectedLocation?.getCustomProperty("tuesdayclose") as String?
        val tuesdayOpenAllday = selectedLocation?.getCustomProperty("tuesdayopenallday") as String?
        val tuesdayClosedAllDay = selectedLocation?.getCustomProperty("tuesdayclosedallday") as String?
        val wednesdayOpen = selectedLocation?.getCustomProperty("wednesdayopen") as String?
        val wednesdayClose = selectedLocation?.getCustomProperty("wednesdayclose") as String?
        val wednesdayOpenAllday = selectedLocation?.getCustomProperty("wednesdayopenallday") as String?
        val wednesdayClosedAllDay = selectedLocation?.getCustomProperty("wednesdayclosedallday") as String?
        val thursdayOpen = selectedLocation?.getCustomProperty("thursdayopen") as String?
        val thursdayClose = selectedLocation?.getCustomProperty("thursdayclose") as String?
        val thursdayOpenAllday = selectedLocation?.getCustomProperty("thursdayopenallday") as String?
        val thursdayClosedAllDay = selectedLocation?.getCustomProperty("thursdayclosedallday") as String?
        val fridayOpen = selectedLocation?.getCustomProperty("fridayopen") as String?
        val fridayClose = selectedLocation?.getCustomProperty("fridayclose") as String?
        val fridayOpenAllday = selectedLocation?.getCustomProperty("fridayopenallday") as String?
        val fridayClosedAllDay = selectedLocation?.getCustomProperty("fridayclosedallday") as String?
        val saturdayOpen = selectedLocation?.getCustomProperty("saturdayopen") as String?
        val saturdayClose = selectedLocation?.getCustomProperty("saturdayclose") as String?
        val saturdayOpenAllday = selectedLocation?.getCustomProperty("saturdayopenallday") as String?
        val saturdayClosedAllDay = selectedLocation?.getCustomProperty("saturdayclosedallday") as String?
        sundayHoursText.text = calculateHours(sundayOpenAllday, sundayClosedAllDay, sundayOpen, sunddayClose)
        mondayHoursText.text = calculateHours(mondayOpenAllday, mondayClosedAllDay, mondayOpen, mondayClose)
        tuesdayHoursText.text = calculateHours(tuesdayOpenAllday, tuesdayClosedAllDay, tuesdayOpen, tuesdayClose)
        wednesdayHoursText.text = calculateHours(wednesdayOpenAllday, wednesdayClosedAllDay, wednesdayOpen, wednesdayClose)
        thursdayHoursText.text = calculateHours(thursdayOpenAllday, thursdayClosedAllDay, thursdayOpen, thursdayClose)
        fridayHoursText.text = calculateHours(fridayOpenAllday, fridayClosedAllDay, fridayOpen, fridayClose)
        saturdayHoursText.text = calculateHours(saturdayOpenAllday, saturdayClosedAllDay, saturdayOpen, saturdayClose)
        when (today) {
            1 -> {
                sundayHoursLabel.setTypeface(null, Typeface.BOLD)
                sundayHoursText.setTypeface(null, Typeface.BOLD)
            }
            2 -> {
                mondayHoursLabel.setTypeface(null, Typeface.BOLD)
                mondayHoursText.setTypeface(null, Typeface.BOLD)
            }
            3 -> {
                tuesdayHoursLabel.setTypeface(null, Typeface.BOLD)
                tuesdayHoursText.setTypeface(null, Typeface.BOLD)
            }
            4 -> {
                wednesdayHoursLabel.setTypeface(null, Typeface.BOLD)
                wednesdayHoursText.setTypeface(null, Typeface.BOLD)
            }
            5 -> {
                thursdayHoursLabel.setTypeface(null, Typeface.BOLD)
                thursdayHoursText.setTypeface(null, Typeface.BOLD)
            }
            6 -> {
                fridayHoursLabel.setTypeface(null, Typeface.BOLD)
                fridayHoursText.setTypeface(null, Typeface.BOLD)
            }
            7 -> {
                saturdayHoursLabel.setTypeface(null, Typeface.BOLD)
                saturdayHoursText.setTypeface(null, Typeface.BOLD)
            }
            else -> {
            }
        }
    }

    private fun calculateHours(openAllday: String?, closedAllday: String?, openTime: String?, closeTime: String?): String {
        return if (openAllday != null && openAllday == "1") {
            "Open all day"
        } else if (closedAllday != null && closedAllday == "1") {
            "Closed all day"
        } else {
            formatTime(openTime) + " - " + formatTime(closeTime)
        }
    }

    private fun formatTime(timeString: String?): String {
        var theTime = ""
        if (timeString != null && timeString.toInt() <= 1200) {
            theTime = timeString.substring(0, 2) + ":" + timeString.substring(2) + "am"
            if (theTime.startsWith("0")) {
                theTime = theTime.substring(1)
            }
            if (timeString == "0000") {
                theTime = "12:00am"
            }
            if (timeString === "1200") {
                theTime = "12:00pm"
            }
        } else if (timeString != null) {
            var hour = timeString.substring(0, 2).toInt()
            hour -= 12
            theTime = hour.toString() + ":" + timeString.substring(2) + "pm"
        }
        return theTime
    }

    private fun setStoreInformation() {
        val storeEquipText = findViewById<View>(R.id.equiment_services_text) as TextView
        if (selectedLocation?.getCustomProperty("services") != null) {
            val services = selectedLocation?.getCustomProperty("services").toString()
            if (services != "null") {
                storeEquipText.text = services
            } else {
                storeEquipText.text = "N/A"
            }
        }
        val aboutText = findViewById<View>(R.id.about_text) as TextView
        if (selectedLocation?.getCustomProperty("locationdescription") != null) {
            val about = selectedLocation?.getCustomProperty("locationdescription").toString()
            if (about != "null") {
                aboutText.text = about
            } else {
                aboutText.text = "N/A"
            }
        }
    }

    private fun setStoreImages() {
        val storeImage1 = findViewById<View>(R.id.store_image1) as ImageView
        val storeImage2 = findViewById<View>(R.id.store_image2) as ImageView
        val storeImage3 = findViewById<View>(R.id.store_image3) as ImageView
        val storeImage4 = findViewById<View>(R.id.store_image4) as ImageView
        val imagePath = config!!.baseStoreImagePathURL
        if (imagePath != null) {
            for (i in 1..4) {
                val imageURL = imagePath + selectedLocation?.clientKey + "-" + i.toString() + "-m.jpg"
                when (i) {
                    1 -> Picasso.with(this.applicationContext).load(imageURL).into(storeImage1)
                    2 -> Picasso.with(this.applicationContext).load(imageURL).into(storeImage2)
                    3 -> Picasso.with(this.applicationContext).load(imageURL).into(storeImage3)
                    4 -> Picasso.with(this.applicationContext).load(imageURL).into(storeImage4)
                }
            }
        }
    }

    private fun thisClass(): Activity {
        return this
    }
}