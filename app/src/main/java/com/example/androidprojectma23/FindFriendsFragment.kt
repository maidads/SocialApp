package com.example.androidprojectma23

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.first


class FindFriendsFragment : Fragment() {

    private lateinit var adapter: ProfileCardAdapter
    private val matchingFriendsList = mutableListOf<User>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewLifecycleOwner.lifecycleScope.launch {
            // Get current user interests
            val currentUserInterests = getCurrentUserInterests().first()

            // Get other users interests
            val allUsersInterests = getAllUsersInterests().first()

            // Get display names and profile image URLs of all users
            val usersData = getUsersData().first()

            val matchingUsers = mutableListOf<User>()
            // Iterate through all users and compare their interests to the current user's interests
            for ((userId, interests) in allUsersInterests) {
                if (userId != FirebaseAuth.getInstance().currentUser?.uid) {
                    val commonInterests = findCommonInterests(currentUserInterests, interests)
                    if (commonInterests.isNotEmpty()) {
                        // Extract display name and profile image URL for the user
                        val displayName = usersData[userId]?.first ?: "Anonym"
                        val profileImageUrl = usersData[userId]?.second ?: ""
                        // Create a User object and add it to the list of matching users
                        matchingUsers.add(User(displayName, profileImageUrl, interests.toMutableList()))
                    }
                }
            }

            // Update RecyclerView with the list of matching users
            adapter.updateData(matchingUsers)
        }
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

    private fun getCurrentUserInterests(): Flow<List<String>> = flow {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val db = FirebaseFirestore.getInstance()
            val document = db.collection("users").document(userId).get().await()
            Log.d("!!!", "Hämtade intressen för användare $userId: ${document.get("interests")}")
            val interests = document.get("interests") as? List<String> ?: emptyList()
            emit(interests)
        }
    }.catch { e ->
        Log.e("!!!", "Error fetching user interests", e)
        emit(emptyList())
    }.flowOn(Dispatchers.IO)

    private fun getAllUsersInterests(): Flow<MutableMap<String, List<String>>> = flow {
        val database = FirebaseFirestore.getInstance()
        val usersCollection = database.collection("users")
        val snapshot = usersCollection.get().await()

        val usersInterestsMap = mutableMapOf<String, List<String>>()

        for (document in snapshot.documents) {
            val userId = document.id
            val interests = document.get("interests") as? List<String> ?: emptyList()
            usersInterestsMap[userId] = interests
        }

        emit(usersInterestsMap)
    }.catch { e ->
        Log.e("!!!", "Error fetching users interests", e)
        emit(mutableMapOf())
    }.flowOn(Dispatchers.IO)

    private fun findCommonInterests(currentUserInterests: List<String>, otherUserInterests: List<String>): List<String> {
        return currentUserInterests.intersect(otherUserInterests).toList()
    }

    private fun getUsersData(): Flow<Map<String, Pair<String, String>>> = flow {
        val db = FirebaseFirestore.getInstance()
        val usersCollection = db.collection("users")
        val snapshot = usersCollection.get().await()

        val usersData = mutableMapOf<String, Pair<String, String>>()

        for (document in snapshot.documents) {
            val userId = document.id
            val displayName = document.getString("displayName") ?: "Anonym"
            val profileImageUrl = document.getString("profileImageUrl") ?: "URL_to_profile_image_placeholder"
            usersData[userId] = Pair(displayName, profileImageUrl)
        }

        emit(usersData)
    }.catch { e ->
        Log.e("Tag", "Error fetching users data", e)
        emit(mutableMapOf())
    }.flowOn(Dispatchers.IO)

}