package com.example.androidprojectma23

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore


class FindFriendsFragment : Fragment() {

    private lateinit var adapter: ProfileCardAdapter
    private val users = mutableListOf<User>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDataFirestore()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_find_friends, container, false)

        adapter = ProfileCardAdapter()
        adapter.setProfiles(users)

        val recyclerView = view.findViewById<RecyclerView>(R.id.profilesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.adapter = adapter

        val callback = ItemMoveCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(recyclerView)

        return view
    }

    private fun getDataFirestore(){
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .get()
            .addOnSuccessListener { result->
                val matchingFriendsList = ArrayList<User>()
                for (document in result) {
                    val displayName = document.getString("displayName")
                    val profileImage = document.getString("profileImage")
                    val interest = document.getString("interests")
                    val age = document.getString("age")

                    if (displayName != null && profileImage != null && interest != null && age != null) {
                       val user = User(displayName, profileImage, interest, age)
                       matchingFriendsList.add(user)
                    }
                }

                activity?.runOnUiThread {
                    adapter.setProfiles(matchingFriendsList)
                }
            }
    }
}