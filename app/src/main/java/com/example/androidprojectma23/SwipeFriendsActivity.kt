package com.example.androidprojectma23

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView

class SwipeFriendsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_swipe_friends)


        val navBar: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        navBar.selectedItemId = R.id.activityFragment

        navBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.findFriendsFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentHolder, FindFriendsFragment())
                        .commit()
                    true
                }

//                R.id.activityFragment -> {
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.fragmentHolder, ActivityFragment())
//                        .commit()
//                    true
//                }
//
//                R.id.chatFragment -> {
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.fragmentHolder, ChatFragment())
//                        .commit()
//                    true
//                }

                else -> false
            }
        }

    }

}