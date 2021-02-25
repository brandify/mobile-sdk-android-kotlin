package com.brandify.brandifySDKDemo.locator

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ToggleButton
import androidx.fragment.app.Fragment
import com.brandify.brandifySDKDemo.R
import java.util.*

class LocatorSearchFragment : Fragment() {
    private var searchText: EditText? = null
    private var skateServices: ToggleButton? = null
    private var lacrosseServices: ToggleButton? = null
    private var gloveServices: ToggleButton? = null
    private var golfServices: ToggleButton? = null
    private var archeryServices: ToggleButton? = null
    private var bikeServices: ToggleButton? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.locator_search_fragment, container, false)
        searchText = view.findViewById<View>(R.id.searchText) as EditText
        searchText?.setOnEditorActionListener { v, actionId, _ ->
            var searched = false
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                (activity as LocatorActivity?)?.runSearch(searchText?.text.toString(), filters)
                //close the keyboard
                val imm = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                searched = true
            }
            searched
        }
        val searchButton = view.findViewById<View>(R.id.searchButton) as Button
        searchButton.setOnClickListener { (activity as LocatorActivity?)?.runSearch(searchText?.text.toString(), filters) }
        val cancelButton = view.findViewById<View>(R.id.cancelButton) as Button
        cancelButton.setOnClickListener { (activity as LocatorActivity?)?.closeSearch() }
        skateServices = view.findViewById<View>(R.id.skateServices) as ToggleButton
        bikeServices = view.findViewById<View>(R.id.bikeServices) as ToggleButton
        golfServices = view.findViewById<View>(R.id.golfServices) as ToggleButton
        gloveServices = view.findViewById<View>(R.id.gloveServices) as ToggleButton
        archeryServices = view.findViewById<View>(R.id.archeryServices) as ToggleButton
        lacrosseServices = view.findViewById<View>(R.id.lacrosseServices) as ToggleButton
        return view
    }

    private val filters: HashMap<String, String>
        get() {
            val filter = HashMap<String, String>()
            if (skateServices?.isChecked == true) {
                filter["skateservices"] = "1"
            }
            if (lacrosseServices?.isChecked == true) {
                filter["lacrosseservices"] = "1"
            }
            if (bikeServices?.isChecked == true) {
                filter["bikeservices"] = "1"
            }
            if (gloveServices?.isChecked == true) {
                filter["gloveservices"] = "1"
            }
            if (golfServices?.isChecked == true) {
                filter["golfservices"] = "1"
            }
            if (archeryServices?.isChecked == true) {
                filter["archeryservices"] = "1"
            }
            return filter
        }
}