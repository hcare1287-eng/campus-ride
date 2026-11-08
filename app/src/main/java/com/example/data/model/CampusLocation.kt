package com.example.data.model

data class CampusShortcut(
    val id: String,
    val pickup: String,
    val dropoff: String,
    val bikeTimeMinutes: Int,
    val walkTimeMinutes: Int,
    val defaultFare: Long,
    val co2SavedGrams: Long,
    val category: String = "Hostel to Dept"
)

object CampusData {
    val Shortcuts = listOf(
        CampusShortcut(
            id = "c1",
            pickup = "Hostel Block 4 (Kaveri)",
            dropoff = "Central Library",
            bikeTimeMinutes = 4,
            walkTimeMinutes = 12,
            defaultFare = 15L,
            co2SavedGrams = 420L
        ),
        CampusShortcut(
            id = "c2",
            pickup = "Hostel 7 (Godavari)",
            dropoff = "CS & Tech Annex",
            bikeTimeMinutes = 3,
            walkTimeMinutes = 10,
            defaultFare = 10L,
            co2SavedGrams = 350L
        ),
        CampusShortcut(
            id = "c3",
            pickup = "North Gate Entry",
            dropoff = "Main Auditorium",
            bikeTimeMinutes = 5,
            walkTimeMinutes = 18,
            defaultFare = 20L,
            co2SavedGrams = 580L
        ),
        CampusShortcut(
            id = "c4",
            pickup = "Sports Complex & Gym",
            dropoff = "Cafeteria Circle",
            bikeTimeMinutes = 2,
            walkTimeMinutes = 7,
            defaultFare = 10L,
            co2SavedGrams = 240L
        ),
        CampusShortcut(
            id = "c5",
            pickup = "Girls Hostel Block 2",
            dropoff = "Mechanical & Robotics Labs",
            bikeTimeMinutes = 4,
            walkTimeMinutes = 14,
            defaultFare = 15L,
            co2SavedGrams = 460L
        )
    )

    val Vehicles = listOf(
        VehicleOption(
            name = "Campus EV Scooter",
            tagline = "Fastest peer pickup • Zero emissions",
            etaMinutes = 2,
            baseFare = 15L,
            co2Saved = "420g CO2 saved",
            vehicleIcon = "scooter",
            helmetRequired = true
        ),
        VehicleOption(
            name = "Peer Campus Bike",
            tagline = "Shared campus cycle • Eco-hero",
            etaMinutes = 3,
            baseFare = 10L,
            co2Saved = "560g CO2 saved",
            vehicleIcon = "bike",
            helmetRequired = false
        ),
        VehicleOption(
            name = "Speed E-Bicycle",
            tagline = "Electric pedal assist • Fast cruise",
            etaMinutes = 2,
            baseFare = 12L,
            co2Saved = "500g CO2 saved",
            vehicleIcon = "ebike",
            helmetRequired = true
        )
    )
}

data class VehicleOption(
    val name: String,
    val tagline: String,
    val etaMinutes: Int,
    val baseFare: Long,
    val co2Saved: String,
    val vehicleIcon: String,
    val helmetRequired: Boolean
)
