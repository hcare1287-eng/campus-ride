package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import com.example.auth.AuthManager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.ElectricScooter
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CampusData
import com.example.data.model.UserProfile
import com.example.ui.theme.DeepSlateTertiary
import com.example.ui.theme.EcoGreenSecondary
import com.example.ui.theme.SolarGoldPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupSetupScreen(
    initialName: String,
    initialEmail: String,
    onCompleteSetup: (UserProfile) -> Unit
) {
    val context = LocalContext.current
    var selectedRole by remember { mutableStateOf("passenger") } // "passenger" or "driver"
    var nameInput by remember { mutableStateOf(initialName.ifBlank { "DBU Student" }) }
    var rollInput by remember { mutableStateOf("DBU/2024/ENG/108") }
    var phoneInput by remember { mutableStateOf(AuthManager.lastVerifiedPhoneNumber ?: "9876543210") }
    var selectedFaculty by remember { mutableStateOf(CampusData.DbuFaculties.first()) }
    var selectedResidency by remember { mutableStateOf(CampusData.DbuResidencies.first()) }
    var facultyExpanded by remember { mutableStateOf(false) }
    var residencyExpanded by remember { mutableStateOf(false) }

    // Driver specific fields
    var vehicleType by remember { mutableStateOf("Campus EV Scooter") }
    var vehicleModel by remember { mutableStateOf("Ather 450X (Solar Gold)") }
    var vehiclePlate by remember { mutableStateOf("PB-23-EV-4412") }
    var helmetPledge by remember { mutableStateOf(true) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8FAFC)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            // 3D Campus Header Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_dbu_campus_3d),
                    contentDescription = "Desh Bhagat University 3D Campus",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xFF0F172A).copy(alpha = 0.85f))
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(EcoGreenSecondary)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "DESH BHAGAT UNIVERSITY",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF6EE7B7),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "Campus Profile & Mobility Setup",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "STEP 1: SELECT YOUR CAMPUS ROLE",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = DeepSlateTertiary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Role Selection Cards (User / Passenger vs Rider / Driver)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Role A: Passenger
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedRole = "passenger" }
                            .testTag("role_passenger_button"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedRole == "passenger") Color(0xFFFEF3C7) else Color.White
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (selectedRole == "passenger") 2.dp else 1.dp,
                            color = if (selectedRole == "passenger") SolarGoldPrimary else Color(0xFFCBD5E1)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(if (selectedRole == "passenger") SolarGoldPrimary else Color(0xFFF1F5F9)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Passenger",
                                    tint = DeepSlateTertiary,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Passenger",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = DeepSlateTertiary
                            )
                            Text(
                                text = "Need rides across DBU hostels & labs",
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                color = Color(0xFF475569),
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Role B: Peer Rider / Driver
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedRole = "driver" }
                            .testTag("role_driver_button"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedRole == "driver") Color(0xFFD1FAE5) else Color.White
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (selectedRole == "driver") 2.dp else 1.dp,
                            color = if (selectedRole == "driver") EcoGreenSecondary else Color(0xFFCBD5E1)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(if (selectedRole == "driver") EcoGreenSecondary else Color(0xFFF1F5F9)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ElectricScooter,
                                    contentDescription = "Driver",
                                    tint = if (selectedRole == "driver") Color.White else DeepSlateTertiary,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Peer Driver",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = DeepSlateTertiary
                            )
                            Text(
                                text = "Offer rides on your EV scooter / bike",
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                color = Color(0xFF475569),
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // STEP 2: University & Student Details
                Text(
                    text = "STEP 2: STUDENT DETAILS",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = DeepSlateTertiary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // University Name (fixed badge)
                        OutlinedTextField(
                            value = "Desh Bhagat University (DBU)",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("University Campus", fontWeight = FontWeight.SemiBold) },
                            leadingIcon = {
                                Icon(Icons.Default.School, contentDescription = null, tint = DeepSlateTertiary)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = DeepSlateTertiary,
                                unfocusedTextColor = DeepSlateTertiary,
                                focusedBorderColor = Color(0xFF94A3B8),
                                unfocusedBorderColor = Color(0xFFE2E8F0)
                            )
                        )

                        // Full Name
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Full Name", fontWeight = FontWeight.SemiBold) },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = DeepSlateTertiary)
                            },
                            modifier = Modifier.fillMaxWidth().testTag("signup_name_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = DeepSlateTertiary,
                                unfocusedTextColor = DeepSlateTertiary
                            )
                        )

                        // Student Roll / Reg No.
                        OutlinedTextField(
                            value = rollInput,
                            onValueChange = { rollInput = it },
                            label = { Text("DBU Student Roll / Reg Number", fontWeight = FontWeight.SemiBold) },
                            leadingIcon = {
                                Icon(Icons.Default.Badge, contentDescription = null, tint = DeepSlateTertiary)
                            },
                            modifier = Modifier.fillMaxWidth().testTag("signup_roll_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = DeepSlateTertiary,
                                unfocusedTextColor = DeepSlateTertiary
                            )
                        )

                        // Mobile Contact
                        OutlinedTextField(
                            value = phoneInput,
                            onValueChange = { phoneInput = it },
                            label = { Text("Mobile Contact Number", fontWeight = FontWeight.SemiBold) },
                            leadingIcon = {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = DeepSlateTertiary)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth().testTag("signup_phone_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = DeepSlateTertiary,
                                unfocusedTextColor = DeepSlateTertiary
                            )
                        )

                        // Faculty Dropdown
                        ExposedDropdownMenuBox(
                            expanded = facultyExpanded,
                            onExpandedChange = { facultyExpanded = !facultyExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = selectedFaculty,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Faculty / Department", fontWeight = FontWeight.SemiBold) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = facultyExpanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = DeepSlateTertiary,
                                    unfocusedTextColor = DeepSlateTertiary
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = facultyExpanded,
                                onDismissRequest = { facultyExpanded = false }
                            ) {
                                CampusData.DbuFaculties.forEach { faculty ->
                                    DropdownMenuItem(
                                        text = { Text(faculty, color = DeepSlateTertiary) },
                                        onClick = {
                                            selectedFaculty = faculty
                                            facultyExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Residency / Hostel Dropdown
                        ExposedDropdownMenuBox(
                            expanded = residencyExpanded,
                            onExpandedChange = { residencyExpanded = !residencyExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = selectedResidency,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Hostel / Residence", fontWeight = FontWeight.SemiBold) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = residencyExpanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = DeepSlateTertiary,
                                    unfocusedTextColor = DeepSlateTertiary
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = residencyExpanded,
                                onDismissRequest = { residencyExpanded = false }
                            ) {
                                CampusData.DbuResidencies.forEach { res ->
                                    DropdownMenuItem(
                                        text = { Text(res, color = DeepSlateTertiary) },
                                        onClick = {
                                            selectedResidency = res
                                            residencyExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // STEP 3: Rider / Driver Vehicle Setup (Conditional)
                AnimatedVisibility(visible = selectedRole == "driver") {
                    Column(modifier = Modifier.padding(top = 24.dp)) {
                        Text(
                            text = "STEP 3: DRIVER & VEHICLE VERIFICATION",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = DeepSlateTertiary,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EcoGreenSecondary.copy(alpha = 0.5f)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.TwoWheeler,
                                        contentDescription = null,
                                        tint = EcoGreenSecondary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Registered Campus Vehicle",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = DeepSlateTertiary
                                    )
                                }

                                OutlinedTextField(
                                    value = vehicleModel,
                                    onValueChange = { vehicleModel = it },
                                    label = { Text("Vehicle Brand & Model (e.g. Ather / Ola / TVS)") },
                                    modifier = Modifier.fillMaxWidth().testTag("vehicle_model_input"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = DeepSlateTertiary,
                                        unfocusedTextColor = DeepSlateTertiary
                                    )
                                )

                                OutlinedTextField(
                                    value = vehiclePlate,
                                    onValueChange = { vehiclePlate = it },
                                    label = { Text("Plate / University Sticker Number") },
                                    modifier = Modifier.fillMaxWidth().testTag("vehicle_plate_input"),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = DeepSlateTertiary,
                                        unfocusedTextColor = DeepSlateTertiary
                                    )
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Checkbox(
                                        checked = helmetPledge,
                                        onCheckedChange = { helmetPledge = it },
                                        colors = CheckboxDefaults.colors(checkedColor = EcoGreenSecondary)
                                    )
                                    Text(
                                        text = "I pledge to carry an extra helmet and obey DBU 25 km/h campus speed limits.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = DeepSlateTertiary
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Complete Setup Submit Button
                Button(
                    onClick = {
                        if (nameInput.isBlank() || rollInput.isBlank()) {
                            Toast.makeText(context, "Please enter your name and DBU roll number", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (selectedRole == "driver" && !helmetPledge) {
                            Toast.makeText(context, "Please accept the DBU helmet safety pledge", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val profile = UserProfile(
                            displayName = nameInput.trim(),
                            email = initialEmail,
                            collegeId = rollInput.trim(),
                            isStudentVerified = true,
                            rating = 5.0,
                            ridesCount = 0L,
                            ecoPoints = 100L,
                            role = selectedRole,
                            universityName = "Desh Bhagat University (DBU)",
                            faculty = selectedFaculty,
                            residency = selectedResidency,
                            phoneNumber = phoneInput.trim(),
                            vehicleModel = if (selectedRole == "driver") vehicleModel.trim() else "",
                            vehiclePlate = if (selectedRole == "driver") vehiclePlate.trim() else "",
                            isProfileComplete = true
                        )
                        onCompleteSetup(profile)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("complete_signup_button"),
                    shape = RoundedCornerShape(9999.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedRole == "driver") EcoGreenSecondary else SolarGoldPrimary,
                        contentColor = if (selectedRole == "driver") Color.White else DeepSlateTertiary
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (selectedRole == "driver") "Complete DBU Driver Registration" else "Activate DBU Student Profile",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}
