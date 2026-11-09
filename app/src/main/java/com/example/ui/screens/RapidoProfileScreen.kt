package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ElectricScooter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.auth.AuthManager
import com.example.data.model.Ride
import com.example.data.model.UserProfile
import com.example.ui.MainViewModel
import com.example.ui.theme.DeepSlateTertiary
import com.example.ui.theme.EcoGreenSecondary
import com.example.ui.theme.SignalRedError
import com.example.ui.theme.SolarGoldPrimary

private enum class ProfileModal {
    NONE, HELP, PAYMENT, MY_RIDES, SAFETY, REFER_EARN, REWARDS_COINS, POWER_PASS, NOTIFICATIONS, CLAIMS, EDIT_PROFILE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RapidoProfileScreen(
    profile: UserProfile?,
    viewModel: MainViewModel? = null,
    onOpenSettings: () -> Unit,
    onOpenCaptainMode: () -> Unit,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var activeModal by remember { mutableStateOf(ProfileModal.NONE) }
    var walletBalance by remember { mutableLongStateOf(150L) }
    var rapidoCoinsBalance by remember { mutableLongStateOf(120L) }

    // User rides observation
    val userRides by (viewModel?.userRides?.collectAsState() ?: remember { mutableStateOf(emptyList<Ride>()) })

    // Edit profile dialog state
    var editName by remember(profile?.displayName) { mutableStateOf(profile?.displayName ?: "DBU Student") }
    var editRoll by remember(profile?.collegeId) { mutableStateOf(profile?.collegeId ?: "DBU/2024/ENG/108") }
    var editPhone by remember(profile?.phoneNumber) {
        mutableStateOf(profile?.phoneNumber?.ifBlank { AuthManager.lastVerifiedPhoneNumber ?: "9876543210" } ?: (AuthManager.lastVerifiedPhoneNumber ?: "9876543210"))
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(top = 44.dp, bottom = 12.dp, start = 20.dp, end = 20.dp)
            ) {
                Text(
                    text = "Profile",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = DeepSlateTertiary
                )
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .padding(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Top Profile Card with Real User Name, Phone & Roll Number (NO HARDCODED SAKIB)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("rapido_profile_header_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // User info row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { activeModal = ProfileModal.EDIT_PROFILE },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF1F5F9))
                                .border(2.dp, SolarGoldPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = DeepSlateTertiary,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = profile?.displayName?.ifBlank { "DBU Student" } ?: "DBU Student",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepSlateTertiary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Profile",
                                    tint = Color(0xFF64748B),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = profile?.phoneNumber?.ifBlank { AuthManager.lastVerifiedPhoneNumber ?: "+91 98765 43210" } ?: (AuthManager.lastVerifiedPhoneNumber ?: "+91 98765 43210"),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF64748B)
                            )
                            Text(
                                text = "Roll: ${profile?.collegeId?.ifBlank { "DBU/2024/ENG/108" } ?: "DBU/2024/ENG/108"}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = EcoGreenSecondary
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = "Details",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(14.dp))

                    // Rating & Eco Badge Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = SolarGoldPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "%.2f My Rating".format(profile?.rating ?: 4.95),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = DeepSlateTertiary
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(9999.dp),
                            color = Color(0xFFD1FAE5)
                        ) {
                            Text(
                                text = "🌱 ${profile?.ecoPoints ?: 150} Eco Points",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF065F46),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // 2. Menu List Items (Now every single item opens a real sub-page/sheet!)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    RapidoMenuItem(
                        icon = Icons.AutoMirrored.Filled.HelpOutline,
                        title = "Help & Support",
                        subtitle = "24/7 DBU Safety & Ride Assistance"
                    ) {
                        activeModal = ProfileModal.HELP
                    }

                    RapidoMenuItem(
                        icon = Icons.Default.CreditCard,
                        title = "Payment",
                        subtitle = "Wallet: ₹$walletBalance • UPI & Cards"
                    ) {
                        activeModal = ProfileModal.PAYMENT
                    }

                    RapidoMenuItem(
                        icon = Icons.Default.History,
                        title = "My Rides",
                        subtitle = "${userRides.size} Past Campus Trips"
                    ) {
                        activeModal = ProfileModal.MY_RIDES
                    }

                    RapidoMenuItem(
                        icon = Icons.Default.Security,
                        title = "Safety & Shield",
                        subtitle = "Emergency Contacts & Campus GPS"
                    ) {
                        activeModal = ProfileModal.SAFETY
                    }

                    RapidoMenuItem(
                        icon = Icons.Default.CardGiftcard,
                        title = "Refer and Earn",
                        subtitle = "Get ₹50 Free Ride per friend"
                    ) {
                        activeModal = ProfileModal.REFER_EARN
                    }

                    RapidoMenuItem(
                        icon = Icons.Default.WorkspacePremium,
                        title = "My Rewards",
                        subtitle = "Coupons & Discounts"
                    ) {
                        activeModal = ProfileModal.REWARDS_COINS
                    }

                    RapidoMenuItem(
                        icon = Icons.Default.MonetizationOn,
                        title = "Rapido Coins",
                        subtitle = "$rapidoCoinsBalance Coins Available"
                    ) {
                        activeModal = ProfileModal.REWARDS_COINS
                    }

                    RapidoMenuItem(
                        icon = Icons.Default.TwoWheeler,
                        title = "Power Pass",
                        subtitle = "50% off monthly campus rides"
                    ) {
                        activeModal = ProfileModal.POWER_PASS
                    }

                    RapidoMenuItem(
                        icon = Icons.Default.Notifications,
                        title = "Notifications"
                    ) {
                        activeModal = ProfileModal.NOTIFICATIONS
                    }

                    RapidoMenuItem(
                        icon = Icons.Default.Description,
                        title = "Claims & Insurance"
                    ) {
                        activeModal = ProfileModal.CLAIMS
                    }

                    RapidoMenuItem(
                        icon = Icons.Default.Settings,
                        title = "Settings"
                    ) {
                        onOpenSettings()
                    }
                }
            }

