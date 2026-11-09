package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.auth.AuthManager
import com.example.data.repository.CampusRideRepository
import com.example.ui.MainViewModel
import com.example.ui.components.Globe3DCanvas
import com.example.ui.screens.AllServicesScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CampusMapScreen
import com.example.ui.screens.DriverLobbyScreen
import com.example.ui.screens.RapidoProfileScreen
import com.example.ui.screens.SearchDropScreen
import com.example.ui.screens.RapidoCaptainScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SignupSetupScreen
import com.example.ui.screens.TravelScreen
import com.example.ui.theme.CampusVelocityTheme
import com.example.ui.theme.DeepSlateTertiary
import com.example.ui.theme.SolarGoldPrimary
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

object NavRoutes {
    const val AUTH = "auth"
    const val SIGNUP = "signup"
    const val MAIN_TABS = "main_tabs"
    const val SEARCH_DROP = "search_drop"
    const val GLOBE = "globe"
    const val DRIVER_LOBBY = "driver_lobby"
    const val CAPTAIN = "captain"
    const val SETTINGS = "settings"
}

enum class RapidoTab {
    RIDE, ALL_SERVICES, TRAVEL, PROFILE
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CampusVelocityTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    val navController = rememberNavController()
    var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
        }
        Firebase.auth.addAuthStateListener(listener)

        if (currentUser == null) {
            AuthManager.attemptAutoSignIn(
                context = context,
                credentialManager = credentialManager,
                onAuthSuccess = { currentUser = Firebase.auth.currentUser },
                onUnauthenticated = { },
                scope = scope
            )
        }

        onDispose {
            Firebase.auth.removeAuthStateListener(listener)
        }
    }

    if (currentUser == null) {
        AuthScreen(
            onAuthSuccess = {
                currentUser = Firebase.auth.currentUser
            }
        )
    } else {
        val user = currentUser!!
        val uid = user.uid

        key(uid) {
            val viewModel: MainViewModel = viewModel(
                key = uid,
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        val repository = CampusRideRepository(context)
                        return MainViewModel(repository, uid) as T
                    }
                }
            )

            val userProfile by viewModel.userProfile.collectAsState()
            val currentLatitude by viewModel.currentLatitude.collectAsState()
            val currentLongitude by viewModel.currentLongitude.collectAsState()
            val nearestLandmark by viewModel.nearestLandmark.collectAsState()
            val pickupInput by viewModel.pickupInput.collectAsState()

            val startDestination = if (userProfile != null && !userProfile!!.isProfileComplete) {
                NavRoutes.SIGNUP
            } else {
                NavRoutes.MAIN_TABS
            }

            LaunchedEffect(userProfile?.isProfileComplete) {
                if (userProfile != null && !userProfile!!.isProfileComplete) {
                    navController.navigate(NavRoutes.SIGNUP) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }

            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(NavRoutes.AUTH) {
                    AuthScreen(
                        onAuthSuccess = {
                            currentUser = Firebase.auth.currentUser
                        }
                    )
                }

                composable(NavRoutes.SIGNUP) {
                    SignupSetupScreen(
                        initialName = userProfile?.displayName ?: user.displayName ?: "DBU Student",
                        initialEmail = userProfile?.email ?: user.email ?: "",
                        onCompleteSetup = { newProfile ->
                            viewModel.completeProfileSetup(newProfile)
                            navController.navigate(NavRoutes.MAIN_TABS) {
                                popUpTo(NavRoutes.SIGNUP) { inclusive = true }
                            }
                        }
                    )
                }

                // Main Rapido Tabbed Container (Ride, All Services, Travel, Profile)
                composable(NavRoutes.MAIN_TABS) {
                    RapidoMainContainer(
                        viewModel = viewModel,
                        onNavigateToSearch = {
                            navController.navigate(NavRoutes.SEARCH_DROP)
                        },
                        onNavigateToGlobe = {
                            navController.navigate(NavRoutes.GLOBE)
                        },
                        onNavigateToDriverLobby = {
                            navController.navigate(NavRoutes.DRIVER_LOBBY)
                        },
                        onNavigateToCaptain = {
                            navController.navigate(NavRoutes.CAPTAIN)
                        },
                        onNavigateToSettings = {
                            navController.navigate(NavRoutes.SETTINGS)
                        },
                        onSignOut = {
                            AuthManager.signOut(
                                credentialManager = credentialManager,
                                scope = scope,
                                onSignOutComplete = {
                                    currentUser = null
                                }
                            )
                        }
                    )
                }

                // Dedicated Rapido Captain App Screen
                composable(NavRoutes.CAPTAIN) {
                    RapidoCaptainScreen(
                        viewModel = viewModel,
                        onSwitchToPassengerMode = {
                            navController.navigate(NavRoutes.MAIN_TABS) {
                                popUpTo(NavRoutes.MAIN_TABS) { inclusive = true }
                            }
                        }
                    )
                }

                // Drop Location Search Screen (Rapido Screenshot 6)
                composable(NavRoutes.SEARCH_DROP) {
                    SearchDropScreen(
                        currentPickup = pickupInput,
                        onPickupChanged = { viewModel.setPickupInput(it) },
                        onDestinationSelected = { landmark ->
                            viewModel.setDropInput(landmark.name)
                            navController.popBackStack()
                            viewModel.openBookingSheet(true)
                        },
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }

                // 3D Globe Screen
                composable(NavRoutes.GLOBE) {
                    Globe3DCanvas(
                        userLatitude = currentLatitude,
                        userLongitude = currentLongitude,
                        onNavigateBackToMap = {
                            navController.popBackStack()
                        }
                    )
                }

                // Driver / Captain Lobby
                composable(NavRoutes.DRIVER_LOBBY) {
                    DriverLobbyScreen(
                        viewModel = viewModel,
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }

                // Settings Screen
                composable(NavRoutes.SETTINGS) {
                    SettingsScreen(
                        profile = userProfile,
                        currentLatitude = currentLatitude,
                        currentLongitude = currentLongitude,
                        nearestLandmark = nearestLandmark,
                        onRefreshLocation = { viewModel.refreshLocation(context) },
                        onSignOut = {
                            AuthManager.signOut(
                                credentialManager = credentialManager,
                                scope = scope,
                                onSignOutComplete = {
                                    currentUser = null
                                }
                            )
                        },
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RapidoMainContainer(
    viewModel: MainViewModel,
    onNavigateToSearch: () -> Unit,
    onNavigateToGlobe: () -> Unit,
    onNavigateToDriverLobby: () -> Unit,
    onNavigateToCaptain: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onSignOut: () -> Unit
) {
    var currentTab by remember { mutableStateOf(RapidoTab.RIDE) }
    val userProfile by viewModel.userProfile.collectAsState()

    Scaffold(
        bottomBar = {
            RapidoBottomNavBar(
                selectedTab = currentTab,
                onTabSelected = { currentTab = it }
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentTab) {
                RapidoTab.RIDE -> {
                    CampusMapScreen(
                        viewModel = viewModel,
                        onNavigateToSearch = onNavigateToSearch,
                        onNavigateToGlobe = onNavigateToGlobe,
                        onNavigateToDriverLobby = onNavigateToDriverLobby,
                        onNavigateToCaptain = onNavigateToCaptain
                    )
                }

                RapidoTab.ALL_SERVICES -> {
                    AllServicesScreen(
                        onServiceSelected = { service ->
                            // Map service to vehicle
                            val matchedVehicle = com.example.data.model.CampusData.Vehicles.firstOrNull {
                                it.name.contains(service.title, ignoreCase = true) || service.title.contains(it.name, ignoreCase = true)
                            } ?: com.example.data.model.CampusData.Vehicles.first()
                            viewModel.setVehicle(matchedVehicle)
                            currentTab = RapidoTab.RIDE
                            viewModel.openBookingSheet(true)
                        }
                    )
                }

                RapidoTab.TRAVEL -> {
                    TravelScreen(
                        onBookRoute = { route ->
                            viewModel.setPickupInput(route.from)
                            viewModel.setDropInput(route.to)
                            currentTab = RapidoTab.RIDE
                            viewModel.openBookingSheet(true)
                        }
                    )
                }

                RapidoTab.PROFILE -> {
                    RapidoProfileScreen(
                        profile = userProfile,
                        viewModel = viewModel,
                        onOpenSettings = onNavigateToSettings,
                        onOpenCaptainMode = onNavigateToCaptain,
                        onSignOut = onSignOut
                    )
                }
            }
        }
    }
}

@Composable
fun RapidoBottomNavBar(
    selectedTab: RapidoTab,
    onTabSelected: (RapidoTab) -> Unit
) {
    Surface(
        color = Color.White,
        shadowElevation = 12.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.navigationBarsPadding()) {
            HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 0.8.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RapidoNavItem(
                    icon = Icons.Default.Home,
                    label = "Ride",
                    isSelected = selectedTab == RapidoTab.RIDE,
                    onClick = { onTabSelected(RapidoTab.RIDE) },
                    testTag = "nav_ride_tab"
                )

                RapidoNavItem(
                    icon = Icons.Default.NearMe,
                    label = "All Services",
                    isSelected = selectedTab == RapidoTab.ALL_SERVICES,
                    onClick = { onTabSelected(RapidoTab.ALL_SERVICES) },
                    testTag = "nav_services_tab"
                )

                RapidoNavItem(
                    icon = Icons.Default.Luggage,
                    label = "Travel",
                    isSelected = selectedTab == RapidoTab.TRAVEL,
                    onClick = { onTabSelected(RapidoTab.TRAVEL) },
                    testTag = "nav_travel_tab"
                )

                RapidoNavItem(
                    icon = Icons.Default.Person,
                    label = "Profile",
                    isSelected = selectedTab == RapidoTab.PROFILE,
                    onClick = { onTabSelected(RapidoTab.PROFILE) },
                    testTag = "nav_profile_tab"
                )
            }
        }
    }
}

@Composable
private fun RapidoNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) DeepSlateTertiary else Color(0xFF64748B),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Normal,
            color = if (isSelected) DeepSlateTertiary else Color(0xFF64748B),
            fontSize = 11.sp
        )
    }
}
