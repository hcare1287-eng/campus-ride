package com.example.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.CampusData
import com.example.data.model.CampusShortcut
import com.example.data.model.Ride
import com.example.data.model.UserProfile
import com.example.data.model.VehicleOption
import com.example.data.repository.CampusRideRepository
import com.example.util.LocationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: CampusRideRepository,
    val currentUserId: String
) : ViewModel() {

    // User profile observation from Firestore
    val userProfile: StateFlow<UserProfile?> = repository.observeUserProfile(currentUserId)
        .catch { emit(null) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = null
        )

    // User active rides observation as passenger
    val userRides: StateFlow<List<Ride>> = repository.observeUserRides()
        .catch { emit(emptyList()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    // Open ride requests available for peer drivers to discover and accept
    val availableRides: StateFlow<List<Ride>> = repository.observeAvailableRides()
        .catch { emit(emptyList()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    // Rides where the current user is assigned as the peer driver
    val driverAssignedRides: StateFlow<List<Ride>> = repository.observeDriverAssignedRides()
        .catch { emit(emptyList()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    // UI state
    private val _selectedVehicle = MutableStateFlow(CampusData.Vehicles[0])
    val selectedVehicle: StateFlow<VehicleOption> = _selectedVehicle.asStateFlow()

    private val _splitFare = MutableStateFlow(false)
    val splitFare: StateFlow<Boolean> = _splitFare.asStateFlow()

    private val _splitCount = MutableStateFlow(2)
    val splitCount: StateFlow<Int> = _splitCount.asStateFlow()

    private val _pickupInput = MutableStateFlow("S. Lal Singh Boys Hostel")
    val pickupInput: StateFlow<String> = _pickupInput.asStateFlow()

    private val _dropInput = MutableStateFlow("DBU Central Library (Kalam Block)")
    val dropInput: StateFlow<String> = _dropInput.asStateFlow()

    private val _isDriverMode = MutableStateFlow(false)
    val isDriverMode: StateFlow<Boolean> = _isDriverMode.asStateFlow()

    private val _is3dViewEnabled = MutableStateFlow(true)
    val is3dViewEnabled: StateFlow<Boolean> = _is3dViewEnabled.asStateFlow()

    private val _currentLatitude = MutableStateFlow(CampusData.DBU_LATITUDE)
    val currentLatitude: StateFlow<Double> = _currentLatitude.asStateFlow()

    private val _currentLongitude = MutableStateFlow(CampusData.DBU_LONGITUDE)
    val currentLongitude: StateFlow<Double> = _currentLongitude.asStateFlow()

    private val _nearestLandmark = MutableStateFlow("DBU Central Library (Kalam Block)")
    val nearestLandmark: StateFlow<String> = _nearestLandmark.asStateFlow()

    private val _showBookingSheet = MutableStateFlow(false)
    val showBookingSheet: StateFlow<Boolean> = _showBookingSheet.asStateFlow()

    private val _showProfileSheet = MutableStateFlow(false)
    val showProfileSheet: StateFlow<Boolean> = _showProfileSheet.asStateFlow()

    private val _showSosDialog = MutableStateFlow(false)
    val showSosDialog: StateFlow<Boolean> = _showSosDialog.asStateFlow()

    private val _statusNotice = MutableStateFlow<String?>(null)
    val statusNotice: StateFlow<String?> = _statusNotice.asStateFlow()

    fun selectShortcut(shortcut: CampusShortcut) {
        _pickupInput.value = shortcut.pickup
        _dropInput.value = shortcut.dropoff
        _showBookingSheet.value = true
    }

    fun setVehicle(vehicle: VehicleOption) {
        _selectedVehicle.value = vehicle
    }

    fun toggleSplitFare(enabled: Boolean) {
        _splitFare.value = enabled
    }

    fun setSplitCount(count: Int) {
        if (count in 2..5) {
            _splitCount.value = count
        }
    }

    fun setPickupInput(value: String) {
        _pickupInput.value = value
    }

    fun setDropInput(value: String) {
        _dropInput.value = value
    }

    fun openBookingSheet(open: Boolean) {
        _showBookingSheet.value = open
    }

    fun openProfileSheet(open: Boolean) {
        _showProfileSheet.value = open
    }

    fun openSosDialog(open: Boolean) {
        _showSosDialog.value = open
    }

    fun toggleDriverMode() {
        _isDriverMode.value = !_isDriverMode.value
        _statusNotice.value = if (_isDriverMode.value) {
            "Driver Mode Active: Check the Driver Lobby to discover open requests!"
        } else {
            "Rider Mode Active: Request quick peer pickups across DBU campus."
        }
    }

    fun toggle3dView() {
        _is3dViewEnabled.value = !_is3dViewEnabled.value
        _statusNotice.value = if (_is3dViewEnabled.value) {
            "3D Digital Twin View enabled"
        } else {
            "Interactive Vector Map View enabled"
        }
    }

    fun clearNotice() {
        _statusNotice.value = null
    }

    fun refreshLocation(context: Context) {
        LocationHelper.requestCurrentLocation(
            context = context,
            onSuccess = { lat, lng, landmark ->
                _currentLatitude.value = lat
                _currentLongitude.value = lng
                _nearestLandmark.value = landmark
                _pickupInput.value = landmark
                _statusNotice.value = "Live GPS updated: $landmark (%.4f° N, %.4f° E)".format(lat, lng)
            },
            onError = { _ ->
                _currentLatitude.value = CampusData.DBU_LATITUDE
                _currentLongitude.value = CampusData.DBU_LONGITUDE
                _nearestLandmark.value = "DBU Central Library (Kalam Block)"
                _pickupInput.value = "DBU Central Library (Kalam Block)"
                _statusNotice.value = "Location synced to Desh Bhagat University (DBU) GT Road Grid"
            }
        )
    }

    fun completeProfileSetup(profile: UserProfile) {
        viewModelScope.launch {
            val finalProfile = profile.copy(
                userId = currentUserId,
                isProfileComplete = true
            )
            val result = repository.saveUserProfile(finalProfile)
            if (result.isSuccess) {
                if (finalProfile.role == "driver") {
                    _isDriverMode.value = true
                }
                _statusNotice.value = "DBU Campus Profile activated! Welcome to Campus Velocity."
            } else {
                _statusNotice.value = "Profile update failed: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun confirmBooking() {
        val vehicle = _selectedVehicle.value
        val isSplit = _splitFare.value
        val splits = if (isSplit) _splitCount.value else 1
        val finalFare = if (isSplit) (vehicle.baseFare / splits).coerceAtLeast(5L) else vehicle.baseFare
        val generatedOtp = (1000..9999).random().toString()

        val ride = Ride(
            userId = currentUserId,
            riderId = currentUserId,
            riderName = userProfile.value?.displayName?.ifBlank { "DBU Student" } ?: "DBU Student",
            driverId = null,
            driverName = null,
            vehicleType = vehicle.name,
            pickupLocation = _pickupInput.value.ifBlank { "S. Lal Singh Boys Hostel" },
            dropLocation = _dropInput.value.ifBlank { "DBU Central Library (Kalam Block)" },
            fare = finalFare,
            splitFare = isSplit,
            splitCount = splits.toLong(),
            otp = generatedOtp,
            status = "requested",
            ecoSavedGrams = 420L,
            etaMinutes = vehicle.etaMinutes.toLong(),
            helmetVerified = vehicle.helmetRequired
        )

        viewModelScope.launch {
            val result = repository.bookRide(ride)
            if (result.isSuccess) {
                _showBookingSheet.value = false
                _statusNotice.value = "Ride requested! Nearby DBU peer drivers can now see and accept your trip in the Lobby."
            } else {
                _statusNotice.value = "Failed to book ride: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    // Driver claims an open ride request from the Lobby
    fun driverAcceptRide(ride: Ride) {
        viewModelScope.launch {
            val profile = userProfile.value
            val driverName = profile?.displayName?.ifBlank { "DBU Peer Driver" } ?: "DBU Peer Driver"
            val vehiclePlate = profile?.vehiclePlate?.ifBlank { "PB-23-EV-4412" } ?: "PB-23-EV-4412"

            val result = repository.claimRide(
                rideId = ride.id,
                driverId = currentUserId,
                driverName = driverName,
                vehiclePlate = vehiclePlate,
                etaMinutes = 3L
            )

            if (result.isSuccess) {
                _statusNotice.value = "Ride accepted! Heading to ${ride.pickupLocation}. Enter passenger OTP to start trip."
            } else {
                _statusNotice.value = "Could not claim ride: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    // Driver enters the passenger's OTP to start the trip
    fun driverVerifyOtp(
        ride: Ride,
        enteredOtp: String,
        onError: (String) -> Unit,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.verifyOtpAndStartRide(
                rideId = ride.id,
                enteredOtp = enteredOtp,
                actualOtp = ride.otp
            )
            if (result.isSuccess) {
                _statusNotice.value = "OTP Verified! Trip is now in route."
                onSuccess()
            } else {
                val err = result.exceptionOrNull()?.message ?: "Invalid OTP"
                onError(err)
            }
        }
    }

    // Driver completes the trip
    fun driverCompleteRide(ride: Ride) {
        viewModelScope.launch {
            val result = repository.updateRideStatus(
                rideId = ride.id,
                newStatus = "completed",
                etaMinutes = 0L
            )
            if (result.isSuccess) {
                val profile = userProfile.value
                if (profile != null) {
                    repository.saveUserProfile(
                        profile.copy(
                            ecoPoints = profile.ecoPoints + 50L,
                            ridesCount = profile.ridesCount + 1L
                        )
                    )
                }
                _statusNotice.value = "Trip completed! Received ₹${ride.fare} and +50 Eco Points 🌱"
            }
        }
    }

    // Solo Tester Demo Helper: create a real ride request so a single device tester can test the driver lobby
    fun createDemoStudentRideRequest() {
        val testRide = Ride(
            userId = "demo_student_${(100..999).random()}",
            riderId = "demo_student_rider",
            riderName = "Aarav Patel (Computing)",
            driverId = null,
            driverName = null,
            vehicleType = "Campus EV Scooter",
            pickupLocation = "S. Lal Singh Boys Hostel",
            dropLocation = "Faculty of Eng. & Computing",
            fare = 20L,
            splitFare = true,
            splitCount = 2L,
            otp = "4421",
            status = "requested",
            ecoSavedGrams = 420L,
            etaMinutes = 3L,
            helmetVerified = true
        )

        viewModelScope.launch {
            repository.bookRide(testRide)
            _statusNotice.value = "Demo campus ride request created! Appears in Driver Lobby."
        }
    }

    // Advance ride for passenger view
    fun advanceRide(ride: Ride) {
        viewModelScope.launch {
            when (ride.status) {
                "in_route" -> {
                    repository.updateRideStatus(
                        rideId = ride.id,
                        newStatus = "completed",
                        etaMinutes = 0L
                    )
                    val currentProf = userProfile.value
                    if (currentProf != null) {
                        repository.saveUserProfile(
                            currentProf.copy(
                                ecoPoints = currentProf.ecoPoints + 50L,
                                ridesCount = currentProf.ridesCount + 1L
                            )
                        )
                    }
                    _statusNotice.value = "Ride completed! You earned +50 Eco-Points! 🌱"
                }
            }
        }
    }

    fun cancelRide(rideId: String) {
        viewModelScope.launch {
            repository.cancelRide(rideId)
            _statusNotice.value = "Ride cancelled."
        }
    }

    fun broadcastSosDistress() {
        viewModelScope.launch {
            val prof = userProfile.value
            val sName = prof?.displayName?.ifBlank { "DBU Student" } ?: "DBU Student"
            val cId = prof?.collegeId?.ifBlank { "DBU/2024/ENG/108" } ?: "DBU/2024/ENG/108"
            repository.broadcastSosAlert(
                studentName = sName,
                collegeId = cId,
                latitude = _currentLatitude.value,
                longitude = _currentLongitude.value,
                nearestLandmark = _nearestLandmark.value
            )
            _statusNotice.value = "Emergency distress beacon broadcasted to DBU security network!"
        }
    }

    fun updateStudentId(newRoll: String) {
        val currentProf = userProfile.value ?: return
        viewModelScope.launch {
            repository.saveUserProfile(currentProf.copy(collegeId = newRoll, isStudentVerified = true))
            _statusNotice.value = "Student ID verification updated: $newRoll"
        }
    }
}
