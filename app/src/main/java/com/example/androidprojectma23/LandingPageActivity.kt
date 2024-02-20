package com.example.androidprojectma23

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage

class LandingPageActivity : AppCompatActivity() {

    private val storageRef = Firebase.storage.reference
    private val firestore = Firebase.firestore
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    private val userProfileManager = UserProfileManager(storageRef, firestore)


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
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            // Ingen användare inloggad, visa ett meddelande eller hantera detta fall
            return
        }

        userProfileManager.getUserInterestsIcons(userId, onSuccess = { icons ->
            val layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val popupView = layoutInflater.inflate(R.layout.menu_popup_recycler_view, null)

            val recyclerView: RecyclerView = popupView.findViewById(R.id.popup_recycler_view)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = InterestsAdapter(icons)

            val popupWidth = resources.getDimensionPixelSize(R.dimen.popup_width) // Ange en dimension i res/dimens.xml
            val popupHeight = WindowManager.LayoutParams.WRAP_CONTENT

            val popupWindow = PopupWindow(popupView, popupWidth, popupHeight).apply {
                isFocusable = true
                elevation = 10.0f
            }

            // Använd ikonen som ankare för popup-fönstret
            val anchorView = findViewById<View>(R.id.filter)

            // Beräkna x-off så att popup-fönstret visas till höger om ankaret
            val location = IntArray(2)
            anchorView.getLocationOnScreen(location)
            val xOff = location[0] + anchorView.width - popupWidth
            val yOff = 0

            // Visa PopupWindow till höger om ankaret
            popupWindow.showAsDropDown(anchorView, xOff, yOff)

        }, onFailure = { exception ->
            // Hantera fel här
        })
    }




}