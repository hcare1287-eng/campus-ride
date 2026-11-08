package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.ElectricScooter
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Ride
import com.example.ui.theme.DeepSlateTertiary
import com.example.ui.theme.EcoGreenSecondary
import com.example.ui.theme.SignalRedError
import com.example.ui.theme.SolarGoldPrimary

@Composable
fun RideTrackingHud(
    ride: Ride,
    onAdvanceRide: () -> Unit,
    onCancelRide: () -> Unit,
    onSosClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulseHud")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hudPulseAlpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("ride_tracking_hud"),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Drag Handle Pill
            Box(
                modifier = Modifier
                    .size(width = 44.dp, height = 4.dp)
                    .clip(RoundedCornerShape(9999.dp))
                    .background(Color(0xFFCBD5E1))
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Live State Header Row with Pulsing Chip & SOS Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status Tracking Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9999.dp))
                        .background(Color(0xFFF1F5F9))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(9999.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(SolarGoldPrimary.copy(alpha = pulseAlpha))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (ride.status) {
                            "requested" -> "Finding Peer Driver..."
                            "accepted" -> "Driver ${ride.etaMinutes} mins away"
                            "in_route" -> "In Route to ${ride.dropLocation}"
                            "completed" -> "Ride Completed!"
                            else -> "Trip Active"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = DeepSlateTertiary
                    )
                }

                // Instant Emergency SOS Pill
                Button(
                    onClick = onSosClicked,
                    shape = RoundedCornerShape(9999.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SignalRedError,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("sos_emergency_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Emergency SOS",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "SOS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dual-Color Progress Track with Bike Icon Indicator
            Column(modifier = Modifier.fillMaxWidth()) {
                val progressFraction = when (ride.status) {
                    "requested" -> 0.2f
                    "accepted" -> 0.5f
                    "in_route" -> 0.85f
                    "completed" -> 1.0f
                    else -> 0.1f
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(9999.dp))
                        .background(Color(0xFFE2E8F0))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progressFraction)
                            .height(8.dp)
                            .clip(RoundedCornerShape(9999.dp))
                            .background(SolarGoldPrimary)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = ride.pickupLocation,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                    Text(
                        text = ride.dropLocation,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = DeepSlateTertiary,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // OTP Box & Driver Matching Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Driver Details
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(SolarGoldPrimary.copy(alpha = 0.2f))
                            .border(2.dp, SolarGoldPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ElectricScooter,
                            contentDescription = "Driver Avatar",
                            tint = DeepSlateTertiary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = ride.driverName ?: "Campus Peer Driver",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = DeepSlateTertiary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            // Verified Student Badge
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(9999.dp))
                                    .background(EcoGreenSecondary.copy(alpha = 0.12f))
                                    .border(1.dp, EcoGreenSecondary, RoundedCornerShape(9999.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = "Verified",
                                    tint = EcoGreenSecondary,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "Verified",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = EcoGreenSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }
                        Text(
                            text = "${ride.vehiclePlate ?: "Campus EV-04"} • 4.9 ★",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // 4-Digit Mono-spaced High-Contrast OTP Box
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "START OTP",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        ride.otp.forEach { digit ->
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(DeepSlateTertiary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = digit.toString(),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = SolarGoldPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Meta tags: Fare split & Eco CO2 saved
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Fare Pill
                Surface(
                    shape = RoundedCornerShape(9999.dp),
                    color = Color(0xFFF1F5F9),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1))
                ) {
                    Text(
                        text = if (ride.splitFare) "₹${ride.fare} Split Fare (${ride.splitCount} peers)" else "₹${ride.fare} Standard Fare",
                        style = MaterialTheme.typography.labelSmall,
                        color = DeepSlateTertiary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                // Eco CO2 Pill
                Surface(
                    shape = RoundedCornerShape(9999.dp),
                    color = EcoGreenSecondary.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EcoGreenSecondary.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "🌱 ${ride.ecoSavedGrams}g CO2 Saved",
                        style = MaterialTheme.typography.labelSmall,
                        color = EcoGreenSecondary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                // Helmet Verified
                Surface(
                    shape = RoundedCornerShape(9999.dp),
                    color = Color(0xFFE0F2FE),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8))
                ) {
                    Text(
                        text = "🛡️ Helmet Verified",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF0369A1),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onCancelRide,
                    shape = RoundedCornerShape(9999.dp),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Button(
                    onClick = onAdvanceRide,
                    shape = RoundedCornerShape(9999.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SolarGoldPrimary,
                        contentColor = DeepSlateTertiary
                    ),
                    modifier = Modifier
                        .weight(2f)
                        .height(48.dp)
                        .testTag("advance_ride_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = when (ride.status) {
                                "requested" -> Icons.AutoMirrored.Filled.DirectionsRun
                                "accepted" -> Icons.Default.PlayArrow
                                else -> Icons.Default.CheckCircle
                            },
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when (ride.status) {
                                "requested" -> "Simulate Match"
                                "accepted" -> "Verify OTP & Start"
                                "in_route" -> "Complete Trip"
                                else -> "Completed"
                            },
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
