package com.example.androidprojectma23

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
            if (userIds != null) {
                UserSharedPreferences.saveUserIds(requireContext(), userIds.toSet())
            }
            // Logga användar-ID:n
            Log.d("MyMatchesFragment", "Mottagna användar-ID:n: $userIds")

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_my_matches, container, false)

        recyclerView = view.findViewById(R.id.matchesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        matchesAdapter = MyMatchesAdapter(emptyList())
        recyclerView.adapter = matchesAdapter

        fetchUsersAndDisplay()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val savedUserIds = UserSharedPreferences.getUserIds(requireContext())
        if (savedUserIds != null && savedUserIds.isNotEmpty()) {
            Log.d("MyMatchesFragment", "Återhämtade sparade användar-ID:n: $savedUserIds")
        } else {
            Log.d("MyMatchesFragment", "Inga sparade användar-ID:n att återhämta.")
        }
    }

    private fun fetchUsersAndDisplay() {
        val userIds = UserSharedPreferences.getUserIds(requireContext()) ?: return
        val db = FirebaseFirestore.getInstance()
        val usersList = mutableListOf<User>()

        userIds.forEach { userId ->
            db.collection("users").document(userId).get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val user = document.toObject(User::class.java)
                    user?.let { usersList.add(it) }

                    if (usersList.size == userIds.size) { // Kontrollera om alla användare är hämtade
                        matchesAdapter.updateUsers(usersList) // Uppdatera din RecyclerView
                    }
                }
            }.addOnFailureListener { exception ->
                Log.d("MyMatchesFragment", "Error getting documents: ", exception)
            }
        }
    }
}