package com.brandify.brandifySDKDemo.baseActivity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.drawerlayout.widget.DrawerLayout
import com.brandify.BrandifyMobileSDK.activity.BFBaseActivity
import com.brandify.BrandifyMobileSDK.providers.BFConfigurationDP
import com.brandify.BrandifyMobileSDK.providers.BFMobileSDK
import com.brandify.brandifySDKDemo.R
import com.brandify.brandifySDKDemo.about.AboutActivity
import com.brandify.brandifySDKDemo.beacon.BeaconActivity
import com.brandify.brandifySDKDemo.favorites.FavoritesActivity
import com.brandify.brandifySDKDemo.hamburgerMenu.HamburgerDP
import com.brandify.brandifySDKDemo.hamburgerMenu.HamburgerMenuListViewAdapter
import com.brandify.brandifySDKDemo.hamburgerMenu.SingleCell
import com.brandify.brandifySDKDemo.locator.LocatorActivity
import pl.openrnd.multilevellistview.ItemInfo
import pl.openrnd.multilevellistview.MultiLevelListView
import pl.openrnd.multilevellistview.OnItemClickListener
import java.util.*

abstract class BaseActivity : BFBaseActivity() {
    //Declare the UI components
    private var hamburgerMenuDrawerLayout: DrawerLayout? = null
    private var hamburgerMenuItemsListView: MultiLevelListView? = null
    private var hamburgerMenuDrawerButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(content)

        //Wire up the UI components
        hamburgerMenuDrawerLayout = findViewById<View>(R.id.hamburgerMenuDrawerLayout) as DrawerLayout
        hamburgerMenuItemsListView = findViewById<View>(R.id.hamburgerMenuListView) as MultiLevelListView
        hamburgerMenuDrawerButton = findViewById<View>(R.id.hamburger_menu_drawer_button_id) as Button

        //Now populate the navigation drawer (hamburger menu) with the options we created above by instantiating the adapter and setting it
        val adapter = HamburgerMenuListViewAdapter(applicationContext)
        hamburgerMenuItemsListView?.setAdapter(adapter)
        hamburgerMenuItemsListView?.setOnItemClickListener(mOnItemClickListener)
        adapter.setDataItems(HamburgerDP.initialData)

        //Implement on click listener for the hamburger menu drawer button
        hamburgerMenuDrawerButton?.setOnClickListener {
            //Open the drawer and hide the hamburger button
            if (hamburgerMenuItemsListView != null) {
                hamburgerMenuDrawerLayout?.openDrawer(hamburgerMenuItemsListView!!)
            }
        }
    }

    override fun onReadyForInitialization(locationPermissionsGranted: Boolean) {
        BFConfigurationDP.context = applicationContext
        BFMobileSDK.initialize(this, false, false)
    }

    private val mOnItemClickListener: OnItemClickListener = object : OnItemClickListener {
        override fun onItemClicked(parent: MultiLevelListView, view: View, item: Any, itemInfo: ItemInfo) {
            require(item is SingleCell) { "" }
            var intent: Intent? = null
            when (item.cellDescription.toLowerCase(Locale.ROOT)) {
                "about us" -> intent = Intent(applicationContext, AboutActivity::class.java)
                "locator" -> intent = Intent(applicationContext, LocatorActivity::class.java)
                "favorites" -> intent = Intent(applicationContext, FavoritesActivity::class.java)
                "ibeacon" -> intent = Intent(applicationContext, BeaconActivity::class.java)
                else -> {
                }
            }
            if (intent != null) {
                startActivity(intent)
                if (content != R.layout.home_activity) {
                    thisClass().finish()
                }
            }
        }

        override fun onGroupItemClicked(parent: MultiLevelListView, view: View, item: Any, itemInfo: ItemInfo) {
            return
        }
    }
    protected abstract val content: Int
    protected abstract fun thisClass(): Activity
}