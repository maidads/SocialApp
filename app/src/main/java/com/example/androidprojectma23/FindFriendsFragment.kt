package com.example.androidprojectma23

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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


class FindFriendsFragment : Fragment(), LandingPageActivity.OnFilterSelectionChangedListener {

    override fun onSelectionChanged(selectedCount: Int) {
        fetchAndDisplayMatchingUsers(selectedCount)
    }

    private lateinit var adapter: ProfileCardAdapter
    private val matchingFriendsList = mutableListOf<User>()
    private lateinit var geoLocationManager: GeoLocationManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        geoLocationManager = GeoLocationManager(requireContext(), requireActivity())
        checkLocationPermissionAndProceed()

        val minimumNumberOfInterestRequired = 1

        fetchAndDisplayMatchingUsers(minimumNumberOfInterestRequired)
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("!!!", "onRequestPermissionsResult received.")
        if (requestCode == GeoLocationManager.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchCurrentUserLocation()

            } else {
                // Platsbehörighet nekades, fortsätt utan platsdata eller visa en förklaring

            }
        }
    }


    private fun setUpRecyclerView(view: View): RecyclerView {
        val recyclerView = view.findViewById<RecyclerView>(R.id.profilesRecyclerView)
        recyclerView.layoutManager = this.context?.let { NoScrollableRecycleView(it) }
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
        Log.e("!!!", "Error fetching users data", e)
        emit(mutableMapOf())
    }.flowOn(Dispatchers.IO)

    fun fetchAndDisplayMatchingUsers(minimumNumberOfInterestsRequired: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            val currentUserInterests = getCurrentUserInterests().first()
            val allUsersInterests = getAllUsersInterests().first()
            val usersData = getUsersData().first()

            val tempMatchingUsers = mutableListOf<User>()

            for ((userId, interests) in allUsersInterests) {
                if (userId != FirebaseAuth.getInstance().currentUser?.uid) {
                    val commonInterests = findCommonInterests(currentUserInterests, interests)
                    if (commonInterests.size >= minimumNumberOfInterestsRequired) {
                        val displayName = usersData[userId]?.first ?: "Anonym"
                        val profileImageUrl = usersData[userId]?.second ?: ""

                        val user = User(
                            displayName = displayName,
                            profileImage = profileImageUrl,
                            interests = interests.toMutableList(),
                            commonInterests = commonInterests.toMutableList()
                        )

                        tempMatchingUsers.add(user)
                    }
                }
            }

            val sortedMatchingUsers = tempMatchingUsers.sortedByDescending { it.commonInterests.size }

            adapter.updateData(sortedMatchingUsers)
        }
    }

    private fun fetchCurrentUserLocation() {
        Log.d("!!!", "Attempting to fetch current user location")
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            geoLocationManager.getCurrentLocationHash { geohash ->
                Log.d("!!!", "Current user geohash: $geohash")
                saveCurrentUserLocationToFirestore(geohash)
            }
        } else {
            // Begär platsbehörigheter
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), GeoLocationManager.LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    private fun fetchNearbyUsersLocation() {
        val userUid = FirebaseAuth.getInstance().currentUser?.uid
        val database = FirebaseFirestore.getInstance()
        val userRef = database.collection("users")

        // currentUserUid?.let

    }

    private fun saveCurrentUserLocationToFirestore(geohash: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let {
            val userRef = FirebaseFirestore.getInstance().collection("users").document(it)
            userRef.update("geohash", geohash)
                .addOnSuccessListener {
                    Log.d("!!!", "Saving geohash for user: $userId")
                }
                .addOnFailureListener {
                    e -> Log.w("!!!", "Error updating user geohash.", e)
                }
        }
    }

    private fun checkLocationPermissionAndProceed() {
        if (!geoLocationManager.checkLocationPermission()) {
            // Visa dialogen innan du begär behörigheten
            showLocationPermissionDialog()
        } else {
            fetchCurrentUserLocation()
        }
    }

    private fun showLocationPermissionDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Platsbehörighet krävs")
            .setMessage("För att ge dig den bästa möjliga upplevelsen och hjälpa dig att hitta nya vänner i närheten, behöver appen tillgång till din plats.")
            .setPositiveButton("OK") { _, _ ->
                // Begär platsbehörigheter när användaren accepterar
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), GeoLocationManager.LOCATION_PERMISSION_REQUEST_CODE)
            }
            .setNegativeButton("Avbryt") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }






}