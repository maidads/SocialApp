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
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage

class LandingPageActivity : AppCompatActivity(), TopBarManager.TopBarClickListener {
    interface OnFilterSelectionChangedListener {
        fun onSelectionChanged(selectedCount: Int)
    }

    private val storageRef = Firebase.storage.reference
    private val firestore = Firebase.firestore
    private lateinit var topAppBar: MaterialToolbar
    private lateinit var listener: FragmentManager.OnBackStackChangedListener
    private lateinit var topBarManager: TopBarManager
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    private val userProfileManager = UserProfileManager(storageRef, firestore)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_page)

        topBarManager = TopBarManager(this, this)
        topAppBar = findViewById(R.id.topAppBar)
        setSupportActionBar(topAppBar)
        updateTopBarWithInitialFragment()

        // FragmentTransactionListener for TopBarManager
        listener = FragmentManager.OnBackStackChangedListener {
            val fragmentTag = supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1).name
            fragmentTag?.let { topBarManager.updateTopBar(it) }
        }
        supportFragmentManager.addOnBackStackChangedListener(listener)

        val navBar: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navBar.selectedItemId = R.id.findFriendsFragment

        filterIconClickListener()

        navBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.findFriendsFragment -> replaceFragment(FindFriendsFragment())
                R.id.activityFragment -> replaceFragment(EventPageFragment())
                R.id.chatFragment -> replaceFragment(ChatFragment())
                else -> false
            }
        }

    }
    override fun onProfileIconClicked() {
        // Open MyProfileFragment
    }

    override fun onBackIconClicked() {
        // Better method?
        onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_app_bar, menu)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        supportFragmentManager.removeOnBackStackChangedListener(listener)
    }

    fun updateTopBarWithInitialFragment() {
        val initialFragmentTag = "FindFriendsFragment"
        topBarManager.updateTopBar(initialFragmentTag)
    }

    private fun replaceFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentHolder, fragment)
            .addToBackStack(fragment::class.java.simpleName)
            .commit()
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
                fragment?.fetchAndDisplayMatchingUsers(selectedCount)

                popupWindow.dismiss()
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

    private fun filterIconClickListener() {
        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.filter -> {
                    showFilterPopup()
                    true
                }
                else -> false
            }
        }

    }
}