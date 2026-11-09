package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.ElectricScooter
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CampusData
import com.example.ui.theme.DeepSlateTertiary
import com.example.ui.theme.EcoGreenSecondary
import com.example.ui.theme.SolarGoldPrimary
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

data class Campus3DBuilding(
    val id: String,
    val name: String,
    val category: String,
    val description: String,
    // Relative coordinate percentages on the 3D map (0f to 1f)
    val normX: Float,
    val normY: Float,
    val etaMinutes: Int,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

private val DbuCampus3DBuildings = listOf(
    Campus3DBuilding(
        id = "library",
        name = "DBU Student Library (Kalam Block)",
        category = "Academic & Research",
        description = "Central 3-story digital library, AC reading halls & 24/7 study zone.",
        normX = 0.50f,
        normY = 0.44f,
        etaMinutes = 2,
        icon = Icons.Default.School
    ),
    Campus3DBuilding(
        id = "engineering",
        name = "Faculty of Engineering & Computing",
        category = "Laboratories & Lectures",
        description = "Advanced robotics, computer center, AI research lab & workshops.",
        normX = 0.16f,
        normY = 0.32f,
        etaMinutes = 3,
        icon = Icons.Default.School
    ),
    Campus3DBuilding(
        id = "sports",
        name = "DBU Sports Arena & Stadium",
        category = "Athletics & Recreation",
        description = "Olympic-size gymnasium, 400m track, football turf & badminton courts.",
        normX = 0.76f,
        normY = 0.38f,
        etaMinutes = 4,
        icon = Icons.Default.SportsSoccer
    ),
    Campus3DBuilding(
        id = "business",
        name = "Business School & Management",
        category = "Commerce & Management",
        description = "Seminar auditoriums, MBA lecture halls & incubation cell.",
        normX = 0.32f,
        normY = 0.14f,
        etaMinutes = 3,
        icon = Icons.Default.School
    ),
    Campus3DBuilding(
        id = "cafeteria",
        name = "Student Cafeteria & Food Court",
        category = "Dining & Refreshments",
        description = "Multi-cuisine campus dining, coffee bars & open-air terrace.",
        normX = 0.88f,
        normY = 0.72f,
        etaMinutes = 2,
        icon = Icons.Default.Place
    ),
    Campus3DBuilding(
        id = "gate1",
        name = "DBU Main Campus Gate (Estd 2005)",
        category = "Campus Entry & Security",
        description = "Main Grand Trunk Road security portal, visitor pass desk & EV pickup point.",
        normX = 0.28f,
        normY = 0.76f,
        etaMinutes = 1,
        icon = Icons.Default.NearMe
    )
)

@Composable
fun Globe3DCanvas(
    modifier: Modifier = Modifier,
    userLatitude: Double = CampusData.DBU_LATITUDE,
    userLongitude: Double = CampusData.DBU_LONGITUDE,
    onNavigateBackToMap: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()

    // Smooth transform states: scale (zoom), pan offsets (X/Y)
    var zoomScale by remember { mutableFloatStateOf(1.2f) }
    var panOffsetX by remember { mutableFloatStateOf(0f) }
    var panOffsetY by remember { mutableFloatStateOf(0f) }

    // Layer toggles
    var showBuildingPins by remember { mutableStateOf(true) }
    var showEvTraffic by remember { mutableStateOf(true) }
    var selectedBuilding by remember { mutableStateOf<Campus3DBuilding?>(DbuCampus3DBuildings[0]) }

    // Animated beacon pulse for landmarks
    val infiniteTransition = rememberInfiniteTransition(label = "pulse3d")
    val beaconPulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "beaconPulse"
    )

    // Moving EV scooters on campus avenues
    val trafficProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(7000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "trafficProgress"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
    ) {
        // ==========================================
        // 1. Interactive 3D Satellite & Street Canvas Container
        // ==========================================
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        zoomScale = (zoomScale * zoom).coerceIn(1.0f, 4.0f)
                        val maxPan = 600f * (zoomScale - 0.8f)
                        panOffsetX = (panOffsetX + pan.x).coerceIn(-maxPan, maxPan)
                        panOffsetY = (panOffsetY + pan.y).coerceIn(-maxPan, maxPan)
                    }
                }
                .testTag("interactive_3d_google_earth_canvas")
        ) {
            // High-resolution 3D Isometric Satellite Campus Map
            Image(
                painter = painterResource(id = R.drawable.img_dbu_campus_3d),
                contentDescription = "Desh Bhagat University 3D Satellite Earth",
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = zoomScale
                        scaleY = zoomScale
                        translationX = panOffsetX
                        translationY = panOffsetY
                    },
                contentScale = ContentScale.Crop
            )

            // Dynamic 3D Building Pins & Traffic Layer (Synchronized with zoom/pan)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = zoomScale
                        scaleY = zoomScale
                        translationX = panOffsetX
                        translationY = panOffsetY
                    }
            ) {
                // Interactive 3D Building Pinpoints
                if (showBuildingPins) {
                    DbuCampus3DBuildings.forEach { building ->
                        val isSelected = selectedBuilding?.id == building.id
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .offset {
                                    // Scale normalized coordinates according to canvas dimensions
                                    // Default base reference 1080x1920
                                    IntOffset(
                                        x = (building.normX * 1000).dp.roundToPx(),
                                        y = (building.normY * 1600).dp.roundToPx()
                                    )
                                }
                                .clickable { selectedBuilding = building }
                                .testTag("pin_${building.id}")
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                // Pin head with icon & pulse
                                Box(
                                    modifier = Modifier
                                        .size(if (isSelected) 42.dp else 32.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) SolarGoldPrimary else Color.White)
                                        .border(2.dp, if (isSelected) DeepSlateTertiary else SolarGoldPrimary, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = building.icon,
                                        contentDescription = building.name,
                                        tint = DeepSlateTertiary,
                                        modifier = Modifier.size(if (isSelected) 22.dp else 16.dp)
                                    )
                                }

                                // Building Name Label Tag
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) DeepSlateTertiary else Color.White.copy(alpha = 0.92f),
                                    shadowElevation = 4.dp,
                                    modifier = Modifier.padding(top = 2.dp)
                                ) {
                                    Text(
                                        text = building.name.split("(").first().trim(),
                                        fontSize = if (isSelected) 11.sp else 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) SolarGoldPrimary else DeepSlateTertiary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Live Moving EV Scooters on Campus Streets
                if (showEvTraffic) {
                    // Scooter 1: Main avenue from Gate to Library
                    val sc1X = (0.28f + (0.50f - 0.28f) * trafficProgress) * 1000
                    val sc1Y = (0.76f + (0.44f - 0.76f) * trafficProgress) * 1600
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .offset { IntOffset(sc1X.dp.roundToPx(), sc1Y.dp.roundToPx()) }
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(SolarGoldPrimary)
                            .border(1.5.dp, DeepSlateTertiary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ElectricScooter, contentDescription = null, tint = DeepSlateTertiary, modifier = Modifier.size(14.dp))
                    }

                    // Scooter 2: Library to Engineering block
                    val sc2X = (0.50f + (0.16f - 0.50f) * ((trafficProgress + 0.5f) % 1f)) * 1000
                    val sc2Y = (0.44f + (0.32f - 0.44f) * ((trafficProgress + 0.5f) % 1f)) * 1600
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .offset { IntOffset(sc2X.dp.roundToPx(), sc2Y.dp.roundToPx()) }
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(EcoGreenSecondary)
                            .border(1.5.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ElectricScooter, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }

        // ==========================================
        // 2. Top App Bar & Satellite Status Overlay
        // ==========================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.96f)),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFE2E8F0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onNavigateBackToMap,
                            modifier = Modifier.testTag("back_to_map_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DeepSlateTertiary)
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "DBU 3D GOOGLE EARTH",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Black,
                                    color = DeepSlateTertiary,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(shape = RoundedCornerShape(9999.dp), color = Color(0xFFD1FAE5)) {
                                    Text("3D LIVE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF065F46), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                            Text(
                                text = "Desh Bhagat University • Mandi Gobindgarh / Patiala (30.6062° N)",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF64748B),
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Reset Center button
                    IconButton(
                        onClick = {
                            zoomScale = 1.2f
                            panOffsetX = 0f
                            panOffsetY = 0f
                        },
                        modifier = Modifier.testTag("recenter_earth_button")
                    ) {
                        Icon(Icons.Default.CenterFocusStrong, contentDescription = "Recenter", tint = SolarGoldPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Fly-To Landmark Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DbuCampus3DBuildings.forEach { building ->
                    val isSelected = selectedBuilding?.id == building.id
                    Surface(
                        shape = RoundedCornerShape(9999.dp),
                        color = if (isSelected) SolarGoldPrimary else Color.White.copy(alpha = 0.92f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) DeepSlateTertiary else Color(0xFFCBD5E1)),
                        modifier = Modifier
                            .clickable {
                                selectedBuilding = building
                                zoomScale = 1.8f
                                // Pan toward building position
                                panOffsetX = (0.5f - building.normX) * 450f
                                panOffsetY = (0.5f - building.normY) * 600f
                            }
                            .testTag("chip_fly_${building.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(building.icon, contentDescription = null, tint = DeepSlateTertiary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = building.name.split("(").first().trim(),
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                color = DeepSlateTertiary
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // 3. Floating Zoom & Layer Controls (Right side)
        // ==========================================
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Zoom In (+)
            IconButton(
                onClick = { zoomScale = (zoomScale + 0.4f).coerceAtMost(4.0f) },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                    .testTag("zoom_in_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = DeepSlateTertiary)
            }

            // Zoom Out (-)
            IconButton(
                onClick = { zoomScale = (zoomScale - 0.4f).coerceAtLeast(1.0f) },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                    .testTag("zoom_out_button")
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Zoom Out", tint = DeepSlateTertiary)
            }

            // Toggle Building Labels
            IconButton(
                onClick = { showBuildingPins = !showBuildingPins },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (showBuildingPins) SolarGoldPrimary else Color.White)
                    .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                    .testTag("toggle_labels_button")
            ) {
                Icon(Icons.Default.Layers, contentDescription = "Toggle Labels", tint = DeepSlateTertiary)
            }

            // Toggle EV Scooters on streets
            IconButton(
                onClick = { showEvTraffic = !showEvTraffic },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (showEvTraffic) EcoGreenSecondary else Color.White)
                    .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                    .testTag("toggle_traffic_button")
            ) {
                Icon(Icons.Default.DirectionsBike, contentDescription = "Toggle Traffic", tint = if (showEvTraffic) Color.White else DeepSlateTertiary)
            }
        }

        // ==========================================
        // 4. Bottom Selected Landmark Details HUD
        // ==========================================
        selectedBuilding?.let { building ->
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp)
                    .testTag("selected_building_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, SolarGoldPrimary),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(EcoGreenSecondary)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = building.category.uppercase(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EcoGreenSecondary
                                )
                            }
                            Text(
                                text = building.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = DeepSlateTertiary
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(9999.dp),
                            color = Color(0xFFFEF3C7)
                        ) {
                            Text(
                                text = "⚡ ${building.etaMinutes} min pickup",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = building.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onNavigateBackToMap,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).height(48.dp)
                        ) {
                            Text("Campus Map", color = DeepSlateTertiary, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onNavigateBackToMap,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SolarGoldPrimary, contentColor = DeepSlateTertiary),
                            modifier = Modifier.weight(1.5f).height(48.dp).testTag("book_to_building_button")
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Book Ride Here • ₹10", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
