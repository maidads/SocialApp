package com.example.androidprojectma23

import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class TopBarManager() {

    fun updateTopBar (activity: AppCompatActivity, fragmentTag: String){
        when (fragmentTag) {
            "FindFriendsFragment" -> {
                // Change icons based on which fragment is currently open and set click listeners
            }
            "EventPageFragment" -> {

            }
            "ChatFragment" -> {

            }
            "ChatConversationFragment" -> {

            }
            "MyProfileFragment" -> {

            }
            "EventDetailFragment" -> {

            }
        }
    }

}