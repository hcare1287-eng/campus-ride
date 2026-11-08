package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ElectricBike
import androidx.compose.material.icons.filled.ElectricScooter
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.PedalBike
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CampusData
import com.example.data.model.VehicleOption
import com.example.ui.theme.DeepSlateTertiary
import com.example.ui.theme.EcoGreenSecondary
import com.example.ui.theme.SolarGoldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RideBookingSheet(
    sheetState: SheetState,
    pickupLocation: String,
    dropLocation: String,
    onPickupChanged: (String) -> Unit,
    onDropChanged: (String) -> Unit,
    selectedVehicle: VehicleOption,
    onVehicleSelected: (VehicleOption) -> Unit,
    splitFare: Boolean,
    onSplitFareToggled: (Boolean) -> Unit,
    splitCount: Int,
    onSplitCountChanged: (Int) -> Unit,
    onConfirmBooking: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Book Campus Peer Ride",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = DeepSlateTertiary
            )
            Text(
                text = "Fast, eco-friendly transit between campus blocks",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Pickup & Drop Inputs
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Pickup
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "Pickup",
                            tint = SolarGoldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        OutlinedTextField(
                            value = pickupLocation,
                            onValueChange = onPickupChanged,
                            placeholder = { Text("Pickup hostel / landmark") },
                            modifier = Modifier.fillMaxWidth().testTag("pickup_input"),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFFE2E8F0))
                            .padding(vertical = 4.dp)
                    )

                    // Dropoff
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Dropoff",
                            tint = EcoGreenSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        OutlinedTextField(
                            value = dropLocation,
                            onValueChange = onDropChanged,
                            placeholder = { Text("Where to on campus?") },
                            modifier = Modifier.fillMaxWidth().testTag("drop_input"),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Vehicle Selection
            Text(
                text = "Choose Vehicle",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = DeepSlateTertiary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CampusData.Vehicles.forEach { vehicle ->
                    val isSelected = vehicle.name == selectedVehicle.name
                    val price = if (splitFare) (vehicle.baseFare / splitCount).coerceAtLeast(5L) else vehicle.baseFare

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onVehicleSelected(vehicle) }
                            .testTag("vehicle_${vehicle.name.lowercase().replace(" ", "_")}"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) SolarGoldPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) SolarGoldPrimary else Color(0xFFE2E8F0)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) SolarGoldPrimary else Color(0xFFF1F5F9)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (vehicle.vehicleIcon) {
                                            "bike" -> Icons.Default.PedalBike
                                            "ebike" -> Icons.Default.ElectricBike
                                            else -> Icons.Default.ElectricScooter
                                        },
                                        contentDescription = vehicle.name,
                                        tint = if (isSelected) DeepSlateTertiary else DeepSlateTertiary.copy(alpha = 0.7f),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = vehicle.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = DeepSlateTertiary
                                    )
                                    Text(
                                        text = "${vehicle.etaMinutes} mins away • ${vehicle.co2Saved}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = EcoGreenSecondary
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "₹$price",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepSlateTertiary
                                )
                                if (splitFare) {
                                    Text(
                                        text = "Split for $splitCount",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = EcoGreenSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Split Fare Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Group,
                                contentDescription = "Split Fare",
                                tint = EcoGreenSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Split Fare with Peer",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepSlateTertiary
                                )
                                Text(
                                    text = "Save money and reduce campus emissions",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = splitFare,
                            onCheckedChange = onSplitFareToggled,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = EcoGreenSecondary
                            ),
                            modifier = Modifier.testTag("split_fare_switch")
                        )
                    }

                    AnimatedVisibility(visible = splitFare) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Number of students sharing:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = DeepSlateTertiary
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { onSplitCountChanged(splitCount - 1) },
                                    enabled = splitCount > 2
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Decrease")
                                }
                                Text(
                                    text = "$splitCount peers",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepSlateTertiary,
                                    modifier = Modifier.padding(horizontal = 6.dp)
                                )
                                IconButton(
                                    onClick = { onSplitCountChanged(splitCount + 1) },
                                    enabled = splitCount < 5
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Increase")
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Primary Booking Button
            Button(
                onClick = onConfirmBooking,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("confirm_booking_button"),
                shape = RoundedCornerShape(9999.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SolarGoldPrimary,
                    contentColor = DeepSlateTertiary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 1.dp)
            ) {
                Text(
                    text = "Request Campus Pickup • ₹${if (splitFare) (selectedVehicle.baseFare / splitCount).coerceAtLeast(5L) else selectedVehicle.baseFare}",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
