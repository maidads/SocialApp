package com.example.androidprojectma23

import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.hsr.geohash.GeoHash
import ch.hsr.geohash.WGS84Point
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.first
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import kotlin.math.sqrt
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext


class FindFriendsFragment : Fragment(), LandingPageActivity.OnFilterSelectionChangedListener {

    override fun onSelectionChanged(selectedCount: Int) {
        // Använda lifecycleScope för att starta en korutin
        viewLifecycleOwner.lifecycleScope.launch {
            if (geoLocationManager.checkLocationPermission()) {
                // Hämta användarens ID här
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
                    Toast.makeText(requireContext(), "Användar-ID är inte tillgängligt", Toast.LENGTH_LONG).show()
                    return@launch // Avsluta korutinen om userId inte finns
                }

                // Anta att getCurrentLocation nu är en suspend funktion eller anpassad för att använda CompletableDeferred
                val userLocation = geoLocationManager.getCurrentLocation(userId)
                userLocation?.let {
                    // Anropa din suspend funktion med den nuvarande platsen och användar-ID
                    fetchAndDisplayMatchingUsers(selectedCount, it.latitude, it.longitude)
                } ?: run {
                    // Hantera fallet då platsen inte kunde hämtas
                    Toast.makeText(requireContext(), "Kunde inte få aktuell plats", Toast.LENGTH_LONG).show()
                }
            } else {
                // Behörighet är inte beviljad; hanteringen bör redan vara på plats via checkLocationPermission
                Toast.makeText(requireContext(), "Platsbehörighet krävs", Toast.LENGTH_LONG).show()
            }
        }
    }


    private lateinit var adapter: ProfileCardAdapter
    private val matchingFriendsList = mutableListOf<User>()
    private lateinit var geoLocationManager: GeoLocationManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        geoLocationManager = GeoLocationManager(requireContext(), requireActivity())
        checkLocationPermissionAndProceed()

        val minimumNumberOfInterestRequired = 1

        viewLifecycleOwner.lifecycleScope.launch {

            checkLocationPermissionAndProceed()


            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {

                val userLocation = geoLocationManager.getCurrentLocation(userId)
                userLocation?.let {

                    fetchAndDisplayMatchingUsers(minimumNumberOfInterestRequired, it.latitude, it.longitude)
                } ?: run {

                    Toast.makeText(requireContext(), "Kunde inte få aktuell plats", Toast.LENGTH_LONG).show()
                }
            } else {

                Toast.makeText(requireContext(), "Användar-ID är inte tillgängligt", Toast.LENGTH_LONG).show()
            }
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("!!!", "onRequestPermissionsResult received.")
        if (requestCode == GeoLocationManager.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchCurrentUserLocation()

            } else {

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

    suspend fun fetchAndDisplayMatchingUsers(minimumNumberOfInterestsRequired: Int, currentLat: Double, currentLng: Double) {
        Log.d("displayMatch", "fetchAndDisplayMatchingUsers start")
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val allUsersInterests = getAllUsersInterests().first()
                val usersData = getUsersData().first()
                val nearbyUsers = fetchUsersWithinRadius(currentLat, currentLng, 50.0).filter { it.userId != currentUserUid }
                val currentUserInterests = getCurrentUserInterests().first()

                val tempMatchingUsers = nearbyUsers.filter { user ->
                    val interests = allUsersInterests[user.userId] ?: emptyList()
                    val commonInterests = interests.intersect(currentUserInterests).toList()
                    user.commonInterests = commonInterests.toMutableList()
                    commonInterests.size >= minimumNumberOfInterestsRequired
                }.map { user ->
                    usersData[user.userId]?.let { data ->
                        user.displayName = data.first
                        user.profileImage = data.second
                    }
                    user
                }.sortedByDescending { it.commonInterests.size }

                adapter.updateData(tempMatchingUsers)
                Log.d("displayMatch", "Matching users updated: $tempMatchingUsers")
            } catch (e: Exception) {
                Log.e("displayMatch", "Error fetching and displaying matching users", e)
            }
        }
    }


    private fun fetchCurrentUserLocation() {
        Log.d("!!!", "Attempting to fetch current user location")
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Log.d("!!!", "User ID not available, cannot fetch location")
            return
        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            viewLifecycleOwner.lifecycleScope.launch {
                val user = geoLocationManager.getCurrentLocation(userId)
                user?.let {
                    Log.d("!!!", "Current user location: Lat=${it.latitude}, Lng=${it.longitude}, Geohash=${it.geohash}")

                    saveCurrentUserLocationToFirestore(userId, it.geohash, it.latitude, it.longitude)
                } ?: run {
                    Log.d("!!!", "Could not fetch user location")
                    // Hantera fallet då platsen inte kunde hämtas
                }
            }
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), GeoLocationManager.LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    private fun saveCurrentUserLocationToFirestore(userId: String, geohash: String, latitude: Double, longitude: Double) {

        val locationUpdateMap = hashMapOf<String, Any>(
            "geohash" to geohash,
            "latitude" to latitude,
            "longitude" to longitude
        )


        val db = FirebaseFirestore.getInstance()

        db.collection("users").document(userId)
            .update(locationUpdateMap)
            .addOnSuccessListener {
                Log.d("!!!", "User location updated successfully")
            }
            .addOnFailureListener { e ->
                Log.e("!!!", "Error updating user location", e)
            }
    }

    private suspend fun fetchUsersWithinRadius(currentLat: Double, currentLng: Double, radiusInKm: Double): List<User> {
        Log.d("!!!", "fetchUsersWithinRadius start")

        val currentUserGeohash = calculateGeohash(currentLat, currentLng)

        val geohashRange = calculateGeohashRange(currentUserGeohash)
        val db = FirebaseFirestore.getInstance()
        val usersWithinRadius = mutableListOf<User>()

        withContext(Dispatchers.IO) {
            try {
                Log.d("!!!", "Fetching users within geohash range: ${geohashRange.first} to ${geohashRange.second}")

                val snapshot = Tasks.await(
                    db.collection("users")
                        .whereGreaterThanOrEqualTo("geohash", geohashRange.first)
                        .whereLessThanOrEqualTo("geohash", geohashRange.second)
                        .get()
                )

                for (document in snapshot.documents) {
                    val user = document.toObject(User::class.java)
                    if (user != null) {
                        user.userId = document.id
                        val distance = calculateDistance(currentLat, currentLng, user.latitude, user.longitude)
                        Log.d("!!!", "Distance to user ${user.userId}: $distance km")
                        if (distance <= radiusInKm) {
                            usersWithinRadius.add(user)
                        }
                    }
                }
            } catch (e: Exception) {

                Log.e("!!!", "Error fetching users within geohash range", e)
            }
        }

        Log.d("!!!", "fetchUsersWithinRadius end. Found ${usersWithinRadius.size} users within radius.")
        if (usersWithinRadius.isNotEmpty()) {
            usersWithinRadius.forEach { user ->
                Log.d("!!!", "User within radius: ${user.userId}")
            }
        } else {
            Log.d("!!!", "No users found within radius.")
        }
        return usersWithinRadius
    }


