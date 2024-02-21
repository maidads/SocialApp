package com.example.androidprojectma23

import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class TopBarManager(private var activity: AppCompatActivity) {
    //All main fragments: My profile icon/button upper left,
    //All sub fragments: Back icon/button upper left

    fun updateTopBar (fragmentTag: String){
        val topAppBar: MaterialToolbar = activity.findViewById(R.id.topAppBar)
        val filterMenuItem = topAppBar.menu.findItem(R.id.filter)
        val savedProfilesMenuItem = topAppBar.menu.findItem(R.id.favorite)

        when (fragmentTag) {
            "FindFriendsFragment" -> {
                filterMenuItem?.isVisible = true
                savedProfilesMenuItem?.isVisible = true
                topAppBar.title = null
                Log.d("!!!", "If friends is chosen from start")
            }
            "EventPageFragment" -> {
                filterMenuItem?.isVisible = false
                savedProfilesMenuItem?.isVisible = false
                topAppBar.title = null
                Log.d("!!!", "If event is runned")
            }
            "ChatFragment" -> {
                filterMenuItem?.isVisible = false
                savedProfilesMenuItem?.isVisible = false
                topAppBar.title = null
                //Remove icons upper right
            }
            "ChatConversationFragment" -> {
                topAppBar.title = "Other user name" //Get username
                //User displayname center
            }
            "MyProfileFragment" -> {
                topAppBar.title = "Min profil"
                //Fragment name center "Min Profil"
            }
            "EventDetailFragment" -> {
                topAppBar.title = "Event name"
                //Event name center
            }
        }
    }
}