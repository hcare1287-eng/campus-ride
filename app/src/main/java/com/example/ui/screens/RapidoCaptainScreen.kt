package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricScooter
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Ride
import com.example.ui.MainViewModel
import com.example.ui.theme.DeepSlateTertiary
import com.example.ui.theme.EcoGreenSecondary
import com.example.ui.theme.SignalRedError
import com.example.ui.theme.SolarGoldPrimary

private val CaptainGold = Color(0xFFFFC107)
private val CaptainGreen = Color(0xFF059669)
private val CaptainDark = Color(0xFF0F172A)
private val CaptainMuted = Color(0xFF475569)

@Composable
fun RapidoCaptainScreen(
    viewModel: MainViewModel,
    onSwitchToPassengerMode: () -> Unit
) {
    val context = LocalContext.current
    val userProfile by viewModel.userProfile.collectAsState()
    val availableRides by viewModel.availableRides.collectAsState()
    val driverAssignedRides by viewModel.driverAssignedRides.collectAsState()

    var isCaptainOnline by remember { mutableStateOf(true) }
    var dismissedRideIds by remember { mutableStateOf(setOf<String>()) }
    var enteredOtp by remember { mutableStateOf("") }
    var otpError by remember { mutableStateOf<String?>(null) }
    var hasArrivedAtPickup by remember { mutableStateOf(false) }

    // Active accepted/in_route ride assigned to this Captain
    val activeDriverRide = driverAssignedRides.firstOrNull { it.status == "accepted" || it.status == "in_route" }

    // Open incoming ride request to notify the Captain
    val incomingRide = if (isCaptainOnline && activeDriverRide == null) {
        availableRides.firstOrNull { it.status == "requested" && !dismissedRideIds.contains(it.id) }
    } else null

    // Vibrate when a new incoming ride arrives
    LaunchedEffect(incomingRide?.id) {
        if (incomingRide != null) {
            triggerCaptainAlertHaptic(context)
        }
    }

    Scaffold(
        topBar = {
            CaptainTopBar(
                isOnline = isCaptainOnline,
                onToggleOnline = { isCaptainOnline = it },
                onSwitchToPassenger = onSwitchToPassengerMode
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Captain Vehicle & Profile Card
                CaptainProfileSummaryCard(
                    driverName = userProfile?.displayName ?: "DBU Captain",
                    vehicleModel = userProfile?.vehicleModel?.ifBlank { "Campus EV Scooter" } ?: "Campus EV Scooter",
                    vehiclePlate = userProfile?.vehiclePlate?.ifBlank { "PB-23-EV-4412" } ?: "PB-23-EV-4412",
                    rating = userProfile?.rating ?: 4.95,
                    completedRides = userProfile?.ridesCount ?: 12L
                )

                // 2. Today's Earnings HUD
                CaptainEarningsCard(
                    todayEarnings = 180L + (userProfile?.ridesCount ?: 0L) * 20L,
                    todayRides = (userProfile?.ridesCount ?: 0L).coerceAtLeast(3L),
                    ecoCredits = userProfile?.ecoPoints ?: 250L
                )

                // 3. Active Accepted Ride HUD (If Captain is on a trip)
                if (activeDriverRide != null) {
                    CaptainActiveTripCard(
                        ride = activeDriverRide,
                        hasArrivedAtPickup = hasArrivedAtPickup,
                        onArrivedAtPickup = { hasArrivedAtPickup = true },
                        enteredOtp = enteredOtp,
                        onOtpChanged = {
                            if (it.length <= 4) enteredOtp = it
                            otpError = null
                        },
                        otpError = otpError,
                        onVerifyOtp = {
                            viewModel.driverVerifyOtp(
                                ride = activeDriverRide,
                                enteredOtp = enteredOtp,
                                onError = { err -> otpError = err },
                                onSuccess = {
                                    enteredOtp = ""
                                    otpError = null
                                    Toast.makeText(context, "OTP Verified! Trip is now In Route.", Toast.LENGTH_SHORT).show()
                                }
                            )
                        },
                        onCompleteTrip = {
                            viewModel.driverCompleteRide(activeDriverRide)
                            hasArrivedAtPickup = false
                            Toast.makeText(context, "Trip completed! ₹${activeDriverRide.fare} added to wallet 🌱", Toast.LENGTH_LONG).show()
                        },
                        onCallPassenger = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:9876543210")
                            }
                            context.startActivity(intent)
                        }
                    )
                }

                // 4. Live Radar & Discovery Status (when online and waiting)
                if (activeDriverRide == null && incomingRide == null) {
                    CaptainRadarCard(
                        isOnline = isCaptainOnline,
                        onGoOnline = { isCaptainOnline = true },
                        onSimulatePassengerRequest = {
                            viewModel.createDemoStudentRideRequest()
                            Toast.makeText(context, "Incoming ride request created! Watch notification below.", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                // 5. Open Requests Pool list
                if (activeDriverRide == null && availableRides.isNotEmpty()) {
                    Text(
                        text = "ALL CAMPUS RIDE REQUESTS (${availableRides.size})",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = CaptainDark,
                        letterSpacing = 1.sp
                    )

                    availableRides.forEach { ride ->
                        CaptainPoolItem(
                            ride = ride,
                            onAccept = {
                                viewModel.driverAcceptRide(ride)
                                Toast.makeText(context, "Ride accepted! Head to ${ride.pickupLocation}", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // ==========================================
            // INCOMING RIDE REQUEST FULL-SCREEN ALERT NOTIFICATION
            // ==========================================
            if (incomingRide != null) {
                IncomingRideAlertOverlay(
                    ride = incomingRide,
                    onAccept = {
                        viewModel.driverAcceptRide(incomingRide)
                        Toast.makeText(context, "Ride Accepted! Navigating to pickup...", Toast.LENGTH_SHORT).show()
                    },
                    onDecline = {
                        dismissedRideIds = dismissedRideIds + incomingRide.id
                    }
                )
            }
        }
    }
}

@Composable
private fun CaptainTopBar(
    isOnline: Boolean,
    onToggleOnline: (Boolean) -> Unit,
    onSwitchToPassenger: () -> Unit
) {
    Surface(
        color = Color.White,
        shadowElevation = 6.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.statusBarsPadding()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rapido Captain Brand
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(CaptainGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TwoWheeler,
                            contentDescription = "Captain Logo",
                            tint = CaptainDark,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "RAPIDO CAPTAIN",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = CaptainDark
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(if (isOnline) CaptainGreen else Color.Gray)
                            )
                        }
                        Text(
                            text = if (isOnline) "ON DUTY • DBU CAMPUS" else "OFF DUTY",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isOnline) CaptainGreen else Color.Gray,
                            fontSize = 10.sp
                        )
                    }
                }

                // Action Controls: Online Switch + Switch to Passenger Mode
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = isOnline,
                        onCheckedChange = onToggleOnline,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = CaptainGreen,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFFCBD5E1)
                        ),
                        modifier = Modifier.scale(0.85f).testTag("captain_duty_toggle")
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Mode Switcher to Passenger App
                    Surface(
                        shape = RoundedCornerShape(9999.dp),
                        color = Color(0xFFF1F5F9),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
                        modifier = Modifier
                            .clickable { onSwitchToPassenger() }
                            .testTag("switch_to_passenger_mode_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = null,
                                tint = CaptainDark,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Passenger App",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = CaptainDark,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
            HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 0.8.dp)
        }
    }
}

@Composable
private fun CaptainProfileSummaryCard(
    driverName: String,
    vehicleModel: String,
    vehiclePlate: String,
    rating: Double,
    completedRides: Long
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(CaptainGold.copy(alpha = 0.2f))
                    .border(1.5.dp, CaptainGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = CaptainDark,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = driverName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CaptainDark
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(9999.dp),
                        color = Color(0xFFFEF3C7)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(11.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "%.2f".format(rating),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
                Text(
                    text = "$vehicleModel • $vehiclePlate",
                    style = MaterialTheme.typography.bodySmall,
                    color = CaptainMuted
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF1F5F9)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$completedRides",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = CaptainDark
                    )
                    Text(
                        text = "Rides",
                        fontSize = 10.sp,
                        color = CaptainMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun CaptainEarningsCard(
    todayEarnings: Long,
    todayRides: Long,
    ecoCredits: Long
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CaptainDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "TODAY'S EARNINGS",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8),
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "₹$todayEarnings",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = CaptainGold,
                        fontSize = 28.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(9999.dp),
                    color = Color(0xFF1E293B)
                ) {
                    Text(
                        text = "Daily Payout: Active",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF38BDF8),
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Color(0xFF334155), thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CaptainGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$todayRides Completed Trips",
                        color = Color(0xFFE2E8F0),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🌱 $ecoCredits Eco Pts",
                        color = Color(0xFF6EE7B7),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun CaptainRadarCard(
    isOnline: Boolean,
    onGoOnline: () -> Unit,
    onSimulatePassengerRequest: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "radarPulse")
    val radarScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "radarScale"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isOnline) {
                // Pulsing Radar Ring
                Box(
                    modifier = Modifier.size(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .scale(radarScale)
                            .clip(CircleShape)
                            .background(CaptainGreen.copy(alpha = 0.15f))
                    )
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(CaptainGreen.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "Radar",
                            tint = CaptainGreen,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Searching for Nearby Student Rides",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CaptainDark
                )
                Text(
                    text = "Stationed near DBU Kalam Library, Hostels & GT Road. Any ride booked by a student will trigger an instant alert here.",
                    style = MaterialTheme.typography.bodySmall,
                    color = CaptainMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Solo Test Button to trigger a request
                OutlinedButton(
                    onClick = onSimulatePassengerRequest,
                    shape = RoundedCornerShape(9999.dp),
                    modifier = Modifier.testTag("captain_simulate_passenger_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Solo Test: Trigger Demo Student Request", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Icon(
                    imageVector = Icons.Default.PowerSettingsNew,
                    contentDescription = "Offline",
                    tint = Color.Gray,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "You are Currently Offline",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CaptainDark
                )
                Text(
                    text = "Toggle duty switch at the top to start receiving student ride alerts.",
                    style = MaterialTheme.typography.bodySmall,
                    color = CaptainMuted,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onGoOnline,
                    shape = RoundedCornerShape(9999.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CaptainGreen)
                ) {
                    Text("Go Online Now", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun CaptainActiveTripCard(
    ride: Ride,
    hasArrivedAtPickup: Boolean,
    onArrivedAtPickup: () -> Unit,
    enteredOtp: String,
    onOtpChanged: (String) -> Unit,
    otpError: String?,
    onVerifyOtp: () -> Unit,
    onCompleteTrip: () -> Unit,
    onCallPassenger: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("captain_active_trip_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
        border = androidx.compose.foundation.BorderStroke(2.dp, CaptainGold),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Status Badge & Fare
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(9999.dp),
                    color = if (ride.status == "accepted") CaptainGold else CaptainGreen
                ) {
                    Text(
                        text = if (ride.status == "accepted") {
                            if (hasArrivedAtPickup) "ARRIVED AT PICKUP • ENTER OTP" else "PICKUP IN PROGRESS"
                        } else "TRIP IN ROUTE TO DESTINATION",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (ride.status == "accepted") CaptainDark else Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                    )
                }

                Text(
                    text = "₹${ride.fare}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = CaptainDark,
                    fontSize = 24.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Passenger Name & Call Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Rider: ${ride.riderName}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CaptainDark
                    )
                    Text(
                        text = if (ride.splitFare) "Fare Split (${ride.splitCount} classmates)" else "Single Rider",
                        style = MaterialTheme.typography.bodySmall,
                        color = CaptainMuted
                    )
                }

                IconButton(
                    onClick = onCallPassenger,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(CaptainGreen)
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Call Rider",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFFDE68A), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Pickup & Drop
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(CaptainGreen)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Pickup: ${ride.pickupLocation}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = CaptainDark
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEA580C))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Drop: ${ride.dropLocation}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = CaptainDark
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Phase 1: Arrived at Pickup button
            if (ride.status == "accepted" && !hasArrivedAtPickup) {
                Button(
                    onClick = onArrivedAtPickup,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("captain_arrived_pickup_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CaptainDark)
                ) {
                    Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("I Have Arrived at Pickup Point", fontWeight = FontWeight.Bold)
                }
            }

            // Phase 2: Enter Rider OTP to start ride
            if (ride.status == "accepted" && hasArrivedAtPickup) {
                Text(
                    text = "Ask Rider for 4-Digit OTP to Start Trip:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = CaptainDark
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = enteredOtp,
                        onValueChange = onOtpChanged,
                        placeholder = { Text("4-digit OTP", color = Color.Gray) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("captain_otp_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = CaptainDark,
                            unfocusedTextColor = CaptainDark,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    Button(
                        onClick = onVerifyOtp,
                        modifier = Modifier
                            .height(52.dp)
                            .testTag("captain_verify_otp_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CaptainGreen)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Verify & Start", fontWeight = FontWeight.Bold)
                    }
                }

                if (otpError != null) {
                    Text(
                        text = otpError,
                        color = SignalRedError,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Phase 3: Trip In Route -> Complete Trip
            if (ride.status == "in_route") {
                Button(
                    onClick = onCompleteTrip,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("captain_complete_ride_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CaptainGreen)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Complete Trip & Collect ₹${ride.fare}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun IncomingRideAlertOverlay(
    ride: Ride,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    // 15 second countdown animation for accepting
    var timeLeftSeconds by remember { mutableStateOf(15) }
    LaunchedEffect(ride.id) {
        while (timeLeftSeconds > 0) {
            kotlinx.coroutines.delay(1000L)
            timeLeftSeconds--
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.65f))
            .clickable(enabled = false) {},
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("incoming_ride_alert_sheet"),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header with Flash Badge & Timer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(CaptainGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = CaptainDark,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "NEW RIDE REQUEST!",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = CaptainDark
                            )
                            Text(
                                text = "Campus EV Bike Taxi",
                                fontSize = 11.sp,
                                color = CaptainMuted
                            )
                        }
                    }

                    // Countdown Ring
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFEF3C7))
                            .border(2.dp, CaptainGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${timeLeftSeconds}s",
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            color = Color(0xFF92400E)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { timeLeftSeconds / 15f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(9999.dp)),
                    color = CaptainGold,
                    trackColor = Color(0xFFE2E8F0)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Fare & Distance
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF8FAFC))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "EARNING (CASH/UPI)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = CaptainMuted
                        )
                        Text(
                            text = "₹${ride.fare}",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            color = CaptainDark
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "0.8 km • 3 mins pickup",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CaptainGreen
                        )
                        Text(
                            text = "+50 Eco Points 🌱",
                            fontSize = 11.sp,
                            color = Color(0xFF047857)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Rider & Route Details
                Text(
                    text = "Rider: ${ride.riderName} • Verified Student",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = CaptainDark
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(CaptainGreen)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "From: ${ride.pickupLocation}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CaptainDark,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEA580C))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "To: ${ride.dropLocation}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CaptainDark,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Actions: Decline vs Accept
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDecline,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("captain_decline_ride_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Decline", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onAccept,
                        modifier = Modifier
                            .weight(2f)
                            .height(52.dp)
                            .testTag("captain_accept_ride_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CaptainGold,
                            contentColor = CaptainDark
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ACCEPT RIDE", fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun CaptainPoolItem(
    ride: Ride,
    onAccept: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = ride.riderName,
                        fontWeight = FontWeight.Bold,
                        color = CaptainDark,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ₹${ride.fare}",
                        fontWeight = FontWeight.Black,
                        color = CaptainGreen,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${ride.pickupLocation} → ${ride.dropLocation}",
                    fontSize = 12.sp,
                    color = CaptainMuted,
                    maxLines = 1
                )
            }

            Button(
                onClick = onAccept,
                modifier = Modifier
                    .height(38.dp)
                    .testTag("pool_accept_${ride.id}"),
                shape = RoundedCornerShape(9999.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CaptainGold, contentColor = CaptainDark)
            ) {
                Text("Accept", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

private fun triggerCaptainAlertHaptic(context: Context) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator?.vibrate(
                VibrationEffect.createWaveform(longArrayOf(0, 150, 100, 250), -1)
            )
        } else {
            @Suppress("DEPRECATION")
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            @Suppress("DEPRECATION")
            vibrator?.vibrate(300L)
        }
    } catch (_: Exception) {}
}
