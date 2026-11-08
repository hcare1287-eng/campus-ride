package com.example

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.Ride
import com.example.data.repository.CampusRideRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.util.UUID

class CampusRideRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun createRide_authenticatedUser_success() = runBlocking {
        signInTestUser("alice@campus.edu")
        val repo = CampusRideRepository(firestore)

        val ride = Ride(
            riderName = "Alice Sharma",
            pickupLocation = "Hostel 4",
            dropLocation = "Central Library",
            fare = 15L,
            otp = "4412",
            status = "requested"
        )

        val result = withTimeout(5000L) {
            repo.bookRide(ride)
        }
        assertTrue("Ride booking should succeed", result.isSuccess)
        assertNotNull(result.getOrNull())
    }

    @Test
    fun observeUserRides_authenticatedUser_returnsRides() = runBlocking {
        signInTestUser("bob@campus.edu")
        val repo = CampusRideRepository(firestore)

        val uniquePickup = "Hostel " + UUID.randomUUID().toString().take(6)
        val ride = Ride(
            riderName = "Bob Mehta",
            pickupLocation = uniquePickup,
            dropLocation = "Auditorium",
            fare = 20L,
            otp = "8812",
            status = "requested"
        )
        repo.bookRide(ride)

        val rides = withTimeout(5000L) {
            repo.observeUserRides().first { list -> list.any { it.pickupLocation == uniquePickup } }
        }
        assertTrue(rides.any { it.pickupLocation == uniquePickup })
    }

    @Test
    fun observeUserRides_unauthenticated_failsWithPermissionDenied() = runBlocking {
        auth.signOut()
        val repo = CampusRideRepository(firestore)

        try {
            withTimeout(3000L) {
                repo.observeUserRides().first()
            }
            fail("Expected exception when unauthenticated")
        } catch (e: Exception) {
            // Expected either IllegalStateException from requireUserId or PERMISSION_DENIED
            assertTrue(e is IllegalStateException || e.unwrapFirestoreException()?.code == FirebaseFirestoreException.Code.PERMISSION_DENIED)
        }
    }
}