            // 3. Become a Captain Banner (Opens Captain App directly!)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenCaptainMode() }
                    .testTag("become_captain_banner"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF9C3)),
                border = androidx.compose.foundation.BorderStroke(1.dp, SolarGoldPrimary.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Earn money with Rapido",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF854D0E)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Become a Captain!",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = DeepSlateTertiary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(SolarGoldPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ElectricScooter,
                            contentDescription = "Captain",
                            tint = DeepSlateTertiary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // 4. Sign out button
            OutlinedButton(
                onClick = onSignOut,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("rapido_sign_out_button"),
                shape = RoundedCornerShape(9999.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SignalRedError)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = null,
                    tint = SignalRedError,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sign Out",
                    fontWeight = FontWeight.Bold,
                    color = SignalRedError
                )
            }
        }
    }

    // ==========================================
    // INTERACTIVE SUB-PAGES & BOTTOM SHEETS
    // ==========================================

    if (activeModal != ProfileModal.NONE) {
        ModalBottomSheet(
            onDismissRequest = { activeModal = ProfileModal.NONE },
            sheetState = sheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .padding(bottom = 36.dp)
            ) {
                // Modal Close Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (activeModal) {
                            ProfileModal.HELP -> "Help & Support"
                            ProfileModal.PAYMENT -> "Payment & Wallet"
                            ProfileModal.MY_RIDES -> "My Campus Rides"
                            ProfileModal.SAFETY -> "Safety & Emergency Shield"
                            ProfileModal.REFER_EARN -> "Refer & Earn ₹50"
                            ProfileModal.REWARDS_COINS -> "Rewards & Rapido Coins"
                            ProfileModal.POWER_PASS -> "DBU Power Pass"
                            ProfileModal.NOTIFICATIONS -> "Notifications"
                            ProfileModal.CLAIMS -> "Insurance & Claims"
                            ProfileModal.EDIT_PROFILE -> "Edit DBU Profile"
                            else -> ""
                        },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = DeepSlateTertiary
                    )

                    IconButton(onClick = { activeModal = ProfileModal.NONE }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                when (activeModal) {
                    ProfileModal.HELP -> {
                        HelpSubScreen(context = context)
                    }

                    ProfileModal.PAYMENT -> {
                        PaymentSubScreen(
                            walletBalance = walletBalance,
                            onAddMoney = { amount ->
                                walletBalance += amount
                                Toast.makeText(context, "₹$amount added to Rapido Wallet!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }

                    ProfileModal.MY_RIDES -> {
                        MyRidesSubScreen(rides = userRides, onDismiss = { activeModal = ProfileModal.NONE })
                    }

                    ProfileModal.SAFETY -> {
                        SafetySubScreen(context = context)
                    }

                    ProfileModal.REFER_EARN -> {
                        ReferEarnSubScreen(context = context, clipboardManager = clipboardManager)
                    }

                    ProfileModal.REWARDS_COINS -> {
                        RewardsCoinsSubScreen(
                            coins = rapidoCoinsBalance,
                            onRedeemCoins = { cost, disc ->
                                if (rapidoCoinsBalance >= cost) {
                                    rapidoCoinsBalance -= cost
                                    Toast.makeText(context, "Redeemed! ₹$disc coupon code applied to next ride.", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, "Need at least $cost coins", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }

                    ProfileModal.POWER_PASS -> {
                        PowerPassSubScreen(context = context)
                    }

                    ProfileModal.NOTIFICATIONS -> {
                        NotificationsSubScreen()
                    }

                    ProfileModal.CLAIMS -> {
                        ClaimsSubScreen()
                    }

                    ProfileModal.EDIT_PROFILE -> {
                        EditProfileSubScreen(
                            name = editName,
                            roll = editRoll,
                            phone = editPhone,
                            onNameChange = { editName = it },
                            onRollChange = { editRoll = it },
                            onPhoneChange = { editPhone = it },
                            onSave = {
                                val updatedProfile = (profile ?: UserProfile()).copy(
                                    displayName = editName.trim(),
                                    collegeId = editRoll.trim(),
                                    phoneNumber = editPhone.trim(),
                                    isProfileComplete = true
                                )
                                AuthManager.lastVerifiedPhoneNumber = editPhone.trim()
                                viewModel?.completeProfileSetup(updatedProfile)
                                activeModal = ProfileModal.NONE
                                Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }

                    else -> {}
                }
            }
        }
    }
}

// ==========================================
// SUB-SCREEN IMPLEMENTATIONS
// ==========================================

@Composable
private fun HelpSubScreen(context: android.content.Context) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Phone, contentDescription = null, tint = DeepSlateTertiary)
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("24/7 DBU Campus Hotline", fontWeight = FontWeight.Bold, color = DeepSlateTertiary)
                    Text("Call toll-free: 1800-200-8888", fontSize = 12.sp, color = Color(0xFF854D0E))
                }
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:18002008888"))
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepSlateTertiary)
                ) {
                    Text("Call Now")
                }
            }
        }

        Text("Frequently Asked Questions", fontWeight = FontWeight.Bold, color = DeepSlateTertiary, modifier = Modifier.padding(top = 8.dp))
        FaqItem("How does the ₹10 campus fare work?", "All rides within DBU campus boundaries have a flat ₹10 student rate with instant fare splitting.")
        FaqItem("What if I forgot an item in a bike taxi?", "Call our 24/7 dispatch hotline with your 4-digit ride OTP. The captain will return it to DBU Security Desk.")
        FaqItem("How to report reckless driving?", "Use the SOS button during ride or report through this support screen to flag the driver.")
    }
}

