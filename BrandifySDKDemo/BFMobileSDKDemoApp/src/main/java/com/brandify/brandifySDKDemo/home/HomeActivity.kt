package com.brandify.brandifySDKDemo.home

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import com.brandify.brandifySDKDemo.R
import com.brandify.brandifySDKDemo.baseActivity.BaseActivity

class HomeActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setInitialMainFrameContent()
    }

    //Implement a method to load user settings and set the INITIAL main content frame when user logs in. This content includes products menu.
    private fun setInitialMainFrameContent() {
        //Load the main product menu fragment
        supportFragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.mainContentsFrameLayout, LandingScreenFragment()).commit()
    }

    override val content: Int
        get() = R.layout.home_activity

    override fun thisClass(): Activity {
        return this
    }
}