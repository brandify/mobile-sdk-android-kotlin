//MultiLevelListAdapter Source: https://android-arsenal.com/details/1/3082
package com.brandify.brandifySDKDemo.hamburgerMenu

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.brandify.brandifySDKDemo.R
import pl.openrnd.multilevellistview.ItemInfo
import pl.openrnd.multilevellistview.MultiLevelListAdapter

//This class represents the blueprint for the hamburger menu list view items in order to populate them
class HamburgerMenuListViewAdapter(private val appContext: Context) : MultiLevelListAdapter() {
    private class ViewHolder {
        var imageView: ImageView? = null
        var descriptionView: TextView? = null
        var arrowView: ImageView? = null
    }

    public override fun isExpandable(obj: Any): Boolean {
        return HamburgerDP.isExpandable(obj as SingleCell)
    }

    public override fun getSubObjects(obj: Any): List<SingleCell?> {
        return HamburgerDP.getSubObjects(obj as SingleCell)
    }

    public override fun getViewForObject(item: Any, convertViewParam: View?, itemInfo: ItemInfo): View {
        val convertView: View
        val viewHolder: ViewHolder
        if (item is GroupCell) //Expandable cell
        {
            convertView = LayoutInflater.from(appContext).inflate(R.layout.hamburger_expandable_cell, null)
            viewHolder = ViewHolder()
            viewHolder.descriptionView = convertView.findViewById<View>(R.id.cellDescription) as TextView
            viewHolder.arrowView = convertView.findViewById<View>(R.id.cellArrow) as ImageView
            viewHolder.imageView = convertView.findViewById<View>(R.id.cellImage) as ImageView
            if ((item as SingleCell).imageId != -1) {
                viewHolder.imageView?.setImageResource((item as SingleCell).imageId)
            }
            viewHolder.descriptionView?.text = (item as SingleCell).cellDescription
            if (viewHolder.arrowView == null) {
                val view = LayoutInflater.from(appContext).inflate(R.layout.hamburger_expandable_cell, null)
                viewHolder.arrowView = view.findViewById<View>(R.id.cellArrow) as ImageView
            }
            viewHolder.arrowView?.visibility = View.VISIBLE
            viewHolder.arrowView?.setImageResource(if (itemInfo.isExpanded) R.drawable.arrow_up else R.drawable.arrow_down)
        } else  //Non-Expandable cell
        {
            convertView = LayoutInflater.from(appContext).inflate(R.layout.hamburger_single_cell, null)
            viewHolder = ViewHolder()
            viewHolder.descriptionView = convertView.findViewById<View>(R.id.cellDescription) as TextView
            viewHolder.imageView = convertView.findViewById<View>(R.id.cellImage) as ImageView
            if ((item as SingleCell).imageId != -1) {
                viewHolder.imageView?.setImageResource(item.imageId)
            }
            viewHolder.descriptionView?.text = item.cellDescription
        }
        return convertView
    }
}