//    private fun fetchNearbyUsersLocation(): Flow<List<String>> = flow {
//        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
//        val database = FirebaseFirestore.getInstance()
//        val userRef = database.collection("users")
//        val nearbyUserIds = mutableListOf<String>()
//
//        currentUserUid?.let { uid ->
//            val document = userRef.document(uid).get().await() // Use kotlinx-coroutines to wait for results
//            val currentUserGeohash = document.getString("geohash") ?: return@flow
//            val geohashRange = calculateGeohashRange(currentUserGeohash)
//
//            val querySnapshot = userRef
//                .whereGreaterThanOrEqualTo("geohash", geohashRange.first)
//                .whereLessThanOrEqualTo("geohash", geohashRange.second)
//                .get()
//                .await() // Using kotlinx-coroutines to wait for results
//
//            for (snapshot in querySnapshot.documents) {
//                if (snapshot.id != uid) {
//                    nearbyUserIds.add(snapshot.id)
//                }
//            }
//        }
//
//        emit(nearbyUserIds)
//    }.catch { e ->
//        Log.e("fetchNearbyUsersLocation", "Error fetching nearby users", e)
//    }


    private fun calculateGeohashRange(geohash: String): Pair<String, String> {
        // Check too see that the geohash is longer than the amout of chars to remove
        val newLength = max(geohash.length - 3, 1)
        val baseGeohash = geohash.substring(0, newLength)
        val start = baseGeohash + "0".repeat(geohash.length - newLength)
        val end = baseGeohash + "z".repeat(geohash.length - newLength)
        return Pair(start, end)
    }


    private fun calculateGeohash(latitude: Double, longitude: Double): String {
        val point = WGS84Point(latitude, longitude)
        return GeoHash.geoHashStringWithCharacterPrecision(point.latitude, point.longitude, 4) // Justera precisionen efter behov
    }

//    private fun findGeohashNeighbors(geohash: String): List<String> {
//        val hash = GeoHash.fromGeohashString(geohash)
//        val neighbors = hash.adjacent
//        val geohashList = mutableListOf<String>()
//        neighbors.forEach { geohashList.add(it.toBase32()) }
//        return geohashList
//    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371 // Earth radius in km
        val latDistance = Math.toRadians(lat2 - lat1)
        val lonDistance = Math.toRadians(lon2 - lon1)
        val a = sin(latDistance / 2) * sin(latDistance / 2) +
                (cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                        sin(lonDistance / 2) * sin(lonDistance / 2))
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c // Distance in km
    }



    private fun checkLocationPermissionAndProceed() {
        if (!geoLocationManager.checkLocationPermission()) {

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