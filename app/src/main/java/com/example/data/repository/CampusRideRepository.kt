package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.Ride
import com.example.data.model.UserProfile
import com.example.data.util.OperationType
import com.example.data.util.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class CampusRideRepository(private val db: FirebaseFirestore) {

    // Resolves database ID from R.string.firestore_database_id
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    // Observe active user profile
    fun observeUserProfile(userId: String): Flow<UserProfile?> = flow {
        val path = "users/$userId"
        emitAll(
            db.collection("users").document(userId)
                .snapshots()
                .map { snapshot ->
                    if (snapshot.exists()) snapshot.toObject(UserProfile::class.java) else null
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val exceptionToLog = firestoreError ?: (error as? Exception)
                    if (exceptionToLog != null && auth.currentUser?.uid == userId) {
                        handleFirestoreError(exceptionToLog, OperationType.GET, path)
                    }
                    throw error
                }
        )
    }

    // Create or update user profile
    suspend fun saveUserProfile(profile: UserProfile): Result<Unit> {
        val uid = requireUserId()
        val path = "users/$uid"
        val docRef = db.collection("users").document(uid)

        val payload = mutableMapOf<String, Any?>(
            "userId" to uid,
            "displayName" to profile.displayName,
            "email" to profile.email,
            "collegeId" to profile.collegeId,
            "isStudentVerified" to profile.isStudentVerified,
            "avatarUrl" to profile.avatarUrl,
            "rating" to profile.rating,
            "ridesCount" to profile.ridesCount,
            "ecoPoints" to profile.ecoPoints,
            "role" to profile.role,
            "universityName" to profile.universityName,
            "faculty" to profile.faculty,
            "residency" to profile.residency,
            "phoneNumber" to profile.phoneNumber,
            "vehicleModel" to profile.vehicleModel,
            "vehiclePlate" to profile.vehiclePlate,
            "isProfileComplete" to profile.isProfileComplete,
            "updatedAt" to FieldValue.serverTimestamp()
        )

        return try {
            val exists = docRef.get().await().exists()
            if (!exists) {
                payload["createdAt"] = FieldValue.serverTimestamp()
            }
            docRef.set(payload.filterValues { it != null }).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    // Real-time observation of user's active campus rides as passenger
    fun observeUserRides(): Flow<List<Ride>> = flow {
        val uid = requireUserId()
        val path = "rides"
        emitAll(
            db.collection("rides")
                .whereEqualTo("userId", uid)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(Ride::class.java).sortedByDescending { it.createdAt }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    val exceptionToLog = firestoreError ?: (error as? Exception)
                    if (exceptionToLog != null && auth.currentUser?.uid == uid) {
                        handleFirestoreError(exceptionToLog, OperationType.LIST, path)
                    }
                    throw error
                }
        )
    }

    // Real-time observation of open available ride requests for Peer Drivers
    fun observeAvailableRides(): Flow<List<Ride>> = flow {
        requireUserId()
        val path = "rides"
        emitAll(
            db.collection("rides")
                .whereEqualTo("status", "requested")
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(Ride::class.java).sortedByDescending { it.createdAt }
                }
                .catch { error ->
                    val firestoreError = generateSequence(error) { it.cause }
                        .filterIsInstance<FirebaseFirestoreException>()
                        .firstOrNull()
                    if (firestoreError == null && error is CancellationException) throw error
                    emit(emptyList())
                }
        )
    }

    // Real-time observation of rides assigned to this peer driver
    fun observeDriverAssignedRides(): Flow<List<Ride>> = flow {
        val uid = requireUserId()
        val path = "rides"
        emitAll(
            db.collection("rides")
                .whereEqualTo("driverId", uid)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(Ride::class.java).sortedByDescending { it.createdAt }
                }
                .catch { error ->
                    emit(emptyList())
                }
        )
    }

    // Create a new campus ride request
    suspend fun bookRide(ride: Ride): Result<String> {
        val uid = requireUserId()
        val path = "rides"
        val docRef = db.collection(path).document()

        val payload = ride.toCreateMap().toMutableMap()
        payload["userId"] = uid
        payload["createdAt"] = FieldValue.serverTimestamp()
        payload["updatedAt"] = FieldValue.serverTimestamp()

        return try {
            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, "${docRef.path}")
            Result.failure(e)
        }
    }

    // Peer Driver claims an open ride request
    suspend fun claimRide(
        rideId: String,
        driverId: String,
        driverName: String,
        vehiclePlate: String,
        etaMinutes: Long
    ): Result<Unit> {
        require(rideId.isNotBlank()) { "Ride ID must not be blank" }
        val docRef = db.collection("rides").document(rideId)

        val updates = mutableMapOf<String, Any?>(
            "status" to "accepted",
            "driverId" to driverId,
            "driverName" to driverName,
            "vehiclePlate" to vehiclePlate,
            "etaMinutes" to etaMinutes,
            "updatedAt" to FieldValue.serverTimestamp()
        )

        return try {
            docRef.update(updates.filterValues { it != null }).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    // Verify OTP and transition to in_route
    suspend fun verifyOtpAndStartRide(
        rideId: String,
        enteredOtp: String,
        actualOtp: String
    ): Result<Unit> {
        if (enteredOtp.trim() != actualOtp.trim()) {
            return Result.failure(IllegalArgumentException("Incorrect OTP ($enteredOtp). Please check the 4-digit OTP shown on the rider's screen."))
        }
        return updateRideStatus(rideId = rideId, newStatus = "in_route", etaMinutes = 2L)
    }

    // Update ride state (e.g. in_route or completed)
    suspend fun updateRideStatus(
        rideId: String,
        newStatus: String,
        driverId: String? = null,
        driverName: String? = null,
        vehiclePlate: String? = null,
        etaMinutes: Long? = null
    ): Result<Unit> {
        require(rideId.isNotBlank()) { "Ride ID must not be blank" }
        val docRef = db.collection("rides").document(rideId)

        val updates = mutableMapOf<String, Any?>(
            "status" to newStatus,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        if (driverId != null) updates["driverId"] = driverId
        if (driverName != null) updates["driverName"] = driverName
        if (vehiclePlate != null) updates["vehiclePlate"] = vehiclePlate
        if (etaMinutes != null) updates["etaMinutes"] = etaMinutes

        return try {
            docRef.update(updates.filterValues { it != null }).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    // Cancel a ride
    suspend fun cancelRide(rideId: String): Result<Unit> {
        return updateRideStatus(rideId = rideId, newStatus = "cancelled")
    }

    // Real SOS Emergency Alert Broadcast
    suspend fun broadcastSosAlert(
        studentName: String,
        collegeId: String,
        latitude: Double,
        longitude: Double,
        nearestLandmark: String
    ): Result<String> {
        val uid = requireUserId()
        val docRef = db.collection("emergency_alerts").document()
        val payload = mapOf(
            "userId" to uid,
            "studentName" to studentName,
            "collegeId" to collegeId,
            "latitude" to latitude,
            "longitude" to longitude,
            "nearestLandmark" to nearestLandmark,
            "status" to "ACTIVE",
            "createdAt" to FieldValue.serverTimestamp()
        )
        return try {
            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
