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

            "ChatConversationFragment", "EventDetailFragment" -> {
                setMenuItemVisibility(upperRightIcons, false)
                //Set top bar title in fragment
                setTopBarNavigationIcon("back")
            }

            "MyProfilePageFragment" -> {
                setMenuItemVisibility(upperRightIcons, false)
                showPageTitle(true, "Min Profil")
                setTopBarNavigationIcon("back")
            }

            "ChangeProfileFragment" -> {
                setMenuItemVisibility(upperRightIcons, false)
                showPageTitle(true, "Redigera Profil")
                setTopBarNavigationIcon("back")
            }

            "MyMatchesFragment" -> {
                setMenuItemVisibility(upperRightIcons, false)
                showPageTitle(true, "Matchningar")
                setTopBarNavigationIcon("back")
            }

            "MyMatchesDetailFragment" -> {
                setMenuItemVisibility(upperRightIcons, false)
                showPageTitle(true, "Mer info")
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