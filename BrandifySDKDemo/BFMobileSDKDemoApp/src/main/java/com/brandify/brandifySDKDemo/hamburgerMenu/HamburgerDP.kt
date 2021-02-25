package com.brandify.brandifySDKDemo.hamburgerMenu

import com.brandify.brandifySDKDemo.R
import java.util.*

object HamburgerDP {
    private var groupCells: ArrayList<GroupCell>? = null
    private var subItems: HashMap<GroupCell, List<SingleCell>>? = null

    val initialData: List<GroupCell>?
        get() {
            if (groupCells == null) {
                groupCells = ArrayList()
                groupCells?.add(GroupCell(R.drawable.side_menu_about, "Brandify"))
                groupCells?.add(GroupCell(R.drawable.side_menu_brandify, "Location"))
            }
            if (subItems == null) {
                subItems = HashMap()
                val brandifySubmenus: MutableList<SingleCell> = ArrayList()
                brandifySubmenus.add(SingleCell(R.drawable.side_menu_sub, "About Us"))
                val locationSubmenus: MutableList<SingleCell> = ArrayList()
                locationSubmenus.add(SingleCell(R.drawable.side_menu_sub, "Locator"))
                locationSubmenus.add(SingleCell(R.drawable.side_menu_sub, "Favorites"))
                subItems!![groupCells!![0]] = brandifySubmenus
                subItems!![groupCells!![1]] = locationSubmenus
            }
            return groupCells
        }

    @JvmStatic
    fun isExpandable(cell: SingleCell?): Boolean {
        return cell is GroupCell
    }

    @JvmStatic
    fun getSubObjects(cell: SingleCell?): List<SingleCell> {
        require(cell is GroupCell)
        return subItems!![cell]!!
    }
}