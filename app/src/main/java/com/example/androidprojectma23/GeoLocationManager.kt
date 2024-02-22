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
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return false
        }
        return true
    }

    fun getCurrentLocationHash(onHashReceived: (String) -> Unit) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("!!!", "Location permissions not granted")
            return
        } else {
            Log.d("!!!", "Location permissions granted, requesting updates...")
        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
            location?.let {
                val geohash = GeoHash.withCharacterPrecision(location.latitude, location.longitude, 12).toBase32()
                Log.d("!!!", "Location changed: $location")
                onHashReceived(geohash)
            }
        }
    }
}
