package com.example.ui.components

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DriveEta
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.UserProfile
import com.example.ui.theme.DeepSlateTertiary
import com.example.ui.theme.EcoGreenSecondary
import com.example.ui.theme.SignalRedError
import com.example.ui.theme.SolarGoldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentProfileSheet(
    sheetState: SheetState,
    profile: UserProfile?,
    isDriverMode: Boolean,
    onToggleDriverMode: () -> Unit,
    onUpdateRollNumber: (String) -> Unit,
    onSignOut: () -> Unit,
    onDismiss: () -> Unit
) {
    var isEditingRoll by remember { mutableStateOf(false) }
    var rollInput by remember(profile) { mutableStateOf(profile?.collegeId ?: "IIT-2024-EV-88") }

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
            // Header Profile Card
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(SolarGoldPrimary.copy(alpha = 0.2f))
                        .border(2.dp, SolarGoldPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Avatar",
                        tint = DeepSlateTertiary,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = profile?.displayName ?: "Campus Student",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = DeepSlateTertiary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verified",
                            tint = EcoGreenSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Verified Badge Pill
                    Row(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .clip(RoundedCornerShape(9999.dp))
                            .background(EcoGreenSecondary.copy(alpha = 0.12f))
                            .border(1.dp, EcoGreenSecondary.copy(alpha = 0.3f), RoundedCornerShape(9999.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = EcoGreenSecondary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = profile?.collegeId ?: "IIT-2024-EV-88",
                            style = MaterialTheme.typography.labelSmall,
                            color = EcoGreenSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Eco Vitality & Stats Matrix
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Eco CO2 Metric
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = EcoGreenSecondary.copy(alpha = 0.08f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EcoGreenSecondary.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Icon(
                            imageVector = Icons.Default.Eco,
                            contentDescription = "Eco",
                            tint = EcoGreenSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${(profile?.ecoPoints ?: 420L) * 20 / 1000} kg",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = DeepSlateTertiary
                        )
                        Text(
                            text = "CO2 Saved",
                            style = MaterialTheme.typography.bodySmall,
                            color = EcoGreenSecondary
                        )
                    }
                }

                // Trips Metric
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SolarGoldPrimary.copy(alpha = 0.08f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SolarGoldPrimary.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Icon(
                            imageVector = Icons.Default.DriveEta,
                            contentDescription = "Trips",
                            tint = SolarGoldPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${profile?.ridesCount ?: 8L}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = DeepSlateTertiary
                        )
                        Text(
                            text = "Clean Trips",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Rating Metric
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = SolarGoldPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${profile?.rating ?: 4.9}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = DeepSlateTertiary
                        )
                        Text(
                            text = "Peer Rating",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Peer Driver Mode Switch Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Peer Driver Mode",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DeepSlateTertiary
                        )
                        Text(
                            text = "Heading to class? Offer a seat on your bike/scooter",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Switch(
                        checked = isDriverMode,
                        onCheckedChange = { onToggleDriverMode() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = SolarGoldPrimary
                        ),
                        modifier = Modifier.testTag("driver_mode_switch")
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Student ID Roll Number Edit Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Student ID Card Verification",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DeepSlateTertiary
                        )
                        IconButton(onClick = { isEditingRoll = !isEditingRoll }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit ID")
                        }
                    }

                    if (isEditingRoll) {
                        OutlinedTextField(
                            value = rollInput,
                            onValueChange = { rollInput = it },
                            label = { Text("College Roll / ID Number") },
                            modifier = Modifier.fillMaxWidth().testTag("student_id_input"),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                onUpdateRollNumber(rollInput)
                                isEditingRoll = false
                            },
                            shape = RoundedCornerShape(9999.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DeepSlateTertiary),
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Save Roll Number")
                        }
                    } else {
                        Text(
                            text = "Verified Roll: ${profile?.collegeId ?: "IIT-2024-EV-88"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Sign Out Button
            OutlinedButton(
                onClick = onSignOut,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("sign_out_button"),
                shape = RoundedCornerShape(9999.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SignalRedError)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Sign Out",
                    tint = SignalRedError,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sign Out of Campus Velocity",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