@Composable
private fun PaymentSubScreen(
    walletBalance: Long,
    onAddMoney: (Long) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Card(
            colors = CardDefaults.cardColors(containerColor = DeepSlateTertiary),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("RAPIDO CAMPUS WALLET", color = Color(0xFF94A3B8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("₹$walletBalance.00", color = SolarGoldPrimary, fontSize = 32.sp, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(50L, 100L, 200L).forEach { amount ->
                        Button(
                            onClick = { onAddMoney(amount) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(9999.dp)
                        ) {
                            Text("+ ₹$amount", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Text("Saved Payment Methods", fontWeight = FontWeight.Bold, color = DeepSlateTertiary)
        PaymentMethodRow("Google Pay / PhonePe UPI", "Primary", true)
        PaymentMethodRow("Paytm UPI (dbu.student@paytm)", "Verified", false)
        PaymentMethodRow("DBU Student Smart Card", "Linked", false)
        PaymentMethodRow("Cash on Drop", "Always available", false)
    }
}

@Composable
private fun MyRidesSubScreen(rides: List<Ride>, onDismiss: () -> Unit) {
    if (rides.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.ElectricScooter, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(10.dp))
            Text("No Rides Yet", fontWeight = FontWeight.Bold, color = DeepSlateTertiary)
            Text("Your booked campus rides will show here with route, fare and receipts.", fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center)
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.height(350.dp)) {
            items(rides) { ride ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(ride.vehicleType ?: "Campus EV", fontWeight = FontWeight.Bold, color = DeepSlateTertiary)
                            Text("₹${ride.fare}", fontWeight = FontWeight.Black, color = EcoGreenSecondary)
                        }
                        Text("${ride.pickupLocation} → ${ride.dropLocation}", fontSize = 12.sp, color = Color(0xFF475569))
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = RoundedCornerShape(9999.dp), color = if (ride.status == "completed") Color(0xFFD1FAE5) else Color(0xFFFEF3C7)) {
                                Text(ride.status.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (ride.status == "completed") Color(0xFF065F46) else Color(0xFF92400E), modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("OTP: ${ride.otp} • Driver: ${ride.driverName ?: "Assigned"}", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SafetySubScreen(context: android.content.Context) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)), shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = SignalRedError)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("DBU Safety Shield Active", fontWeight = FontWeight.Bold, color = SignalRedError)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text("All campus rides are GPS tracked in real-time. Speed capped at 25 km/h on DBU internal avenues.", fontSize = 12.sp, color = Color(0xFF7F1D1D))
            }
        }

        Text("Registered Emergency Contacts", fontWeight = FontWeight.Bold, color = DeepSlateTertiary)
        ContactRow("Parents / Guardian", "+91 98765-01234")
        ContactRow("DBU Hostel Warden", "+91 98765-05678")
        ContactRow("Campus Gate Security", "+91 1765-520000")
    }
}

@Composable
private fun ReferEarnSubScreen(context: android.content.Context, clipboardManager: androidx.compose.ui.platform.ClipboardManager) {
    val code = "DBU50"
    Column(verticalArrangement = Arrangement.spacedBy(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(64.dp).clip(CircleShape).background(Color(0xFFFEF3C7)), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = SolarGoldPrimary, modifier = Modifier.size(36.dp))
        }

        Text("Invite Classmates & Earn ₹50", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = DeepSlateTertiary)
        Text("Give ₹50 off on their first campus ride, and you'll get ₹50 added to your Rapido Wallet when they take their first ride.", fontSize = 13.sp, color = Color(0xFF64748B), textAlign = TextAlign.Center)

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFF1F5F9),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(code, fontWeight = FontWeight.Black, fontSize = 20.sp, letterSpacing = 2.sp, color = DeepSlateTertiary)
                IconButton(onClick = {
                    clipboardManager.setText(AnnotatedString(code))
                    Toast.makeText(context, "Referral code copied!", Toast.LENGTH_SHORT).show()
                }) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = DeepSlateTertiary)
                }
            }
        }

        Button(
            onClick = {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "Hey! Use my Rapido Campus code $code to get ₹50 free ride credits across Desh Bhagat University! Download Campus Velocity.")
                    type = "text/plain"
                }
                context.startActivity(Intent.createChooser(sendIntent, "Share Referral Code"))
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SolarGoldPrimary, contentColor = DeepSlateTertiary)
        ) {
            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Share with Friends via WhatsApp", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun RewardsCoinsSubScreen(
    coins: Long,
    onRedeemCoins: (Long, Long) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)), shape = RoundedCornerShape(18.dp)) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("RAPIDO ECO COINS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF854D0E))
                    Text("$coins Coins", fontSize = 28.sp, fontWeight = FontWeight.Black, color = DeepSlateTertiary)
                    Text("1 Coin = ₹1 Discount", fontSize = 11.sp, color = Color(0xFF854D0E))
                }
                Button(
                    onClick = { onRedeemCoins(50L, 50L) },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepSlateTertiary)
                ) {
                    Text("Redeem 50")
                }
            }
        }

        Text("Active Campus Coupons", fontWeight = FontWeight.Bold, color = DeepSlateTertiary)
        CouponItem("CAMPUSFIRST", "Flat ₹15 off on your first 3 campus rides")
        CouponItem("EXAMRUSH", "20% off all rides to Kalam Central Library")
        CouponItem("ECOGREEN", "₹10 instant discount when sharing ride with classmate")
    }
}

