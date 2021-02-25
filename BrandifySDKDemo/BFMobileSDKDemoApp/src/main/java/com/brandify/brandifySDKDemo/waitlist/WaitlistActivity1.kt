package com.brandify.brandifySDKDemo.waitlist

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.brandify.BrandifyMobileSDK.models.BFDineTimeBooking
import com.brandify.BrandifyMobileSDK.models.BFRunnable
import com.brandify.BrandifyMobileSDK.providers.BFConfigurationDP
import com.brandify.BrandifyMobileSDK.providers.BFWaitListDP
import com.brandify.brandifySDKDemo.R

class WaitlistActivity1 : Activity() {
    private var numberOfGuestsEditText: EditText? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_waitlist_1)
        val clientKey = intent.getStringExtra("ClientKey")
        val address = intent.getStringExtra("LocationAddress")
        val addressTextView = findViewById<View>(R.id.locationAddress) as TextView
        numberOfGuestsEditText = findViewById<View>(R.id.numberOfGuests) as EditText
        val nextButton = findViewById<View>(R.id.nextButton) as Button
        val removeButton = findViewById<View>(R.id.removeButton) as Button
        val closeButton = findViewById<View>(R.id.closeButton) as Button
        addressTextView.text = address
        closeButton.setOnClickListener {
            setResult(RESULT_OK, null)
            finish()
        }
        val waitListDP = BFWaitListDP(this)
        val booking = waitListDP.dineTimeBookingForLocation(clientKey)
        if (booking != null) {
            if (booking.guests != null) {
                if (numberOfGuestsEditText != null) {
                    numberOfGuestsEditText?.setText(booking.guests.toString())
                    numberOfGuestsEditText?.setSelection(numberOfGuestsEditText!!.length())
                }
                removeButton.visibility = View.VISIBLE
                removeButton.setOnClickListener {
                    val configurationDP = BFConfigurationDP()
                    val configuration = configurationDP.getConfiguration(false)
                    waitListDP.deleteDineTimeWaitlistBooking(booking.visitId, configuration.accountName, configuration.appKey, clientKey, object : BFRunnable<BFDineTimeBooking?>() {
                        override fun run() {
                            waitListDP.removeVisitForLocationFromCache(clientKey)
                            val alertDialog = AlertDialog.Builder(`this`)
                            alertDialog.setTitle("Brandify")
                            alertDialog.setMessage("Your booking has been removed.")
                            alertDialog.setPositiveButton("OK") { _, _ -> finish() }
                            alertDialog.show()
                        }
                    })
                }
            }
        } else {
            removeButton.visibility = View.INVISIBLE
        }
        nextButton.setOnClickListener {
            var isValid = true
            var validationMessage = ""
            val numberOfGuestsString = numberOfGuestsEditText?.text.toString().trim { it <= ' ' }
            var numberOfGuests = 0
            if (numberOfGuestsString.isEmpty()) {
                isValid = false
                validationMessage = "Please enter the number of guests."
            }
            if (isValid) {
                numberOfGuests = numberOfGuestsString.toInt()
                if (numberOfGuests < minGuests) {
                    isValid = false
                    validationMessage = "This feature is only available for parties of $minGuests or more. Enter a different number of guests or Cancel."
                }
                if (numberOfGuests > maxGuests) {
                    isValid = false
                    validationMessage = "This feature is only available for parties of $maxGuests or less. Enter a different number of guests or Cancel."
                }
            }
            if (isValid) {
                val intent = Intent(`this`, WaitlistActivity2::class.java)
                intent.putExtra("NumberOfGuests", numberOfGuests)
                val clientKeyExtra = getIntent().getStringExtra("ClientKey")
                intent.putExtra("ClientKey", clientKeyExtra)
                intent.putExtra("LocationAddress", address)
                `this`.startActivityForResult(intent, 0)
            } else {
                val alertDialog = AlertDialog.Builder(`this`)
                alertDialog.setTitle("Validation Warning")
                alertDialog.setMessage(validationMessage)
                alertDialog.setPositiveButton("OK", null)
                alertDialog.show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            //keep visibility
        } else if (resultCode == RESULT_CANCELED) {
            finish()
        }
    }

    val `this`: WaitlistActivity1
        get() = this

    companion object {
        private const val minGuests = 1
        private const val maxGuests = 8
    }
}