package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import com.example.data.model.Ride
import com.example.ui.theme.DeepSlateTertiary
import com.example.ui.theme.EcoGreenSecondary
import com.example.ui.theme.SolarGoldPrimary

@Composable
fun CampusMapCanvas(
    modifier: Modifier = Modifier,
    activeRide: Ride? = null,
    onMapTapped: (String) -> Unit = {}
) {
    // Pulse animation for active rider GPS pin
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 12f,
        targetValue = 28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseRadius"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )

    // Moving scooter progress along route
    val vehicleProgress by infiniteTransition.animateFloat(
        initialValue = 0.05f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "vehicleProgress"
    )

    Box(modifier = modifier.background(Color(0xFFF1F5F9))) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        // Detect tap near landmarks
                        if (offset.y < size.height * 0.45f) {
                            onMapTapped("Hostel Zone")
                        } else {
                            onMapTapped("Academic Complex")
                        }
                    }
                }
        ) {
            val w = size.width
            val h = size.height

            // 1. Draw Campus Grass Quads & Lawns
            drawRect(
                color = Color(0xFFF8FAFC),
                size = size
            )

            // North Garden Quad
            drawRoundRect(
                color = Color(0xFFE8F5E9),
                topLeft = Offset(w * 0.08f, h * 0.08f),
                size = Size(w * 0.38f, h * 0.22f),
                cornerRadius = CornerRadius(24f, 24f)
            )

            // Central University Lawn
            drawRoundRect(
                color = Color(0xFFE2F3E7),
                topLeft = Offset(w * 0.52f, h * 0.28f),
                size = Size(w * 0.40f, h * 0.25f),
                cornerRadius = CornerRadius(30f, 30f)
            )

            // Sports Oval
            drawOval(
                color = Color(0xFFFEF3C7).copy(alpha = 0.6f),
                topLeft = Offset(w * 0.08f, h * 0.62f),
                size = Size(w * 0.36f, h * 0.22f)
            )

            // 2. Campus Roads & Avenues
            val roadColor = Color(0xFFE2E8F0)
            val roadStripe = Color(0xFFCBD5E1)
            val strokeWidth = 32f

            // Main Campus Ring Road
            val mainRoad = Path().apply {
                moveTo(w * 0.05f, h * 0.35f)
                cubicTo(w * 0.3f, h * 0.32f, w * 0.7f, h * 0.38f, w * 0.95f, h * 0.35f)
                lineTo(w * 0.95f, h * 0.58f)
                cubicTo(w * 0.6f, h * 0.62f, w * 0.4f, h * 0.54f, w * 0.05f, h * 0.58f)
                close()
            }
            drawPath(mainRoad, roadColor, style = Stroke(width = strokeWidth))

            // Avenue Cross Roads
            drawLine(
                color = roadColor,
                start = Offset(w * 0.48f, 0f),
                end = Offset(w * 0.48f, h),
                strokeWidth = 26f
            )
            drawLine(
                color = roadStripe,
                start = Offset(w * 0.48f, 0f),
                end = Offset(w * 0.48f, h),
                strokeWidth = 3f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 16f))
            )

            // 3. Campus Buildings (Hostels, Departments, Library)
            // Hostel Kaveri (Block 4)
            drawCampusBuilding(
                topLeft = Offset(w * 0.12f, h * 0.16f),
                size = Size(w * 0.28f, h * 0.10f),
                name = "Hostel 4 (Kaveri)",
                tag = "HOSTEL"
            )

            // Central Library
            drawCampusBuilding(
                topLeft = Offset(w * 0.58f, h * 0.14f),
                size = Size(w * 0.34f, h * 0.11f),
                name = "Central Library",
                tag = "LIBRARY"
            )

            // CS & Tech Annex
            drawCampusBuilding(
                topLeft = Offset(w * 0.55f, h * 0.42f),
                size = Size(w * 0.36f, h * 0.11f),
                name = "CS & Tech Annex",
                tag = "ACADEMIC"
            )

            // Sports Complex & Cafeteria
            drawCampusBuilding(
                topLeft = Offset(w * 0.12f, h * 0.48f),
                size = Size(w * 0.30f, h * 0.10f),
                name = "Cafeteria & Hub",
                tag = "AMENITY"
            )

            // 4. Live Peer Driver / Scooter Pins on Campus Roads
            drawPeerVehicle(Offset(w * 0.30f, h * 0.35f), "EV-02", SolarGoldPrimary)
            drawPeerVehicle(Offset(w * 0.72f, h * 0.36f), "EV-04", SolarGoldPrimary)
            drawPeerVehicle(Offset(w * 0.48f, h * 0.22f), "BIKE-09", EcoGreenSecondary)
            drawPeerVehicle(Offset(w * 0.48f, h * 0.68f), "EV-11", SolarGoldPrimary)

            // 5. Active Ride Route & Tracking Line (if ride exists)
            val pickupPt = Offset(w * 0.26f, h * 0.21f)
            val dropPt = Offset(w * 0.75f, h * 0.19f)

            if (activeRide != null && activeRide.status != "completed" && activeRide.status != "cancelled") {
                // Route navigation arc
                val routePath = Path().apply {
                    moveTo(pickupPt.x, pickupPt.y)
                    cubicTo(w * 0.45f, h * 0.34f, w * 0.55f, h * 0.34f, dropPt.x, dropPt.y)
                }

                // Route shadow & glow
                drawPath(
                    routePath,
                    SolarGoldPrimary.copy(alpha = 0.3f),
                    style = Stroke(width = 14f)
                )
                // Active route line
                drawPath(
                    routePath,
                    SolarGoldPrimary,
                    style = Stroke(
                        width = 6f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(24f, 12f))
                    )
                )

                // Animated in-route scooter vehicle position
                val currentVehicleX = pickupPt.x + (dropPt.x - pickupPt.x) * vehicleProgress
                val currentVehicleY = pickupPt.y + (dropPt.y - pickupPt.y) * vehicleProgress +
                        kotlin.math.sin(vehicleProgress * Math.PI).toFloat() * 30f

                // Draw moving vehicle marker
                drawCircle(
                    color = DeepSlateTertiary,
                    radius = 16f,
                    center = Offset(currentVehicleX, currentVehicleY)
                )
                drawCircle(
                    color = SolarGoldPrimary,
                    radius = 12f,
                    center = Offset(currentVehicleX, currentVehicleY)
                )
            }

            // 6. User GPS Location Marker (Pulsing Dot)
            drawCircle(
                color = SolarGoldPrimary.copy(alpha = pulseAlpha),
                radius = pulseRadius,
                center = pickupPt
            )
            drawCircle(
                color = Color.White,
                radius = 11f,
                center = pickupPt
            )
            drawCircle(
                color = DeepSlateTertiary,
                radius = 7f,
                center = pickupPt
            )

            // Drop location target pin
            drawCircle(
                color = EcoGreenSecondary.copy(alpha = 0.3f),
                radius = 18f,
                center = dropPt
            )
            drawCircle(
                color = EcoGreenSecondary,
                radius = 9f,
                center = dropPt
            )
        }
    }
}

private fun DrawScope.drawCampusBuilding(
    topLeft: Offset,
    size: Size,
    name: String,
    tag: String
) {
    // Drop shadow
    drawRoundRect(
        color = Color(0x150F172A),
        topLeft = Offset(topLeft.x + 3f, topLeft.y + 4f),
        size = size,
        cornerRadius = CornerRadius(16f, 16f)
    )
    // Building structure
    drawRoundRect(
        color = Color.White,
        topLeft = topLeft,
        size = size,
        cornerRadius = CornerRadius(16f, 16f)
    )
    // Accent roof border
    drawRoundRect(
        color = Color(0xFFCBD5E1),
        topLeft = topLeft,
        size = size,
        cornerRadius = CornerRadius(16f, 16f),
        style = Stroke(width = 2f)
    )
}

private fun DrawScope.drawPeerVehicle(center: Offset, tag: String, color: Color) {
    // Subtle shadow
    drawCircle(
        color = Color(0x20000000),
        radius = 14f,
        center = Offset(center.x + 2f, center.y + 2f)
    )
    // Outer rim
    drawCircle(
        color = Color.White,
        radius = 13f,
        center = center
    )
    // Inner vibrant core
    drawCircle(
        color = color,
        radius = 9f,
        center = center
    )
}
