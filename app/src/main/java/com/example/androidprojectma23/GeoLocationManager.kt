package com.example.androidprojectma23

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import ch.hsr.geohash.GeoHash
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CompletableDeferred

class GeoLocationManager(private val context: Context, private val activity: Activity) {

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }

    private var locationManager: LocationManager? = null

    init {
        Log.d("!!!", "LocationManager initialized.")
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
    }

    fun checkLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return false
        }
        return true
    }

    suspend fun getCurrentLocation(userId: String): User? {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return null
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        val deferredLocation = CompletableDeferred<Location?>()

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            deferredLocation.complete(location)
        }

        val location = deferredLocation.await()
        return location?.let {
            val geohash = GeoHash.withCharacterPrecision(it.latitude, it.longitude, 12).toBase32()
            User(
                userId = userId,
                geohash = geohash,
                latitude = it.latitude,
                longitude = it.longitude
            )
        }
    }


}
