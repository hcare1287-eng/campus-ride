package com.example.data.model

data class CampusLandmark(
    val id: String,
    val name: String,
    val address: String,
    val category: String,
    val latitude: Double,
    val longitude: Double,
    val tag: String = "Popular"
)

data class RapidoService(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconType: String,
    val baseFare: Long,
    val etaMinutes: Int,
    val discountPercent: Int = 0
)

data class CampusShortcut(
    val id: String,
    val pickup: String,
    val dropoff: String,
    val bikeTimeMinutes: Int,
    val walkTimeMinutes: Int,
    val defaultFare: Long,
    val co2SavedGrams: Long,
    val category: String = "DBU Campus Route"
)

data class VehicleOption(
    val name: String,
    val tagline: String,
    val etaMinutes: Int,
    val baseFare: Long,
    val co2Saved: String,
    val vehicleIcon: String,
    val helmetRequired: Boolean
)

object CampusData {
    // Desh Bhagat University (DBU) & Patiala / Mandi Gobindgarh Region
    const val DBU_LATITUDE = 30.6062
    const val DBU_LONGITUDE = 76.3117
    const val DBU_CAMPUS_NAME = "Desh Bhagat University (DBU)"
    const val DEFAULT_PICKUP_ADDRESS = "154, Urban Estate Phase II, Patiala, Punjab"

    val RecentSearches = listOf(
        CampusLandmark(
            id = "ion_digital",
            name = "iON Digital Zone",
            address = "Rajpura Road, Bahadurgarh, Patiala, Punjab, India",
            category = "Exam Center",
            latitude = 30.3725,
            longitude = 76.4520
        ),
        CampusLandmark(
            id = "thapar_univ",
            name = "Thapar University",
            address = "Prem Nagar, Patiala, Punjab, India",
            category = "University",
            latitude = 30.3533,
            longitude = 76.3688
        ),
        CampusLandmark(
            id = "railway_patiala",
            name = "Railway Station Patiala",
            address = "Power House Colony, Rajpura Colony, Patiala, Punjab",
            category = "Transit",
            latitude = 30.3340,
            longitude = 76.3980
        ),
        CampusLandmark(
            id = "gurudwara_dukhniwaran",
            name = "Gurudwara Shri Dukhniwaran Sahib",
            address = "Patiala, Sirhind - Patiala Road, Factory Area, Punjab",
            category = "Spiritual",
            latitude = 30.3420,
            longitude = 76.3890
        ),
        CampusLandmark(
            id = "dbu_library",
            name = "DBU Central Library (Kalam Block)",
            address = "Amloh Road, Desh Bhagat University Campus",
            category = "Academic",
            latitude = 30.6068,
            longitude = 76.3115
        ),
        CampusLandmark(
            id = "dbu_boys_hostel",
            name = "S. Lal Singh Boys Hostel",
            address = "Resident Student Hostel Blocks A & B, DBU",
            category = "Hostel",
            latitude = 30.6040,
            longitude = 76.3100
        )
    )

    val PopularPlaces = listOf(
        CampusLandmark(
            id = "p1",
            name = "Thapar University",
            address = "Prem Nagar, Patiala",
            category = "University",
            latitude = 30.3533,
            longitude = 76.3688
        ),
        CampusLandmark(
            id = "p2",
            name = "Railway Station Patiala",
            address = "Station Road, Patiala",
            category = "Transit",
            latitude = 30.3340,
            longitude = 76.3980
        ),
        CampusLandmark(
            id = "p3",
            name = "Gurudwara Shri Dukhniwaran",
            address = "Sirhind Road, Patiala",
            category = "Spiritual",
            latitude = 30.3420,
            longitude = 76.3890
        ),
        CampusLandmark(
            id = "p4",
            name = "Punjabi University",
            address = "NH 64, Patiala",
            category = "University",
            latitude = 30.3580,
            longitude = 76.4440
        ),
        CampusLandmark(
            id = "p5",
            name = "Rajindra Hospital Patiala",
            address = "New Lal Bagh, Patiala",
            category = "Hospital",
            latitude = 30.3290,
            longitude = 76.3800
        ),
        CampusLandmark(
            id = "p6",
            name = "DBU Kalam Block",
            address = "Desh Bhagat University, Mandi Gobindgarh",
            category = "Campus",
            latitude = 30.6068,
            longitude = 76.3115
        )
    )

