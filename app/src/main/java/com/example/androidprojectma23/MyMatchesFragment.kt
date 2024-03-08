package com.example.androidprojectma23

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

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
            val userProfileManager = UserProfileManager(
                FirebaseStorage.getInstance().reference,
                FirebaseFirestore.getInstance()
            )

            if (userIds != null) {
                userProfileManager.getCurrentUserId { userId ->
                    if (userId != null) {
                        UserSharedPreferences.saveUserIds(requireContext(), userId, userIds.toSet())
                    } else {
                        // TODO Handle exception
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_matches, container, false)

        recyclerView = view.findViewById(R.id.matchesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        matchesAdapter = MyMatchesAdapter(emptyList()) { userId ->
            openUserDetailFragment(userId)
        }
        recyclerView.adapter = matchesAdapter

        fetchUsersAndDisplay()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userProfileManager = UserProfileManager(
            FirebaseStorage.getInstance().reference,
            FirebaseFirestore.getInstance()
        )

        userProfileManager.getCurrentUserId { userId ->
            if (userId != null) {

                val savedUserIds = UserSharedPreferences.getUserIds(requireContext(), userId)
                if (!savedUserIds.isNullOrEmpty()) {
                    Log.d(
                        "!!!",
                        "Återhämtade sparade användar IDn för användare $userId: $savedUserIds"
                    )
                } else {
                    Log.d("!!!", "Inga sparade användar IDn att återhämta för användare $userId")
                }
            } else {
                Log.d("!!!", "Ingen inloggad användare hittades")
            }
        }
    }


    private fun fetchUsersAndDisplay() {

        val userProfileManager = UserProfileManager(
            FirebaseStorage.getInstance().reference,
            FirebaseFirestore.getInstance()
        )

        userProfileManager.getCurrentUserId { userId ->
            if (userId != null) {

                val userIds = UserSharedPreferences.getUserIds(requireContext(), userId)
                    ?: return@getCurrentUserId
                val db = FirebaseFirestore.getInstance()
                val usersList = mutableListOf<User>()

                userIds.forEach { userId ->
                    db.collection("users").document(userId).get().addOnSuccessListener { document ->
                        if (document.exists()) {
                            val user = document.toObject(User::class.java)?.apply {
                                this.userId = document.id
                                this.profileImage = document.getString("profileImageUrl") ?: ""
                            }
                            user?.let { usersList.add(it) }

                            // Update UI after getting users
                            if (usersList.size == userIds.size) {
                                activity?.runOnUiThread {
                                    matchesAdapter.updateUsers(usersList)
                                }
                            }
                        }
                    }.addOnFailureListener {
                    }
                }
            }
        }
    }

    private fun openUserDetailFragment(userId: String) {
        val fragment = MyMatchesDetailFragment.newInstance(userId)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentHolder, fragment)
            .addToBackStack(MyMatchesDetailFragment::class.java.simpleName)
            .commit()
    }

}
