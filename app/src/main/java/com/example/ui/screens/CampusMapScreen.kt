package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ElectricScooter
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import com.example.auth.AuthManager
import com.example.data.model.CampusData
import com.example.data.model.CampusShortcut
import com.example.ui.MainViewModel
import com.example.ui.components.CampusMapCanvas
import com.example.ui.components.RideBookingSheet
import com.example.ui.components.RideTrackingHud
import com.example.ui.components.SosEmergencyDialog
import com.example.ui.components.StudentProfileSheet
import com.example.ui.theme.DeepSlateTertiary
import com.example.ui.theme.EcoGreenSecondary
import com.example.ui.theme.SignalRedError
import com.example.ui.theme.SolarGoldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampusMapScreen(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    val snackbarHostState = remember { SnackbarHostState() }

    val userProfile by viewModel.userProfile.collectAsState()
    val userRides by viewModel.userRides.collectAsState()
    val selectedVehicle by viewModel.selectedVehicle.collectAsState()
    val splitFare by viewModel.splitFare.collectAsState()
    val splitCount by viewModel.splitCount.collectAsState()
    val pickupInput by viewModel.pickupInput.collectAsState()
    val dropInput by viewModel.dropInput.collectAsState()
    val isDriverMode by viewModel.isDriverMode.collectAsState()
    val showBookingSheet by viewModel.showBookingSheet.collectAsState()
    val showProfileSheet by viewModel.showProfileSheet.collectAsState()
    val showSosDialog by viewModel.showSosDialog.collectAsState()
    val statusNotice by viewModel.statusNotice.collectAsState()

    // Find the currently active ride (requested, accepted, in_route)
    val activeRide = userRides.firstOrNull { it.status == "requested" || it.status == "accepted" || it.status == "in_route" }

    val bookingSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val profileSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(statusNotice) {
        statusNotice?.let { notice ->
            snackbarHostState.showSnackbar(notice)
            viewModel.clearNotice()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Campus Vector Map Canvas Background
        CampusMapCanvas(
            modifier = Modifier.fillMaxSize(),
            activeRide = activeRide,
            onMapTapped = { landmark ->
                viewModel.setDropInput(landmark)
                viewModel.openBookingSheet(true)
            }
        )

        // 2. Top Campus Glassmorphic Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile Avatar Trigger
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { viewModel.openProfileSheet(true) }
                            .testTag("profile_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(SolarGoldPrimary.copy(alpha = 0.2f))
                                .border(1.5.dp, SolarGoldPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = DeepSlateTertiary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "VELOCITY",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = DeepSlateTertiary
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(EcoGreenSecondary)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isDriverMode) "Driver Active" else "28 EV Bikes Live",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = EcoGreenSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // Mode Switcher Pill: Rider vs Driver
                    Surface(
                        shape = RoundedCornerShape(9999.dp),
                        color = Color(0xFFF1F5F9),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
                        modifier = Modifier
                            .clickable { viewModel.toggleDriverMode() }
                            .testTag("mode_toggle_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = "Switch Mode",
                                tint = DeepSlateTertiary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isDriverMode) "Offering Ride" else "Need Ride",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = DeepSlateTertiary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Campus Destination Search Bar (Floating Level 1)
            AnimatedVisibility(visible = activeRide == null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.openBookingSheet(true) }
                        .testTag("search_destination_bar"),
                    shape = RoundedCornerShape(9999.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = SolarGoldPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Where to on campus? (Hostels, Depts...)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Horizontal Hostel-to-Dept Shortcuts
            AnimatedVisibility(visible = activeRide == null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CampusData.Shortcuts.forEach { shortcut ->
                        ShortcutChip(
                            shortcut = shortcut,
                            onClick = { viewModel.selectShortcut(shortcut) }
                        )
                    }
                }
            }
        }

        // 3. Floating Map Controls (Right Side)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Recenter Button
            FloatingMapButton(
                icon = Icons.Default.MyLocation,
                description = "Recenter",
                onClick = {
                    Toast.makeText(context, "Centered on your campus hostel location", Toast.LENGTH_SHORT).show()
                }
            )

            // Campus Layers Button
            FloatingMapButton(
                icon = Icons.Default.Layers,
                description = "Layers",
                onClick = {
                    Toast.makeText(context, "Showing 28 active EV bikes & campus routes", Toast.LENGTH_SHORT).show()
                }
            )

            // Emergency SOS Button (Instant Floating)
            FloatingMapButton(
                icon = Icons.Default.Warning,
                description = "SOS",
                containerColor = SignalRedError,
                contentColor = Color.White,
                onClick = { viewModel.openSosDialog(true) }
            )
        }

        // 4. Bottom Section: Persistent Tracking HUD (if ride active) OR Floating Ride Action Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            if (activeRide != null) {
                RideTrackingHud(
                    ride = activeRide,
                    onAdvanceRide = { viewModel.advanceRide(activeRide) },
                    onCancelRide = { viewModel.cancelRide(activeRide.id) },
                    onSosClicked = { viewModel.openSosDialog(true) }
                )
            } else {
                // Primary Quick Booking FAB
                Button(
                    onClick = { viewModel.openBookingSheet(true) },
                    shape = RoundedCornerShape(9999.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SolarGoldPrimary,
                        contentColor = DeepSlateTertiary
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                        .height(56.dp)
                        .testTag("book_campus_ride_fab")
                ) {
                    Icon(
                        imageVector = Icons.Default.ElectricScooter,
                        contentDescription = "Ride",
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isDriverMode) "Post Classmate Ride Offer" else "Request Quick Campus Pickup",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }

        // 5. Snackbar Host for Notifications
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 70.dp)
        )

        // 6. Modal Sheets & Dialogs
        if (showBookingSheet) {
            RideBookingSheet(
                sheetState = bookingSheetState,
                pickupLocation = pickupInput,
                dropLocation = dropInput,
                onPickupChanged = { viewModel.setPickupInput(it) },
                onDropChanged = { viewModel.setDropInput(it) },
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

        if (showProfileSheet) {
            StudentProfileSheet(
                sheetState = profileSheetState,
                profile = userProfile,
                isDriverMode = isDriverMode,
                onToggleDriverMode = { viewModel.toggleDriverMode() },
                onUpdateRollNumber = { viewModel.updateStudentId(it) },
                onSignOut = {
                    AuthManager.signOut(
                        credentialManager = credentialManager,
                        scope = scope,
                        onSignOutComplete = {
                            viewModel.openProfileSheet(false)
                        }
                    )
                },
                onDismiss = { viewModel.openProfileSheet(false) }
            )
        }

        if (showSosDialog) {
            SosEmergencyDialog(
                onDismiss = { viewModel.openSosDialog(false) }
            )
        }
    }
}

@Composable
private fun ShortcutChip(
    shortcut: CampusShortcut,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${shortcut.pickup.take(12)}... → ${shortcut.dropoff.take(12)}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = DeepSlateTertiary
                )
                Text(
                    text = "₹${shortcut.defaultFare} • ${shortcut.bikeTimeMinutes}m EV vs ${shortcut.walkTimeMinutes}m walk",
                    style = MaterialTheme.typography.bodySmall,
                    color = EcoGreenSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun FloatingMapButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    containerColor: Color = Color.White.copy(alpha = 0.9f),
    contentColor: Color = DeepSlateTertiary,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
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
