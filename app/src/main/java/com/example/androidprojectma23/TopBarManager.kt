package com.example.androidprojectma23

import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class TopBarManager(
    private var activity: AppCompatActivity,
    private val clickListener: TopBarClickListener
) {
    private lateinit var topAppBar: MaterialToolbar
    private lateinit var filterMenuItem: MenuItem
    private lateinit var savedProfilesMenuItem: MenuItem

    interface TopBarClickListener {
        fun onProfileIconClicked()
        fun onBackIconClicked()
        fun onFilterMenuItemClicked()
        fun onSavedProfileMenuItemClicked()
        fun setTitle(title: String)
    }

    fun updateTopBar(fragmentTag: String) {
        topAppBar = activity.findViewById(R.id.topAppBar)
        filterMenuItem = topAppBar.menu.findItem(R.id.filter)
        savedProfilesMenuItem = topAppBar.menu.findItem(R.id.favorite)
        val upperRightIcons = listOf(filterMenuItem, savedProfilesMenuItem)

        when (fragmentTag) {
            "FindFriendsFragment" -> {
                setMenuItemVisibility(upperRightIcons, true)
                showPageTitle(false, null)
                setTopBarNavigationIcon("profile")
            }

            "EventPageFragment", "ChatFragment" -> {
                setMenuItemVisibility(upperRightIcons, false)
                showPageTitle(false, null)
                setTopBarNavigationIcon("profile")
            }

            "ChatConversationFragment" -> {
                setMenuItemVisibility(upperRightIcons, false)
                val otherUserName = "Other user name" //Set name based on clicked conversation
                showPageTitle(true, otherUserName)
                setTopBarNavigationIcon("back")
            }

            "MyProfileFragment" -> {
                setMenuItemVisibility(upperRightIcons, false)
                showPageTitle(true, "Min Profil")
                setTopBarNavigationIcon("back")
            }

            "EventDetailFragment" -> {
                setMenuItemVisibility(upperRightIcons, false)
                setTopBarNavigationIcon("back")
            }

            "MyFriendsFragment" -> {
                setMenuItemVisibility(upperRightIcons, false)
                showPageTitle(true, "Vänner")
                setTopBarNavigationIcon("back")
            }

            "SavedProfilesFragment" -> {
                setMenuItemVisibility(upperRightIcons, false)
                showPageTitle(true, "Sparade profiler")
                setTopBarNavigationIcon("back")
            }
        }
    }

    private fun setMenuItemVisibility(menuItems: List<MenuItem>, visibility: Boolean) {
        menuItems.forEach { menuItem ->
            menuItem.isVisible = visibility
        }
    }

    fun showPageTitle(isVisible: Boolean, title: String?) {
        topAppBar.title = if (isVisible) title else null
    }

    private fun setTopBarNavigationIcon(icon: String) {
        when (icon) {
            "profile" -> {
                topAppBar.setNavigationIcon(R.drawable.baseline_person_24)
                topAppBar.setNavigationOnClickListener { clickListener.onProfileIconClicked() }
            }

            "back" -> {
                topAppBar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
                topAppBar.setNavigationOnClickListener { clickListener.onBackIconClicked() }
            }
        }
    }

    fun setMenuItemClickListener() {

        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                filterMenuItem.itemId -> {
                    clickListener.onFilterMenuItemClicked()
                }

                savedProfilesMenuItem.itemId -> {
                    //Open SavedProfilesFragment
                    clickListener.onSavedProfileMenuItemClicked()
                }
            }
            true
        }
    }
}