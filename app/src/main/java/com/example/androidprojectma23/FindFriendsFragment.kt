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
    private val matchingFriendsList = mutableListOf<User>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDataFirestore()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_find_friends, container, false)

        adapter = ProfileCardAdapter(matchingFriendsList)

        val recyclerView = view.findViewById<RecyclerView>(R.id.profilesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.adapter = adapter

        val callback = ItemMoveCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(recyclerView)

        return view
    }

    private fun getDataFirestore() {
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                matchingFriendsList.clear()
                for (document in result) {
                    val displayName = document.getString("displayName")
                    val profileImage = document.getString("profileImage")
                    val interests = document.getString("interests")
                    val age = document.getString("age")

                    if (displayName != null && profileImage != null && interests != null && age != null) {
                        val user = User(displayName, profileImage, interests, age)
                        matchingFriendsList.add(user)
                    }

                    matchingFriendsList.add(User("Alice", "alice.jpg", "Böcker, Musikk", "25"))
                    matchingFriendsList.add(User("Bob", "bob.jpg", "Sport, Matlagning", "30"))
                    matchingFriendsList.add(User("Charlie", "charlie.jpg", "Resor, Fotografering", "35"))
                }

                activity?.runOnUiThread {
                    adapter.notifyDataSetChanged()
                }
            }
    }
}