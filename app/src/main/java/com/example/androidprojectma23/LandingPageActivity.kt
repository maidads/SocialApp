package com.example.androidprojectma23

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toolbar
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class LandingPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_page)

        val navBar: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        navBar.selectedItemId = R.id.findFriendsFragment

        val topAppBar: MaterialToolbar = findViewById(R.id.topAppBar)
        setSupportActionBar(topAppBar) // Gör topAppBar till ActionBar

        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.filter -> {
                    showFilterPopup()
                    true
                }
                else -> false
            }
        }

        navBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.findFriendsFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentHolder, FindFriendsFragment())
                        .commit()
                    true
                }
// TODO: Add fragments for activity and chat

//                R.id.activityFragment -> {
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.fragmentHolder, ActivityFragment())
//                        .commit()
//                    true
//                }
//
                R.id.chatFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentHolder, ChatFragment())
                        .commit()
                    true
                }

                else -> false
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_app_bar, menu)
        return true
    }


    private fun showFilterPopup() {
        // Get a reference to the topAppBar
        val anchorView = findViewById<MaterialToolbar>(R.id.topAppBar)
        val popup = PopupMenu(this, anchorView)
        popup.menuInflater.inflate(R.menu.filter_menu, popup.menu)

        popup.setOnMenuItemClickListener { menuItem ->
            // Handle click on the menu
            when (menuItem.itemId) {
                // Handle different menu choices
                else -> false
            }
        }
        popup.gravity = Gravity.END
        popup.show()
    }

}