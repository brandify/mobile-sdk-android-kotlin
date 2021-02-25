package com.brandify.brandifySDKDemo.waitlist

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import com.brandify.BrandifyMobileSDK.models.BFDineTimeBooking
import com.brandify.BrandifyMobileSDK.models.BFRunnable
import com.brandify.BrandifyMobileSDK.providers.BFConfigurationDP
import com.brandify.BrandifyMobileSDK.providers.BFWaitListDP
import com.brandify.brandifySDKDemo.R

class WaitlistActivity2 : Activity() {
    private var firstNameText: EditText? = null
    private var lastNameText: EditText? = null
    private var emailText: EditText? = null
    private var phoneText: EditText? = null
    private var termsBox: CheckBox? = null
    private var updateBox: CheckBox? = null
    private var nextButton: Button? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_waitlist_2)

        //set the default result behavior when the activity is 'finish'-ed
        setResult(RESULT_OK)
        val numberOfGuests = intent.getIntExtra("NumberOfGuests", 0)
        val clientKey = intent.getStringExtra("ClientKey")
        val address = intent.getStringExtra("LocationAddress")
        firstNameText = findViewById<View>(R.id.firstName) as EditText
        lastNameText = findViewById<View>(R.id.lastName) as EditText
        phoneText = findViewById<View>(R.id.cellNumber) as EditText
        emailText = findViewById<View>(R.id.email) as EditText
        termsBox = findViewById<View>(R.id.terms) as CheckBox
        updateBox = findViewById<View>(R.id.updates) as CheckBox
        val addressTextView = findViewById<View>(R.id.locationAddress) as TextView
        addressTextView.text = address
        nextButton = findViewById<View>(R.id.nextButton) as Button
        val removeButton = findViewById<View>(R.id.removeButton) as Button
        val closeButton = findViewById<View>(R.id.closeButton) as Button
        closeButton.setOnClickListener {
            setResult(RESULT_CANCELED, null)
            finish()
        }
        val waitListDP = BFWaitListDP(this)
        val booking = waitListDP.dineTimeBookingForLocation(clientKey)
        if (booking != null) {
            if (booking.firstName != null) {
                nextButton!!.text = "Modify"
                firstNameText!!.setText(booking.firstName)
                lastNameText!!.setText(booking.lastName)
                phoneText!!.setText(booking.cellNumber)
                emailText!!.setText(booking.emailAddress)
                firstNameText!!.setSelection(firstNameText!!.length())
                removeButton.visibility = View.VISIBLE
                removeButton.setOnClickListener {
                    val configurationDP = BFConfigurationDP()
                    val configuration = configurationDP.getConfiguration(false)
                    waitListDP.deleteDineTimeWaitlistBooking(booking.visitId, configuration.accountName, configuration.appKey, clientKey, object : BFRunnable<BFDineTimeBooking?>() {
                        override fun run() {
                            waitListDP.removeVisitForLocationFromCache(clientKey)
                            nextButton!!.text = "Confirm"
                            val alertDialog = AlertDialog.Builder(`this`)
                            alertDialog.setTitle("Brandify")
                            alertDialog.setMessage("Your booking has been removed.")
                            alertDialog.setPositiveButton("OK") { _, _ ->
                                setResult(RESULT_CANCELED)
                                finish()
                            }
                            alertDialog.show()
                        }
                    })
                }
            }
        } else {
            removeButton.visibility = View.INVISIBLE
        }
        nextButton!!.setOnClickListener {
            val firstName = firstNameText!!.text.toString().trim { it <= ' ' }
            val lastName = lastNameText!!.text.toString().trim { it <= ' ' }
            val phone = phoneText!!.text.toString().trim { it <= ' ' }
            val email = emailText!!.text.toString().trim { it <= ' ' }
            var isValid = true
            var validationMessage = ""
            if (firstName.isEmpty()) {
                isValid = false
                validationMessage = "Please correct the guest name for the party."
            } else if (phone.isEmpty()) {
                isValid = false
                validationMessage = "Please provide a valid cell phone number."
            } else if (email.isEmpty()) {
                isValid = false
                validationMessage = "Please provide a valid email address.\""
            } else if (!termsBox!!.isChecked) {
                isValid = false
                validationMessage = "You must accept the Terms of Use and Privacy Policy to continue."
            }
            if (isValid) {
                //execute DineTime API
                val configurationDP = BFConfigurationDP()
                val configuration = configurationDP.getConfiguration(false)
                val waitListDPThis = BFWaitListDP(`this`)
                if (!waitListDPThis.checkExistingDineTimeWaitlistEntriesForLocation(clientKey)) {
                    waitListDPThis.addToDineTimeWaitlist(configuration.accountName, configuration.appKey, phone, numberOfGuests, firstName, lastName, email, updateBox!!.isChecked, true, clientKey, object : BFRunnable<BFDineTimeBooking?>() {
                        override fun run() {
                            val visitId = model!!.visitId
                            waitListDPThis.storeDineTimeVisitForLocation(clientKey, numberOfGuests, firstName, lastName, phone, email, visitId)
                            val alertDialog = AlertDialog.Builder(`this`)
                            alertDialog.setTitle("Brandify")
                            alertDialog.setMessage("Thanks! We'll see you soon.")
                            alertDialog.setPositiveButton("OK") { _, _ ->
                                setResult(RESULT_CANCELED)
                                finish()
                            }
                            alertDialog.show()
                        }
                    }
                    )
                } else {
                    val bookingDP = waitListDP.dineTimeBookingForLocation(clientKey)
                    if (bookingDP != null) {
                        waitListDP.modifyDineTimeWaitlistForVisit(bookingDP.visitId, configuration.accountName, configuration.appKey, phone, numberOfGuests, firstName, lastName, email, updateBox!!.isChecked, true, clientKey, object : BFRunnable<BFDineTimeBooking?>() {
                            override fun run() {
                                val visitId = model!!.visitId
                                waitListDP.storeDineTimeVisitForLocation(clientKey, numberOfGuests, firstName, lastName, phone, email, visitId)
                                val alertDialog = AlertDialog.Builder(`this`)
                                alertDialog.setTitle("Brandify")
                                alertDialog.setMessage("Your booking has been modified. We'll see you soon!")
                                alertDialog.setPositiveButton("OK") { _, _ ->
                                    setResult(RESULT_CANCELED)
                                    finish()
                                }
                                alertDialog.show()
                            }
                        }
                        )
                    }
                }
            } else {
                val alertDialog = AlertDialog.Builder(`this`)
                alertDialog.setTitle("Validation Warning")
                alertDialog.setMessage(validationMessage)
                alertDialog.setPositiveButton("OK", null)
                alertDialog.show()
            }
        }
    }

    val `this`: WaitlistActivity2
        get() = this
}