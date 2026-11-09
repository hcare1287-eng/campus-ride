package com.example.ui.screens

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ElectricScooter
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CampusData
import com.example.data.model.CampusLandmark
import com.example.ui.MainViewModel
import com.example.ui.components.CampusMapCanvas
import com.example.ui.components.RideBookingSheet
import com.example.ui.components.RideTrackingHud
import com.example.ui.components.SosEmergencyDialog
import com.example.ui.theme.DeepSlateTertiary
import com.example.ui.theme.EcoGreenSecondary
import com.example.ui.theme.SignalRedError
import com.example.ui.theme.SolarGoldPrimary
import com.example.util.LocationHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampusMapScreen(
    viewModel: MainViewModel,
    onNavigateToSearch: () -> Unit,
    onNavigateToGlobe: () -> Unit,
    onNavigateToDriverLobby: () -> Unit,
    onNavigateToCaptain: () -> Unit
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val userProfile by viewModel.userProfile.collectAsState()
    val userRides by viewModel.userRides.collectAsState()
    val selectedVehicle by viewModel.selectedVehicle.collectAsState()
    val splitFare by viewModel.splitFare.collectAsState()
    val splitCount by viewModel.splitCount.collectAsState()
    val pickupInput by viewModel.pickupInput.collectAsState()
    val dropInput by viewModel.dropInput.collectAsState()
    val is3dViewEnabled by viewModel.is3dViewEnabled.collectAsState()
    val currentLatitude by viewModel.currentLatitude.collectAsState()
    val currentLongitude by viewModel.currentLongitude.collectAsState()
    val nearestLandmark by viewModel.nearestLandmark.collectAsState()
    val showBookingSheet by viewModel.showBookingSheet.collectAsState()
    val showSosDialog by viewModel.showSosDialog.collectAsState()
    val statusNotice by viewModel.statusNotice.collectAsState()

    var favoriteIds by remember { mutableStateOf(setOf("ion_digital", "thapar_univ")) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            viewModel.refreshLocation(context)
        }
    }

    LaunchedEffect(Unit) {
        if (LocationHelper.hasLocationPermission(context)) {
            viewModel.refreshLocation(context)
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    val activeRide = userRides.firstOrNull { it.status == "requested" || it.status == "accepted" || it.status == "in_route" }
    val bookingSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(statusNotice) {
        statusNotice?.let { notice ->
            snackbarHostState.showSnackbar(notice)
            viewModel.clearNotice()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Street Map Canvas Background (Rapido style vector roads with "Pickup Point" pin)
        CampusMapCanvas(
            modifier = Modifier.fillMaxSize(),
            activeRide = activeRide,
            is3dViewEnabled = is3dViewEnabled,
            userLatitude = currentLatitude,
            userLongitude = currentLongitude,
            nearestLandmark = nearestLandmark,
            onMapTapped = { landmark ->
                viewModel.setDropInput(landmark)
                viewModel.openBookingSheet(true)
            }
        )

        // 2. Top Floating Address Bar & Captain Mode Switcher
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(9999.dp),
                    color = Color.White,
                    shadowElevation = 6.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            if (LocationHelper.hasLocationPermission(context)) {
                                viewModel.refreshLocation(context)
                            } else {
                                locationPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        }
                        .testTag("floating_pickup_bar")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF059669))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = pickupInput.ifBlank { CampusData.DEFAULT_PICKUP_ADDRESS },
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = DeepSlateTertiary,
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh GPS",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Captain App Switch Button
                Surface(
                    shape = RoundedCornerShape(9999.dp),
                    color = SolarGoldPrimary,
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .clickable { onNavigateToCaptain() }
                        .testTag("open_captain_app_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ElectricScooter,
                            contentDescription = "Captain Mode",
                            tint = DeepSlateTertiary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Captain App",
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            color = DeepSlateTertiary
                        )
                    }
                }
            }

            // Interconnected alert banner if a ride has been requested and waiting for captain
            if (activeRide?.status == "requested") {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFEF3C7),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, SolarGoldPrimary),
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToCaptain() }
                        .testTag("ride_requested_captain_bridge_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(SolarGoldPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ElectricScooter,
                                contentDescription = null,
                                tint = DeepSlateTertiary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "🛵 Ride Broadcasted to Captains!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = DeepSlateTertiary
                            )
                            Text(
                                text = "Tap here to switch to Captain App & Accept",
                                fontSize = 11.sp,
                                color = Color(0xFF92400E)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(9999.dp),
                            color = DeepSlateTertiary
                        ) {
                            Text(
                                text = "Accept",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = SolarGoldPrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // 3. Floating Side Map Controls (3D Globe, 3D Campus, Recenter, SOS)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 3D Globe Button
            FloatingPillButton(
                icon = Icons.Default.Language,
                description = "3D Globe View",
                containerColor = Color(0xFF0F172A),
                contentColor = SolarGoldPrimary,
                onClick = onNavigateToGlobe
            )

            // 3D Campus Twin Toggle
            FloatingPillButton(
                icon = Icons.Default.ViewInAr,
                description = "3D Campus",
                containerColor = if (is3dViewEnabled) SolarGoldPrimary else Color.White,
                contentColor = DeepSlateTertiary,
                onClick = { viewModel.toggle3dView() }
            )

            // GPS Recenter Button
            FloatingPillButton(
                icon = Icons.Default.MyLocation,
                description = "Recenter",
                onClick = {
                    if (LocationHelper.hasLocationPermission(context)) {
                        viewModel.refreshLocation(context)
                    } else {
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                }
            )

            // SOS Emergency Button
            FloatingPillButton(
                icon = Icons.Default.Warning,
                description = "SOS",
                containerColor = SignalRedError,
                contentColor = Color.White,
                onClick = { viewModel.openSosDialog(true) }
            )
        }

        // 4. Bottom Draggable Search & Recent Destinations Sheet (Rapido Screenshot 1 & 2)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 72.dp) // Leave space for fixed bottom navigation bar
        ) {
            if (activeRide != null) {
                // Persistent Tracking HUD when a ride is in progress
                RideTrackingHud(
                    ride = activeRide,
                    onAdvanceRide = { viewModel.advanceRide(activeRide) },
                    onCancelRide = { viewModel.cancelRide(activeRide.id) },
                    onSosClicked = { viewModel.openSosDialog(true) },
                    onOpenCaptain = onNavigateToCaptain
                )
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("rapido_bottom_sheet_card"),
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        // Drag Handle
                        Box(
                            modifier = Modifier
                                .size(width = 38.dp, height = 4.dp)
                                .clip(RoundedCornerShape(9999.dp))
                                .background(Color(0xFFCBD5E1))
                                .align(Alignment.CenterHorizontally)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Rapido "Where are you going?" Search Bar with Yellow "Speak" Pill
                        Surface(
                            shape = RoundedCornerShape(9999.dp),
                            color = Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            shadowElevation = 2.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToSearch() }
                                .testTag("where_are_you_going_bar")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = DeepSlateTertiary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Where are you going?",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = DeepSlateTertiary
                                    )
                                }

                                // Yellow "Speak" Pill Button (Rapido signature!)
                                Surface(
                                    shape = RoundedCornerShape(9999.dp),
                                    color = SolarGoldPrimary,
                                    modifier = Modifier
                                        .clickable {
                                            Toast.makeText(context, "Voice search: Say destination (e.g. Thapar University)", Toast.LENGTH_SHORT).show()
                                        }
                                        .testTag("speak_pill_button")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Mic,
                                            contentDescription = "Speak",
                                            tint = DeepSlateTertiary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Speak",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = DeepSlateTertiary
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Recent Searches List (2-3 items)
                        CampusData.RecentSearches.take(2).forEach { landmark ->
                            val isFav = favoriteIds.contains(landmark.id)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.setDropInput(landmark.name)
                                        viewModel.openBookingSheet(true)
                                    }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = Color(0xFF64748B),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = landmark.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = DeepSlateTertiary,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = landmark.address,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF64748B),
                                        maxLines = 1
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        favoriteIds = if (isFav) favoriteIds - landmark.id else favoriteIds + landmark.id
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = "Favorite",
                                        tint = if (isFav) Color(0xFFEF4444) else Color(0xFF94A3B8),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 0.8.dp)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // "Popular Places" section header & chips (Bus Stand, Railway station)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Popular Places",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = DeepSlateTertiary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Horizontal Popular Places Chips
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PopularQuickChip("Bus Stand Patiala") {
                                viewModel.setDropInput("Bus Stand Patiala")
                                viewModel.openBookingSheet(true)
                            }
                            PopularQuickChip("Railway station") {
                                viewModel.setDropInput("Railway Station Patiala")
                                viewModel.openBookingSheet(true)
                            }
                            PopularQuickChip("Thapar University") {
                                viewModel.setDropInput("Thapar University")
                                viewModel.openBookingSheet(true)
                            }
                            PopularQuickChip("DBU Kalam Block") {
                                viewModel.setDropInput("DBU Central Library (Kalam Block)")
                                viewModel.openBookingSheet(true)
                            }
                        }
                    }
                }
            }
        }

        // 5. Snackbar Host
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 70.dp)
        )

        // 6. Ride Booking Modal Sheet
        if (showBookingSheet) {
            RideBookingSheet(
                sheetState = bookingSheetState,
                pickupLocation = pickupInput,
                dropLocation = dropInput,
                selectedVehicle = selectedVehicle,
                onVehicleSelected = { viewModel.setVehicle(it) },
                splitFare = splitFare,
                onSplitFareToggled = { viewModel.toggleSplitFare(it) },
                splitCount = splitCount,
                onSplitCountChanged = { viewModel.setSplitCount(it) },
                onConfirmBooking = { viewModel.confirmBooking() },
                onDismiss = { viewModel.openBookingSheet(false) }
            )
        }

        // 7. SOS Emergency Dialog
        if (showSosDialog) {
            SosEmergencyDialog(
                userLatitude = currentLatitude,
                userLongitude = currentLongitude,
                nearestLandmark = nearestLandmark,
                studentName = userProfile?.displayName ?: "DBU Student",
                collegeId = userProfile?.collegeId ?: "DBU/2024/ENG/108",
                onBroadcastDistress = { viewModel.broadcastSosDistress() },
                onDismiss = { viewModel.openSosDialog(false) }
            )
        }
    }
}

@Composable
private fun PopularQuickChip(
    label: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(9999.dp),
        color = Color(0xFFF8FAFC),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = DeepSlateTertiary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(SolarGoldPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = DeepSlateTertiary,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

@Composable
private fun FloatingPillButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    containerColor: Color = Color.White,
    contentColor: Color = DeepSlateTertiary,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(containerColor)
            .border(1.dp, Color(0xFFE2E8F0), CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = contentColor,
            modifier = Modifier.size(22.dp)
        )
    }
}
