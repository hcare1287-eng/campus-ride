package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class Ride(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val riderId: String? = null,
    val riderName: String = "",
    val driverId: String? = null,
    val driverName: String? = null,
    val driverAvatar: String? = null,
    val vehicleType: String? = "Electric Scooter",
    val vehiclePlate: String? = null,
    val pickupLocation: String = "",
    val dropLocation: String = "",
    val fare: Long = 15L,
    val splitFare: Boolean = false,
    val splitCount: Long = 1L,
    val otp: String = "1234",
    val status: String = "requested", // requested, accepted, in_route, completed, cancelled
    val ecoSavedGrams: Long = 350L,
    val etaMinutes: Long = 3L,
    val helmetVerified: Boolean = true,
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    @ServerTimestamp
    val updatedAt: Timestamp? = null
) {
    fun toCreateMap(): Map<String, Any?> {
        return mapOf(
            "userId" to userId,
            "riderId" to (riderId ?: userId),
            "riderName" to riderName,
            "driverId" to driverId,
            "driverName" to driverName,
            "driverAvatar" to driverAvatar,
            "vehicleType" to vehicleType,
            "vehiclePlate" to vehiclePlate,
            "pickupLocation" to pickupLocation,
            "dropLocation" to dropLocation,
            "fare" to fare,
            "splitFare" to splitFare,
            "splitCount" to splitCount,
            "otp" to otp,
            "status" to status,
            "ecoSavedGrams" to ecoSavedGrams,
            "etaMinutes" to etaMinutes,
            "helmetVerified" to helmetVerified
        ).filterValues { it != null }
    }
}
