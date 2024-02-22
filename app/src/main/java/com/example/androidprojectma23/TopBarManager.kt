package com.example.androidprojectma23

import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class TopBarManager(private var activity: AppCompatActivity) {
    private lateinit var topAppBar: MaterialToolbar
    private lateinit var filterMenuItem: MenuItem
    private lateinit var savedProfileMenuItem: MenuItem

    fun updateTopBar (fragmentTag: String){
        topAppBar = activity.findViewById(R.id.topAppBar)
        filterMenuItem = topAppBar.menu.findItem(R.id.filter)
        savedProfileMenuItem = topAppBar.menu.findItem(R.id.favorite)
        val upperRightIcons = listOf(filterMenuItem, savedProfileMenuItem)

        when (fragmentTag) {
            "FindFriendsFragment" -> {
                setMenuItemVisibility(upperRightIcons, true)
                showPageTitle(false, null)
                setUpperLeftIcon("profile")
            }
            "EventPageFragment" -> {
                setMenuItemVisibility(upperRightIcons, false)
                showPageTitle(false, null)
                setUpperLeftIcon("profile")
            }
            "ChatFragment" -> {
                setMenuItemVisibility(upperRightIcons, false)
                showPageTitle(false, null)
                setUpperLeftIcon("profile")
            }
            "ChatConversationFragment" -> {
                setMenuItemVisibility(upperRightIcons, false)
                val otherUserName = "Other user name" //Set name based on clicked conversation
                showPageTitle(true, otherUserName)
                setUpperLeftIcon("back")
            }
            "MyProfileFragment" -> {
                setMenuItemVisibility(upperRightIcons, false)
                showPageTitle(true, "Min Profil")
                setUpperLeftIcon("back")
            }
            "EventDetailFragment" -> {
                setMenuItemVisibility(upperRightIcons, false)
                val eventName = "Event name" //Set name based on clicked event
                showPageTitle(true, eventName)
                setUpperLeftIcon("back")
            }

            "MyFriendsFragment" -> {
                setMenuItemVisibility(upperRightIcons, false)
                showPageTitle(true, "Vänner")
                setUpperLeftIcon("back")
            }
        }
    }

    fun setMenuItemVisibility(menuItems: List<MenuItem>, visibility: Boolean) {
        menuItems.forEach { menuItem ->
            menuItem.isVisible = visibility
        }
    }
    fun showPageTitle(isVisible: Boolean, title: String?) {
        topAppBar.title = if (isVisible) title else null
    }

    fun setUpperLeftIcon(icon: String){
        when (icon) {
            "profile" -> {
                topAppBar.setNavigationIcon(R.drawable.baseline_person_24)
            }
            "back" -> {
                topAppBar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
            }
        }
    }
}