package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfile(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val displayName: String = "",
    val email: String? = null,
    val collegeId: String = "",
    val isStudentVerified: Boolean = true,
    val avatarUrl: String? = null,
    val rating: Double = 5.0,
    val ridesCount: Long = 0L,
    val ecoPoints: Long = 0L,
    val role: String = "passenger", // "passenger" or "driver"
    val universityName: String = "Desh Bhagat University",
    val faculty: String = "Faculty of Engineering & Technology",
    val residency: String = "S. Lal Singh Boys Hostel",
    val phoneNumber: String = "",
    val vehicleModel: String = "",
    val vehiclePlate: String = "",
    val isProfileComplete: Boolean = false,
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    @ServerTimestamp
    val updatedAt: Timestamp? = null
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "userId" to userId,
            "displayName" to displayName,
            "email" to email,
            "collegeId" to collegeId,
            "isStudentVerified" to isStudentVerified,
            "avatarUrl" to avatarUrl,
            "rating" to rating,
            "ridesCount" to ridesCount,
            "ecoPoints" to ecoPoints,
            "role" to role,
            "universityName" to universityName,
            "faculty" to faculty,
            "residency" to residency,
            "phoneNumber" to phoneNumber,
            "vehicleModel" to vehicleModel,
            "vehiclePlate" to vehiclePlate,
            "isProfileComplete" to isProfileComplete
        ).filterValues { it != null }
    }
}
