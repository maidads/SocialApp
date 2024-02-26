package com.example.androidprojectma23

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.PopupWindow
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.launch

class LandingPageActivity : AppCompatActivity() {
    interface OnFilterSelectionChangedListener {
        fun onSelectionChanged(selectedCount: Int)
    }

    private val storageRef = Firebase.storage.reference
    private val firestore = Firebase.firestore

    private lateinit var geoLocationManager: GeoLocationManager

    private val userProfileManager = UserProfileManager(storageRef, firestore)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_page)

        geoLocationManager = GeoLocationManager(applicationContext, this)

        val navBar: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        navBar.selectedItemId = R.id.findFriendsFragment

        val topAppBar: MaterialToolbar = findViewById(R.id.topAppBar)
        setSupportActionBar(topAppBar)

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


            // Hämtar fragmentet för event sidan
                R.id.activityFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentHolder, EventPageFragment())
                        .commit()
                    true
                }

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
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        userProfileManager.getUserInterestsIcons(userId, onSuccess = { icons ->
            val layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val popupView = layoutInflater.inflate(R.layout.menu_popup_recycler_view, null)


            var selectedCount = 0

            val recyclerView: RecyclerView = popupView.findViewById(R.id.popup_recycler_view)
            recyclerView.layoutManager = LinearLayoutManager(this)

            recyclerView.adapter = FilterMenuAdapter(icons) { count ->
                selectedCount = count
            }

            val popupWidth = resources.getDimensionPixelSize(R.dimen.popup_width)
            val popupHeight = WindowManager.LayoutParams.WRAP_CONTENT

            val popupWindow = PopupWindow(popupView, popupWidth, popupHeight, true).apply {
                isFocusable = true
                elevation = 10.0f
            }

            val saveButton: Button = popupView.findViewById(R.id.filter_save_button)
            saveButton.setOnClickListener {

                val fragment = supportFragmentManager.findFragmentById(R.id.fragmentHolder) as? FindFriendsFragment

                fragment?.let { frag ->
                    lifecycleScope.launch {
                        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
                            Toast.makeText(this@LandingPageActivity, "Användar-ID inte tillgängligt.", Toast.LENGTH_SHORT).show()
                            return@launch
                        }

                        val currentLocation = geoLocationManager.getCurrentLocation(userId)
                        if (currentLocation != null) {

                            frag.fetchAndDisplayMatchingUsers(selectedCount, currentLocation.latitude, currentLocation.longitude)
                        } else {
                            Toast.makeText(this@LandingPageActivity, "Kunde inte hämta nuvarande plats.", Toast.LENGTH_SHORT).show()
                        }

                        popupWindow.dismiss()
                    }
                }
            }

            val anchorView = findViewById<View>(R.id.filter)
            val location = IntArray(2)
            anchorView.getLocationOnScreen(location)
            val xOff = location[0] + anchorView.width - popupWidth
            val yOff = 0

            popupWindow.showAsDropDown(anchorView, xOff, yOff)

        }, onFailure = { exception ->

        })
    }


}