@Composable
private fun PowerPassSubScreen(context: android.content.Context) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFD1FAE5)), shape = RoundedCornerShape(18.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("DBU SEMESTER POWER PASS", fontWeight = FontWeight.Black, color = Color(0xFF065F46))
                Spacer(modifier = Modifier.height(4.dp))
                Text("Flat 50% discount on unlimited campus rides for 30 days. Perfect for daily hostel-to-class commutes.", fontSize = 12.sp, color = Color(0xFF047857))
                Spacer(modifier = Modifier.height(14.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Only ₹99 / Month", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = DeepSlateTertiary)
                    Button(
                        onClick = { Toast.makeText(context, "DBU Power Pass Activated! 50% off applied.", Toast.LENGTH_LONG).show() },
                        colors = ButtonDefaults.buttonColors(containerColor = EcoGreenSecondary)
                    ) {
                        Text("Activate Pass")
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationsSubScreen() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        NotificationItem("Ride Discount Active", "Get flat ₹10 off on peer EV rides today around DBU Kalam Block.", "2h ago")
        NotificationItem("Safety Compliance Update", "Helmets mandatory for all campus bike taxi riders and captains.", "1d ago")
        NotificationItem("Welcome to Campus Velocity", "Your student profile is active. Happy riding!", "2d ago")
    }
}

@Composable
private fun ClaimsSubScreen() {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)), shape = RoundedCornerShape(14.dp)) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Zero Active Insurance Claims", fontWeight = FontWeight.Bold, color = DeepSlateTertiary)
                Text("Every ride on DBU Campus Velocity is insured up to ₹5,00,000 for accidental cover by ICICI Lombard.", fontSize = 12.sp, color = Color(0xFF64748B))
            }
        }
    }
}

