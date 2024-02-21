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
import ch.hsr.geohash.GeoHash

class GeoLocationManager(private val context: Context, private val activity: Activity) {

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }

    private var locationManager: LocationManager? = null

    init {
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

            return
        }

        locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, object : LocationListener {
            override fun onLocationChanged(location: Location) {
                val geohash = GeoHash.withCharacterPrecision(location.latitude, location.longitude, 12).toBase32()
                onHashReceived(geohash)

                locationManager?.removeUpdates(this)
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }, null)
    }
}
