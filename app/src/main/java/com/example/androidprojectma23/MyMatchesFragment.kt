package com.example.androidprojectma23

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class MyMatchesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var matchesAdapter: MyMatchesAdapter

    companion object {
        private const val ARG_USER_IDS = "userIds"

        fun newInstance(userIds: ArrayList<String>): MyMatchesFragment {
            val fragment = MyMatchesFragment()
            val args = Bundle()
            args.putStringArrayList(ARG_USER_IDS, userIds)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val userIds = it.getStringArrayList(ARG_USER_IDS)
            // Logga användar-ID:n
            Log.d("MyMatchesFragment", "Mottagna användar-ID:n: $userIds")

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_my_matches, container, false)

        recyclerView = view.findViewById(R.id.matchesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)


        val userIds = arguments?.getStringArrayList(ARG_USER_IDS)

        val users = fetchDataBasedOnUserIds(userIds)

        matchesAdapter = MyMatchesAdapter(users)
        recyclerView.adapter = matchesAdapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getStringArrayList(ARG_USER_IDS)?.let { userIds ->
            fetchUsers(userIds) { users ->
                setupRecyclerView(users)
            }
        }
    }

    private fun fetchUsers(userIds: ArrayList<String>, onComplete: (List<User>) -> Unit) {
        val firestore = FirebaseFirestore.getInstance()
        val users = mutableListOf<User>()

        val tasks = userIds.map { userId ->
            firestore.collection("users").document(userId).get()
        }

        Tasks.whenAllSuccess<DocumentSnapshot>(tasks)
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.toObject(User::class.java)?.let { user ->
                        users.add(user)
                    }
                }
                onComplete(users)
            }
    }

    private fun setupRecyclerView(users: List<User>) {
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = MyMatchesAdapter(users)
    }

    private fun fetchDataBasedOnUserIds(userIds: List<String>?): List<User> {
        val users = mutableListOf<User>()
        userIds?.forEach { userId ->

            val user = fetchUserDetails(userId)
            users.add(user)
        }
        return users
    }

    private fun fetchUserDetails(userId: String): User {


        return User(userId, "Användarnamn", "Profilbild URL")
    }
}
