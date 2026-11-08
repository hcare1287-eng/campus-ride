package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.CampusData
import com.example.data.model.CampusShortcut
import com.example.data.model.Ride
import com.example.data.model.UserProfile
import com.example.data.model.VehicleOption
import com.example.data.repository.CampusRideRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainViewModel(
    private val repository: CampusRideRepository,
    val currentUserId: String
) : ViewModel() {

    // User profile observation
    val userProfile: StateFlow<UserProfile?> = repository.observeUserProfile(currentUserId)
        .catch { emit(null) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = null
        )

    // User active rides observation
    val userRides: StateFlow<List<Ride>> = repository.observeUserRides()
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

    private val _pickupInput = MutableStateFlow("Hostel Block 4 (Kaveri)")
    val pickupInput: StateFlow<String> = _pickupInput.asStateFlow()

    private val _dropInput = MutableStateFlow("Central Library")
    val dropInput: StateFlow<String> = _dropInput.asStateFlow()

    private val _isDriverMode = MutableStateFlow(false)
    val isDriverMode: StateFlow<Boolean> = _isDriverMode.asStateFlow()

    private val _showBookingSheet = MutableStateFlow(false)
    val showBookingSheet: StateFlow<Boolean> = _showBookingSheet.asStateFlow()

    private val _showProfileSheet = MutableStateFlow(false)
    val showProfileSheet: StateFlow<Boolean> = _showProfileSheet.asStateFlow()

    private val _showSosDialog = MutableStateFlow(false)
    val showSosDialog: StateFlow<Boolean> = _showSosDialog.asStateFlow()

    private val _showDriverOfferDialog = MutableStateFlow(false)
    val showDriverOfferDialog: StateFlow<Boolean> = _showDriverOfferDialog.asStateFlow()

    private val _statusNotice = MutableStateFlow<String?>(null)
    val statusNotice: StateFlow<String?> = _statusNotice.asStateFlow()

    init {
        // Initialize user profile if first time
        viewModelScope.launch {
            val defaultProfile = UserProfile(
                userId = currentUserId,
                displayName = "Campus Student",
                collegeId = "IIT-2024-EV-88",
                isStudentVerified = true,
                rating = 4.9,
                ridesCount = 8L,
                ecoPoints = 420L,
                role = "both"
            )
            repository.saveUserProfile(defaultProfile)
        }
    }

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

    fun openDriverOfferDialog(open: Boolean) {
        _showDriverOfferDialog.value = open
    }

    fun toggleDriverMode() {
        _isDriverMode.value = !_isDriverMode.value
        _statusNotice.value = if (_isDriverMode.value) {
            "Driver Mode Active: You can now pick up classmates along your route!"
        } else {
            "Rider Mode Active: Book quick peer pickups across campus."
        }
    }

    fun clearNotice() {
        _statusNotice.value = null
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
            riderName = userProfile.value?.displayName?.ifBlank { "Student Rider" } ?: "Student Rider",
            driverId = null,
            driverName = null,
            vehicleType = vehicle.name,
            pickupLocation = _pickupInput.value.ifBlank { "Hostel Zone" },
            dropLocation = _dropInput.value.ifBlank { "Department Labs" },
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
                _statusNotice.value = "Ride requested! Matching with nearby student riders..."
                // Simulate peer student driver matching in 2 seconds
                val rideId = result.getOrNull()
                if (rideId != null) {
                    kotlinx.coroutines.delay(2000L)
                    simulatePeerDriverMatch(rideId)
                }
            } else {
                _statusNotice.value = "Failed to book ride: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    private fun simulatePeerDriverMatch(rideId: String) {
        val campusDrivers = listOf(
            Triple("Arjun Rao", "CAMPUS-EV-04 (Ather 450X)", 2L),
            Triple("Priya Nair", "CAMPUS-BIKE-12 (Smart EV)", 3L),
            Triple("Vikram Sen", "CAMPUS-EV-09 (Ola S1)", 1L)
        )
        val selected = campusDrivers[Random.nextInt(campusDrivers.size)]

        viewModelScope.launch {
            repository.updateRideStatus(
                rideId = rideId,
                newStatus = "accepted",
                driverId = "peer_driver_${(10..99).random()}",
                driverName = selected.first,
                vehiclePlate = selected.second,
                etaMinutes = selected.third
            )
            _statusNotice.value = "Matched! ${selected.first} is heading your way with ${selected.second}!"
        }
    }

    fun advanceRide(ride: Ride) {
        viewModelScope.launch {
            when (ride.status) {
                "requested" -> {
                    simulatePeerDriverMatch(ride.id)
                }
                "accepted" -> {
                    repository.updateRideStatus(
                        rideId = ride.id,
                        newStatus = "in_route",
                        etaMinutes = 2L
                    )
                    _statusNotice.value = "OTP verified! Ride is now in route across campus."
                }
                "in_route" -> {
                    repository.updateRideStatus(
                        rideId = ride.id,
                        newStatus = "completed",
                        etaMinutes = 0L
                    )
                    // Reward eco points
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

    fun updateStudentId(newRoll: String) {
        val currentProf = userProfile.value ?: return
        viewModelScope.launch {
            repository.saveUserProfile(currentProf.copy(collegeId = newRoll, isStudentVerified = true))
            _statusNotice.value = "Student ID verification updated: $newRoll"
        }
    }
}
