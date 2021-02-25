package com.brandify.brandifySDKDemo.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.brandify.brandifySDKDemo.R
import com.brandify.brandifySDKDemo.locator.LocatorActivity

//This Fragment represents main product menu fragment containing a choice of spec sounds
class LandingScreenFragment : Fragment() {
    //Override and implement onCreateView to instantiate the UI for this fragment
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //Declare the view this fragment will contain
        val landingScreenView = inflater.inflate(R.layout.landing_screen, container, false)

        val sdkLogoButton = landingScreenView.findViewById<View>(R.id.sdk_logo_button_id) as Button
        sdkLogoButton.setOnClickListener {
            val intent = Intent(activity, LocatorActivity::class.java)
            startActivity(intent)
        }
        return landingScreenView
    }
}