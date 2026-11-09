package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CampusData
import com.example.data.model.Ride
import com.example.ui.theme.DeepSlateTertiary
import com.example.ui.theme.EcoGreenSecondary
import com.example.ui.theme.SolarGoldPrimary

@Composable
fun CampusMapCanvas(
    modifier: Modifier = Modifier,
    activeRide: Ride? = null,
    is3dViewEnabled: Boolean = false,
    userLatitude: Double = CampusData.DBU_LATITUDE,
    userLongitude: Double = CampusData.DBU_LONGITUDE,
    nearestLandmark: String = "154, Urban Estate Phase II",
    onMapTapped: (String) -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 14f,
        targetValue = 38f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseRadius"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )

    val vehicleProgress by infiniteTransition.animateFloat(
        initialValue = 0.05f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(3800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "vehicleProgress"
    )

    Box(modifier = modifier.background(Color(0xFFF4F3ED))) {
        if (is3dViewEnabled) {
            // 3D Campus Digital Twin
            Image(
                painter = painterResource(id = R.drawable.img_dbu_campus_3d),
                contentDescription = "DBU 3D Campus",
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            if (offset.y < size.height * 0.4f) {
                                onMapTapped("DBU Central Library (Kalam Block)")
                            } else {
                                onMapTapped("S. Lal Singh Boys Hostel")
                            }
                        }
                    },
                contentScale = ContentScale.Crop
            )
        } else {
            // Rapido Style Vector Street Map Canvas
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            if (offset.y < size.height * 0.45f) {
                                onMapTapped("Thapar University")
                            } else {
                                onMapTapped("Railway Station Patiala")
                            }
                        }
                    }
            ) {
                val w = size.width
                val h = size.height

                // Map base background (Rapido warm street map beige)
                drawRect(color = Color(0xFFF3F1E8), size = size)

                // City Blocks / Parcels (pale gray-white)
                drawRoundRect(
                    color = Color(0xFFFAF9F4),
                    topLeft = Offset(w * 0.05f, h * 0.08f),
                    size = Size(w * 0.42f, h * 0.18f),
                    cornerRadius = CornerRadius(12f, 12f)
                )
                drawRoundRect(
                    color = Color(0xFFFAF9F4),
                    topLeft = Offset(w * 0.52f, h * 0.06f),
                    size = Size(w * 0.44f, h * 0.22f),
                    cornerRadius = CornerRadius(12f, 12f)
                )
                drawRoundRect(
                    color = Color(0xFFE8F5E9), // Green park zone
                    topLeft = Offset(w * 0.06f, h * 0.32f),
                    size = Size(w * 0.38f, h * 0.18f),
                    cornerRadius = CornerRadius(16f, 16f)
                )
                drawRoundRect(
                    color = Color(0xFFFAF9F4),
                    topLeft = Offset(w * 0.50f, h * 0.34f),
                    size = Size(w * 0.46f, h * 0.22f),
                    cornerRadius = CornerRadius(12f, 12f)
                )

                // Secondary roads
                val secRoadColor = Color.White
                drawLine(
                    color = secRoadColor,
                    start = Offset(0f, h * 0.28f),
                    end = Offset(w, h * 0.28f),
                    strokeWidth = 26f
                )
                drawLine(
                    color = secRoadColor,
                    start = Offset(0f, h * 0.54f),
                    end = Offset(w, h * 0.54f),
                    strokeWidth = 24f
                )
                drawLine(
                    color = secRoadColor,
                    start = Offset(w * 0.47f, 0f),
                    end = Offset(w * 0.47f, h),
                    strokeWidth = 26f
                )

                // Main Highway / GT Road with yellow centerline
                val mainRoadColor = Color.White
                val roadOutlineColor = Color(0xFFE2DFD2)

                val highwayPath = Path().apply {
                    moveTo(0f, h * 0.40f)
                    cubicTo(w * 0.3f, h * 0.36f, w * 0.7f, h * 0.44f, w, h * 0.39f)
                }
                drawPath(highwayPath, roadOutlineColor, style = Stroke(width = 44f))
                drawPath(highwayPath, mainRoadColor, style = Stroke(width = 40f))
                drawPath(
                    highwayPath,
                    Color(0xFFFBBF24),
                    style = Stroke(width = 3f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 16f)))
                )

                // Diagonal Avenue
                drawLine(
                    color = mainRoadColor,
                    start = Offset(w * 0.15f, 0f),
                    end = Offset(w * 0.85f, h),
                    strokeWidth = 32f
                )

                // Live Rapido Captains / Peer Bikes on Roads
                drawCaptainVehicle(Offset(w * 0.32f, h * 0.39f), SolarGoldPrimary)
                drawCaptainVehicle(Offset(w * 0.68f, h * 0.41f), SolarGoldPrimary)
                drawCaptainVehicle(Offset(w * 0.47f, h * 0.22f), EcoGreenSecondary)
                drawCaptainVehicle(Offset(w * 0.47f, h * 0.62f), SolarGoldPrimary)

                // Pickup Point (Green pin + speech bubble at center)
                val pickupPt = Offset(w * 0.47f, h * 0.36f)
                val dropPt = Offset(w * 0.76f, h * 0.22f)

                // Pulse circle
                drawCircle(color = EcoGreenSecondary.copy(alpha = pulseAlpha), radius = pulseRadius, center = pickupPt)
                drawCircle(color = Color.White, radius = 10f, center = pickupPt)
                drawCircle(color = Color(0xFF059669), radius = 6f, center = pickupPt)

                // Route trajectory when active ride is present
                if (activeRide != null && activeRide.status != "completed" && activeRide.status != "cancelled") {
                    val routePath = Path().apply {
                        moveTo(pickupPt.x, pickupPt.y)
                        cubicTo(w * 0.55f, h * 0.34f, w * 0.65f, h * 0.28f, dropPt.x, dropPt.y)
                    }
                    drawPath(routePath, Color(0x33059669), style = Stroke(width = 16f))
                    drawPath(
                        routePath,
                        Color(0xFF059669),
                        style = Stroke(width = 6f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 12f)))
                    )

                    // Moving Captain Bike icon
                    val curX = pickupPt.x + (dropPt.x - pickupPt.x) * vehicleProgress
                    val curY = pickupPt.y + (dropPt.y - pickupPt.y) * vehicleProgress
                    drawCircle(color = DeepSlateTertiary, radius = 16f, center = Offset(curX, curY))
                    drawCircle(color = SolarGoldPrimary, radius = 11f, center = Offset(curX, curY))

                    // Destination Drop pin (Orange)
                    drawCircle(color = Color(0xFFEA580C).copy(alpha = 0.3f), radius = 16f, center = dropPt)
                    drawCircle(color = Color(0xFFEA580C), radius = 9f, center = dropPt)
                    drawCircle(color = Color.White, radius = 4f, center = dropPt)
                }
            }

            // Floating "Pickup Point" Speech Bubble on top of the green pin
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(bottom = 90.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF059669),
                    shadowElevation = 4.dp
                ) {
                    Text(
                        text = "Pickup Point",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawCaptainVehicle(center: Offset, color: Color) {
    drawCircle(color = Color(0x25000000), radius = 14f, center = Offset(center.x + 2f, center.y + 2f))
    drawCircle(color = Color.White, radius = 13f, center = center)
    drawCircle(color = color, radius = 9f, center = center)
    drawCircle(color = DeepSlateTertiary, radius = 4f, center = center)
}
