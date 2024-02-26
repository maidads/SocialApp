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
            // Först, säkerställ att vi har behörigheter.
            checkLocationPermissionAndProceed() // Se till att detta hanterar permissions asynkront om det behövs.

            // Hämta användarID från Firebase Auth.
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                // Om vi har ett userId, fortsätt med att hämta platsen.
                val userLocation = geoLocationManager.getCurrentLocation(userId)
                userLocation?.let {
                    // Använd platsinformationen som vanligt.
                    fetchAndDisplayMatchingUsers(minimumNumberOfInterestRequired, it.latitude, it.longitude)
                } ?: run {
                    // Om platsen inte kunde hämtas, visa ett meddelande.
                    Toast.makeText(requireContext(), "Kunde inte få aktuell plats", Toast.LENGTH_LONG).show()
                }
            } else {
                // Om userId inte finns tillgängligt, visa ett lämpligt meddelande.
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

    suspend fun fetchAndDisplayMatchingUsers(minimumNumberOfInterestsRequired: Int, currentLat: Double, currentLng: Double) {
        Log.d("!!!", "fetchAndDisplayMatchingUsers start")
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // Hämta alla användarintressen och användarinformation
                Log.d("!!!", "Fetching all users interests")
                val allUsersInterests = getAllUsersInterests().first()
                Log.d("!!!", "Fetching users data")
                val usersData = getUsersData().first()
                Log.d("!!!", "All users interests: $allUsersInterests")
                Log.d("!!!", "Users data: $usersData")

                // Antag att detta nu returnerar User-objekt direkt, inklusive all nödvändig information
                Log.d("!!!", "Fetching nearby users")
                val nearbyUsers = fetchUsersWithinRadius(currentLat, currentLng, 5000.0) // Anpassa radiegränsen efter behov
                Log.d("!!!", "Nearby users: $nearbyUsers")
                val currentUserInterests = getCurrentUserInterests().first()
                Log.d("!!!", "Current user interests: $currentUserInterests")

                val tempMatchingUsers = mutableListOf<User>()

                for (user in nearbyUsers) {
                    val interests = allUsersInterests[user.userId]
                    if (interests != null) {
                        val commonInterests = findCommonInterests(currentUserInterests, interests)

                        if (commonInterests.size >= minimumNumberOfInterestsRequired) {
                            // Hämta användarinformation för den matchande användaren
                            val userData = usersData[user.userId]
                            val displayName = userData?.first ?: "Anonym"
                            val profileImageUrl = userData?.second ?: ""

                            // Uppdatera användarens intressen och gemensamma intressen baserat på matchningen
                            user.interests = interests.toMutableList()
                            user.commonInterests = commonInterests.toMutableList()
                            user.displayName = displayName
                            user.profileImage = profileImageUrl

                            // Lägg till den uppdaterade användaren i listan med temporära matchande användare
                            tempMatchingUsers.add(user)
                        }
                    }
                }

                // Sortera matchande användare efter antal gemensamma intressen och uppdatera adaptern
                val sortedMatchingUsers = tempMatchingUsers.sortedByDescending { it.commonInterests.size }
                adapter.updateData(sortedMatchingUsers)

                Log.d("!!!", "Matching users updated: $sortedMatchingUsers")
            } catch (e: Exception) {
                Log.e("!!!", "Error fetching and displaying matching users", e)
            }
        }
    }






    private fun fetchCurrentUserLocation() {
        Log.d("!!!", "Attempting to fetch current user location")
        val userId = FirebaseAuth.getInstance().currentUser?.uid // Hämta användarID
        if (userId == null) {
            Log.d("!!!", "User ID not available, cannot fetch location")
            return // Avsluta om vi inte har ett användarID
        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Använda lifecycleScope för att starta en korutin
            viewLifecycleOwner.lifecycleScope.launch {
                val user = geoLocationManager.getCurrentLocation(userId) // Använd userId här om det behövs
                user?.let {
                    Log.d("!!!", "Current user location: Lat=${it.latitude}, Lng=${it.longitude}, Geohash=${it.geohash}")
                    // Här sparar vi all användarplatsinformation till Firestore
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
        // Skapa en Map med de värden du vill uppdatera
        val locationUpdateMap = hashMapOf<String, Any>(
            "geohash" to geohash,
            "latitude" to latitude,
            "longitude" to longitude
        )

        // Referera till Firestore databasen
        val db = FirebaseFirestore.getInstance()

        // Uppdatera användarens dokument i 'users' samlingen med det nya platsdatat
        db.collection("users").document(userId)
            .update(locationUpdateMap) // Använder `update` istället för `set` för att behålla befintliga fält oförändrade
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
        val geohashNeighbors = findGeohashNeighbors(currentUserGeohash)
        val db = FirebaseFirestore.getInstance()
        val usersWithinRadius = mutableListOf<User>()

        withContext(Dispatchers.IO) {
            geohashNeighbors.forEach { geohash ->
                try {
                    Log.d("!!!", "Fetching users for geohash: $geohash")
                    // Vänta synkront på att varje uppgift ska slutföras
                    val snapshot = Tasks.await(
                        db.collection("users")
                            .whereEqualTo("geohash", geohash)
                            .get()
                    )

                    for (document in snapshot.documents) {
                        val user = document.toObject(User::class.java)
                        if (user != null) {
                            val distance = calculateDistance(currentLat, currentLng, user.latitude, user.longitude)
                            Log.d("!!!", "Distance to user ${user.userId}: $distance km")
                            if (distance <= radiusInKm) {
                                usersWithinRadius.add(user)
                            }
                        }
                    }
                } catch (e: Exception) {
                    // Hantera undantag, t.ex. ExecutionException eller InterruptedException
                    Log.e("!!!", "Error fetching users for geohash $geohash", e)
                }
            }
        }

        Log.d("!!!", "fetchUsersWithinRadius end. Found ${usersWithinRadius.size} users within radius.")
        return usersWithinRadius
    }


    private fun calculateGeohash(latitude: Double, longitude: Double): String {
        val point = WGS84Point(latitude, longitude)
        return GeoHash.geoHashStringWithCharacterPrecision(point.latitude, point.longitude, 4) // Justera precisionen efter behov
    }

    private fun findGeohashNeighbors(geohash: String): List<String> {
        val hash = GeoHash.fromGeohashString(geohash)
        val neighbors = hash.adjacent
        val geohashList = mutableListOf<String>()
        neighbors.forEach { geohashList.add(it.toBase32()) }
        return geohashList
    }

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