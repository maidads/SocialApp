package com.example.androidprojectma23

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidprojectma23.IconMapping.docIdToIconIdMap
import com.example.androidprojectma23.IconMapping.imageViewIdProfileCard
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

        val recyclerView = setUpRecyclerView(view)
        val touchHelper = setUpItemTouchHelper(adapter)
        touchHelper.attachToRecyclerView(recyclerView)

        return view
    }

    private fun setUpRecyclerView(view: View): RecyclerView {
        val recyclerView = view.findViewById<RecyclerView>(R.id.profilesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.adapter = adapter
        return recyclerView
    }

    private fun setUpItemTouchHelper(adapter: ProfileCardAdapter): ItemTouchHelper {
        val callback = ItemMoveCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        return touchHelper
    }

    private fun getDataFirestore() {
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                matchingFriendsList.clear()
                for (document in result) {
                    val displayName = document.getString("displayName")
                    val profileImage = document.getString("profileImageUrl")
                    val interestsList = document.get("interests") as? List<String>

                    if (displayName != null && profileImage != null && interestsList != null) {
                        val user = User(displayName, profileImage, interestsList)
                        matchingFriendsList.add(user)
                    }
                }
                adapter.updateData(matchingFriendsList)
                adapter.notifyDataSetChanged()
            }
    }
}