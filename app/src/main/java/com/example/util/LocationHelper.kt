package com.example.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.example.data.model.CampusData
import com.example.data.model.CampusLandmark
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object LocationHelper {

    fun hasLocationPermission(context: Context): Boolean {
        val fineLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fineLocation || coarseLocation
    }

    fun requestCurrentLocation(
        context: Context,
        onSuccess: (latitude: Double, longitude: Double, landmarkName: String) -> Unit,
        onError: (String) -> Unit
    ) {
        if (!hasLocationPermission(context)) {
            onError("Location permission not granted. Please allow location access.")
            return
        }

        try {
            val fusedClient = LocationServices.getFusedLocationProviderClient(context)
            val cancelToken = CancellationTokenSource()

            fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancelToken.token)
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val nearest = findNearestDbuLandmark(location.latitude, location.longitude)
                        onSuccess(location.latitude, location.longitude, nearest.name)
                    } else {
                        // Fallback to last known location or DBU center
                        fusedClient.lastLocation.addOnSuccessListener { lastLoc: Location? ->
                            if (lastLoc != null) {
                                val nearest = findNearestDbuLandmark(lastLoc.latitude, lastLoc.longitude)
                                onSuccess(lastLoc.latitude, lastLoc.longitude, nearest.name)
                            } else {
                                // Default to DBU campus center
                                onSuccess(CampusData.DBU_LATITUDE, CampusData.DBU_LONGITUDE, "DBU Central Library (Kalam Block)")
                            }
                        }.addOnFailureListener {
                            onSuccess(CampusData.DBU_LATITUDE, CampusData.DBU_LONGITUDE, "DBU Central Library (Kalam Block)")
                        }
                    }
                }
                .addOnFailureListener { e ->
                    // Gracefully fallback to DBU location
                    onSuccess(CampusData.DBU_LATITUDE, CampusData.DBU_LONGITUDE, "DBU Central Library (Kalam Block)")
                }
        } catch (e: SecurityException) {
            onError("Security exception: ${e.message}")
        } catch (e: Exception) {
            onError("Location error: ${e.message}")
        }
    }

    fun findNearestDbuLandmark(lat: Double, lng: Double): CampusLandmark {
        return CampusData.DbuLandmarks.minByOrNull { landmark ->
            calculateDistanceMeters(lat, lng, landmark.latitude, landmark.longitude)
        } ?: CampusData.DbuLandmarks.first()
    }

    fun calculateDistanceMeters(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val r = 6371000.0 // Earth radius in meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