@Composable
private fun EditProfileSubScreen(
    name: String,
    roll: String,
    phone: String,
    onNameChange: (String) -> Unit,
    onRollChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onSave: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth().testTag("edit_profile_name_input"),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = DeepSlateTertiary, unfocusedTextColor = DeepSlateTertiary)
        )

        OutlinedTextField(
            value = roll,
            onValueChange = onRollChange,
            label = { Text("DBU Student Roll / Reg Number") },
            modifier = Modifier.fillMaxWidth().testTag("edit_profile_roll_input"),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = DeepSlateTertiary, unfocusedTextColor = DeepSlateTertiary)
        )

        OutlinedTextField(
            value = phone,
            onValueChange = onPhoneChange,
            label = { Text("Mobile Contact Number") },
            modifier = Modifier.fillMaxWidth().testTag("edit_profile_phone_input"),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = DeepSlateTertiary, unfocusedTextColor = DeepSlateTertiary)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth().height(50.dp).testTag("save_profile_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SolarGoldPrimary, contentColor = DeepSlateTertiary)
        ) {
            Text("Save Profile Changes", fontWeight = FontWeight.Bold)
        }
    }
}

// Small UI helpers
@Composable
private fun FaqItem(question: String, answer: String) {
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(question, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DeepSlateTertiary)
            Text(answer, fontSize = 12.sp, color = Color(0xFF64748B))
        }
    }
}

@Composable
private fun PaymentMethodRow(title: String, badge: String, isPrimary: Boolean) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = DeepSlateTertiary)
        Surface(shape = RoundedCornerShape(9999.dp), color = if (isPrimary) Color(0xFFD1FAE5) else Color(0xFFF1F5F9)) {
            Text(badge, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isPrimary) Color(0xFF065F46) else Color.Gray, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
        }
    }
}

@Composable
private fun ContactRow(label: String, phone: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = DeepSlateTertiary)
        Text(phone, fontSize = 13.sp, color = Color(0xFF0284C7), fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun CouponItem(code: String, description: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column {
            Text(code, fontWeight = FontWeight.Black, fontSize = 14.sp, color = DeepSlateTertiary)
            Text(description, fontSize = 11.sp, color = Color(0xFF64748B))
        }
        Surface(shape = RoundedCornerShape(9999.dp), color = Color(0xFFFEF3C7)) {
            Text("APPLY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E), modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
        }
    }
}

@Composable
private fun NotificationItem(title: String, desc: String, time: String) {
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DeepSlateTertiary)
                Text(time, fontSize = 10.sp, color = Color.Gray)
            }
            Text(desc, fontSize = 11.sp, color = Color(0xFF64748B))
        }
    }
}

@Composable
private fun RapidoMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF475569),
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = DeepSlateTertiary
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = EcoGreenSecondary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = Color(0xFFCBD5E1),
                modifier = Modifier.size(14.dp)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 0.8.dp)
    }
}
