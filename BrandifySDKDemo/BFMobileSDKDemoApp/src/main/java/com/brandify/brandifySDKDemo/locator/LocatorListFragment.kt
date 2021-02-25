package com.brandify.brandifySDKDemo.locator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.ListFragment
import com.brandify.brandifySDKDemo.R

class LocatorListFragment : ListFragment() {
    @JvmField
    var listAdapter: LocatorListAdapter? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.locator_list_fragment, container, false)
    }

    override fun onResume() {
        super.onResume()
        (getListAdapter() as LocatorListAdapter?)?.notifyDataSetChanged()
    }

    override fun onListItemClick(l: ListView, v: View, pos: Int, id: Long) {
        val parentActivity = activity as LocatorActivity?
        parentActivity?.listToMapWithLocation(listAdapter?.getLocationAtPos(pos))
    }
}