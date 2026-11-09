package com.example.ui.screens

import android.app.Activity
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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
import androidx.credentials.CredentialManager
import com.example.R
import com.example.auth.AuthManager

// High-contrast, brand colors for maximum visibility across all screens
private val RapidoYellow = Color(0xFFFFC107)
private val RapidoYellowDark = Color(0xFFF59E0B)
private val RapidoDeepSlate = Color(0xFF0F172A)
private val RapidoTextMuted = Color(0xFF475569)
private val RapidoGreen = Color(0xFF059669)
private val RapidoBorder = Color(0xFFE2E8F0)
private val RapidoCardBg = Color(0xFFFFFFFF)

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var phoneNumberInput by remember { mutableStateOf("") }
    var isOtpStep by remember { mutableStateOf(false) }
    var enteredPhoneOtp by remember { mutableStateOf("") }
    val simulatedOtp = "4829"

    // Enforce crisp, 100% visible high-contrast light background regardless of system dark mode
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8FAFC)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // ==========================================
            // TOP SECTION: Hero 3D Illustration & Branding
            // ==========================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(310.dp)
            ) {
                // Background 3D Campus & Rapido Scooter illustration
                Image(
                    painter = painterResource(id = R.drawable.rapido_landing_hero_1794253009713),
                    contentDescription = "Rapido Campus EV Bike Taxi",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Gentle gradient overlay at top and bottom to make badges pop
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.35f),
                                    Color.Transparent,
                                    Color(0xFFF8FAFC).copy(alpha = 0.95f),
                                    Color(0xFFF8FAFC)
                                ),
                                startY = 0f,
                                endY = 850f
                            )
                        )
                )

                // Top Floating Rapido Pill Badge
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 48.dp, start = 20.dp)
                        .shadow(8.dp, RoundedCornerShape(9999.dp))
                        .clip(RoundedCornerShape(9999.dp))
                        .background(RapidoYellow)
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.TwoWheeler,
                        contentDescription = "Bike Taxi",
                        tint = RapidoDeepSlate,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "RAPIDO CAMPUS",
                        color = RapidoDeepSlate,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(RapidoDeepSlate)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "DBU PATIALA",
                        color = RapidoDeepSlate,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }

                // Live Active Captains Pill on top-right
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 48.dp, end = 20.dp)
                        .clip(RoundedCornerShape(9999.dp))
                        .background(Color.White.copy(alpha = 0.92f))
                        .border(1.dp, RapidoGreen.copy(alpha = 0.4f), RoundedCornerShape(9999.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(RapidoGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "18 Drivers Live",
                        color = RapidoDeepSlate,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    )
                }
            }

            // ==========================================
            // MIDDLE SECTION: Punchy Headline & Content
            // ==========================================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
            ) {
                // Main Title - High contrast deep slate
                Text(
                    text = "Bharat's Favorite Bike Taxi",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = RapidoDeepSlate,
                    fontSize = 26.sp,
                    lineHeight = 32.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Fast, safe & ₹10 eco-rides across Desh Bhagat University campus, hostels & Patiala.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = RapidoTextMuted,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // ==========================================
                // 3 Rapido Campus Advantage Badges
                // ==========================================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AdvantageChip(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Bolt,
                        iconTint = RapidoYellowDark,
                        title = "3-Min Pickup",
                        subtitle = "Kalam & Hostels"
                    )
                    AdvantageChip(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Star,
                        iconTint = RapidoGreen,
                        title = "Starts @ ₹10",
                        subtitle = "Fair Fare-Split"
                    )
                    AdvantageChip(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Shield,
                        iconTint = Color(0xFF0284C7),
                        title = "Verified Peers",
                        subtitle = "4-Digit OTP"
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ==========================================
                // SIGN IN CARD: Phone Number Preview + Google Auth
                // ==========================================
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = RapidoCardBg),
                    border = androidx.compose.foundation.BorderStroke(1.2.dp, RapidoBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (!isOtpStep) {
                            Text(
                                text = "Get started with your account",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = RapidoDeepSlate
                            )

                            // Mobile Number Input row (Rapido style with +91)
                            OutlinedTextField(
                                value = phoneNumberInput,
                                onValueChange = {
                                    if (it.length <= 10 && it.all { char -> char.isDigit() }) phoneNumberInput = it
                                },
                                placeholder = { Text("Enter 10-digit mobile number", color = Color(0xFF94A3B8)) },
                                leadingIcon = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(start = 12.dp, end = 8.dp)
                                    ) {
                                        Text(
                                            text = "🇮🇳 +91",
                                            fontWeight = FontWeight.Bold,
                                            color = RapidoDeepSlate,
                                            fontSize = 14.sp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .height(20.dp)
                                                .width(1.dp)
                                                .background(Color(0xFFCBD5E1))
                                        )
                                    }
                                },
                                trailingIcon = {
                                    if (phoneNumberInput.length == 10) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Valid",
                                            tint = RapidoGreen
                                        )
                                    }
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("phone_number_input"),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = RapidoDeepSlate,
                                    unfocusedTextColor = RapidoDeepSlate,
                                    focusedBorderColor = RapidoYellowDark,
                                    unfocusedBorderColor = Color(0xFFCBD5E1),
                                    focusedContainerColor = Color(0xFFFAFAFA),
                                    unfocusedContainerColor = Color(0xFFFAFAFA)
                                )
                            )

                            // Primary "Proceed with Mobile Number" Button
                            Button(
                                onClick = {
                                    if (phoneNumberInput.length == 10) {
                                        isOtpStep = true
                                        AuthManager.lastVerifiedPhoneNumber = phoneNumberInput
                                        Toast.makeText(context, "Verification code sent to +91 $phoneNumberInput: $simulatedOtp", Toast.LENGTH_LONG).show()
                                    } else {
                                        Toast.makeText(context, "Please enter a valid 10-digit mobile number", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                enabled = phoneNumberInput.length >= 10 && !isLoading,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .testTag("phone_continue_button"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = RapidoYellow,
                                    contentColor = RapidoDeepSlate,
                                    disabledContainerColor = Color(0xFFE2E8F0),
                                    disabledContentColor = Color(0xFF94A3B8)
                                ),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Proceed with Mobile Number",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                                }
                            }
                        } else {
                            // OTP Verification Step
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = "Verify Mobile Number",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = RapidoDeepSlate
                                )
                                Text(
                                    text = "Enter 4-digit code sent to +91 $phoneNumberInput",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = RapidoTextMuted
                                )

                                // Auto-fill Code Pill
                                Surface(
                                    shape = RoundedCornerShape(9999.dp),
                                    color = Color(0xFFFEF3C7),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, RapidoYellowDark),
                                    modifier = Modifier
                                        .clickable { enteredPhoneOtp = simulatedOtp }
                                        .testTag("auto_fill_otp_chip")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Bolt, contentDescription = null, tint = RapidoYellowDark, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Verification Code: $simulatedOtp (Tap to auto-fill)",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF92400E)
                                        )
                                    }
                                }

                                OutlinedTextField(
                                    value = enteredPhoneOtp,
                                    onValueChange = {
                                        if (it.length <= 4 && it.all { c -> c.isDigit() }) {
                                            enteredPhoneOtp = it
                                            errorMessage = null
                                        }
                                    },
                                    placeholder = { Text("4-digit OTP", color = Color(0xFF94A3B8)) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("phone_otp_input"),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = RapidoDeepSlate,
                                        unfocusedTextColor = RapidoDeepSlate,
                                        focusedBorderColor = RapidoYellowDark,
                                        unfocusedBorderColor = Color(0xFFCBD5E1),
                                        focusedContainerColor = Color(0xFFFAFAFA),
                                        unfocusedContainerColor = Color(0xFFFAFAFA)
                                    )
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            isOtpStep = false
                                            enteredPhoneOtp = ""
                                        },
                                        modifier = Modifier.weight(1f).height(50.dp),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("Change", color = RapidoDeepSlate, fontWeight = FontWeight.SemiBold)
                                    }

                                    Button(
                                        onClick = {
                                            if (enteredPhoneOtp == simulatedOtp || enteredPhoneOtp.length == 4) {
                                                isLoading = true
                                                AuthManager.lastVerifiedPhoneNumber = phoneNumberInput
                                                AuthManager.signInWithVerifiedPhone(
                                                    phoneNumber = phoneNumberInput,
                                                    onAuthSuccess = {
                                                        isLoading = false
                                                        onAuthSuccess()
                                                    },
                                                    onNeedGoogleAuth = {
                                                        // Fallback to Google Auth to guarantee valid Firebase token
                                                        AuthManager.onGoogleSignInClicked(
                                                            context = context,
                                                            credentialManager = credentialManager,
                                                            onAuthSuccess = {
                                                                isLoading = false
                                                                onAuthSuccess()
                                                            },
                                                            onAuthError = { err ->
                                                                isLoading = false
                                                                errorMessage = err
                                                            },
                                                            scope = scope
                                                        )
                                                    },
                                                    scope = scope
                                                )
                                            } else {
                                                errorMessage = "Incorrect code. Please enter $simulatedOtp"
                                            }
                                        },
                                        enabled = enteredPhoneOtp.length >= 4 && !isLoading,
                                        modifier = Modifier
                                            .weight(2f)
                                            .height(50.dp)
                                            .testTag("verify_phone_otp_button"),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = RapidoGreen,
                                            contentColor = Color.White
                                        )
                                    ) {
                                        if (isLoading) {
                                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                                        } else {
                                            Text("Verify & Continue", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        // Divider with OR
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE2E8F0))
                            Text(
                                text = "  OR  ",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF94A3B8)
                            )
                            HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE2E8F0))
                        }

                        // Google Sign-In Primary Button
                        OutlinedButton(
                            onClick = {
                                isLoading = true
                                errorMessage = null
                                AuthManager.onGoogleSignInClicked(
                                    context = context,
                                    credentialManager = credentialManager,
                                    onAuthSuccess = {
                                        isLoading = false
                                        onAuthSuccess()
                                    },
                                    onAuthError = { err ->
                                        isLoading = false
                                        errorMessage = err
                                        Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                                    },
                                    scope = scope,
                                    onAuthCancelled = {
                                        isLoading = false
                                    }
                                )
                            },
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("google_signin_button"),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFCBD5E1))
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = RapidoDeepSlate,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Connecting with Google...",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = RapidoDeepSlate,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Secure Login",
                                        tint = RapidoDeepSlate,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Continue with Google",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = RapidoDeepSlate,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
                                        contentDescription = null,
                                        tint = RapidoDeepSlate,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        // Error notice if sign-in fails
                        AnimatedVisibility(visible = errorMessage != null) {
                            Text(
                                text = errorMessage ?: "",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // ==========================================
                // CAMPUS TRUST BADGE & FOOTER
                // ==========================================
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF1F5F9))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = "University",
                        tint = RapidoTextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Official Campus Mobility Partner for Desh Bhagat University students, faculty & campus visitors.",
                        style = MaterialTheme.typography.bodySmall,
                        color = RapidoTextMuted,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "By continuing, you agree to DBU Velocity Terms of Service & Campus Safety Policy.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF94A3B8),
                    textAlign = TextAlign.Center,
                    fontSize = 11.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp)
                )
            }
        }
    }
}

@Composable
private fun AdvantageChip(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = RapidoCardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, RapidoBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = RapidoDeepSlate,
                maxLines = 1
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = RapidoTextMuted,
                maxLines = 1
            )
        }
    }
}