    val RapidoServicesList = listOf(
        RapidoService(
            id = "parcel_bike",
            title = "Parcel on Bike",
            subtitle = "Send books & items",
            iconType = "parcel",
            baseFare = 25L,
            etaMinutes = 3
        ),
        RapidoService(
            id = "auto",
            title = "Auto",
            subtitle = "Doorstep 3-wheeler",
            iconType = "auto",
            baseFare = 30L,
            etaMinutes = 4
        ),
        RapidoService(
            id = "cab_economy",
            title = "Cab Economy",
            subtitle = "Affordable AC car",
            iconType = "cab",
            baseFare = 60L,
            etaMinutes = 5
        ),
        RapidoService(
            id = "bike",
            title = "Bike",
            subtitle = "Fastest campus ride",
            iconType = "bike",
            baseFare = 15L,
            etaMinutes = 2
        ),
        RapidoService(
            id = "bike_lite",
            title = "Bike Lite",
            subtitle = "Pocket friendly EV",
            iconType = "bike_lite",
            baseFare = 12L,
            etaMinutes = 2,
            discountPercent = 20
        ),
        RapidoService(
            id = "travel",
            title = "Travel",
            subtitle = "Outstation & Shuttle",
            iconType = "travel",
            baseFare = 120L,
            etaMinutes = 10
        )
    )

    val Vehicles = listOf(
        VehicleOption(
            name = "Bike",
            tagline = "Fastest • Beat campus traffic",
            etaMinutes = 2,
            baseFare = 15L,
            co2Saved = "420g CO2 saved",
            vehicleIcon = "bike",
            helmetRequired = true
        ),
        VehicleOption(
            name = "Bike Lite",
            tagline = "Most Affordable • Campus EV",
            etaMinutes = 3,
            baseFare = 12L,
            co2Saved = "560g CO2 saved",
            vehicleIcon = "bike_lite",
            helmetRequired = true
        ),
        VehicleOption(
            name = "Auto",
            tagline = "Comfy 3-Wheeler • Up to 3 seats",
            etaMinutes = 4,
            baseFare = 30L,
            co2Saved = "300g CO2 saved",
            vehicleIcon = "auto",
            helmetRequired = false
        ),
        VehicleOption(
            name = "Cab Economy",
            tagline = "AC Comfort • Campus to Station",
            etaMinutes = 5,
            baseFare = 65L,
            co2Saved = "200g CO2 saved",
            vehicleIcon = "cab",
            helmetRequired = false
        ),
        VehicleOption(
            name = "Parcel on Bike",
            tagline = "Fast Campus Delivery • Documents & Tiffin",
            etaMinutes = 3,
            baseFare = 20L,
            co2Saved = "350g CO2 saved",
            vehicleIcon = "parcel",
            helmetRequired = false
        )
    )

    val Shortcuts = listOf(
        CampusShortcut(
            id = "c1",
            pickup = "154, Urban Estate Phase II",
            dropoff = "Thapar University",
            bikeTimeMinutes = 5,
            walkTimeMinutes = 20,
            defaultFare = 25L,
            co2SavedGrams = 450L
        ),
        CampusShortcut(
            id = "c2",
            pickup = "S. Lal Singh Boys Hostel",
            dropoff = "DBU Central Library (Kalam Block)",
            bikeTimeMinutes = 3,
            walkTimeMinutes = 11,
            defaultFare = 15L,
            co2SavedGrams = 380L
        ),
        CampusShortcut(
            id = "c3",
            pickup = "Mata Jaspal Kaur Girls Hostel",
            dropoff = "Faculty of Eng. & Computing",
            bikeTimeMinutes = 4,
            walkTimeMinutes = 14,
            defaultFare = 15L,
            co2SavedGrams = 440L
        ),
        CampusShortcut(
            id = "c4",
            pickup = "DBU Main Gate Entrance",
            dropoff = "Railway Station Patiala",
            bikeTimeMinutes = 12,
            walkTimeMinutes = 45,
            defaultFare = 40L,
            co2SavedGrams = 680L
        )
    )

    val DbuLandmarks = RecentSearches

    val DbuFaculties = listOf(
        "Faculty of Engineering & Technology",
        "Desh Bhagat Dental College & Hospital",
        "Faculty of Nursing",
        "Faculty of Pharmacy",
        "Faculty of Management & Computer Applications",
        "Faculty of Ayurveda & Research",
        "Faculty of Education & Social Sciences",
        "Faculty of Hotel Management & Tourism"
    )

    val DbuResidencies = listOf(
        "154, Urban Estate Phase II, Patiala",
        "S. Lal Singh Boys Hostel (Block A)",
        "S. Lal Singh Boys Hostel (Block B)",
        "Mata Jaspal Kaur Girls Hostel",
        "Day Scholar - Mandi Gobindgarh",
        "Day Scholar - Khanna / Sirhind",
        "Day Scholar - Amloh / Patiala"
    )
}
