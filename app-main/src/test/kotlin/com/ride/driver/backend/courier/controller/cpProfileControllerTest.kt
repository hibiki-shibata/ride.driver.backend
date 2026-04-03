package com.ride.driver.backend.courier.controller

import com.ride.driver.backend.courier.dto.CourierProfileReqDTO
import com.ride.driver.backend.courier.dto.CourierProfileResDTO
import com.ride.driver.backend.courier.dto.CourierStatusUpdateDTO
import com.ride.driver.backend.courier.dto.CourierTaskHistoryDTO
import com.ride.driver.backend.courier.service.CourierProfileService
import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
import com.ride.driver.backend.shared.model.Coordinate
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class CourierProfileControllerTest {

    private lateinit var courierProfileService: CourierProfileService
    private lateinit var courierProfileController: CourierProfileController

    @BeforeEach
    fun setUp() {
        courierProfileService = mockk()
        courierProfileController = CourierProfileController(courierProfileService)
    }

    @Test
    fun `getCourierProfile should return 200 ok with courier profile`() {
        val courierDetails = mockk<AccessTokenClaim>(relaxed = true)
        val courierProfile = mockk<CourierProfileResDTO>()

        every { courierProfileService.getCourierProfile(courierDetails) } returns courierProfile

        val response = courierProfileController.getCourierProfile(courierDetails)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertSame(courierProfile, response.body)

        verify(exactly = 1) { courierProfileService.getCourierProfile(courierDetails) }
        confirmVerified(courierProfileService)
    }

    @Test
    fun `getCourierProfile should propagate exception when service throws`() {
        val courierDetails = mockk<AccessTokenClaim>(relaxed = true)
        val exception = RuntimeException("profile not found")

        every { courierProfileService.getCourierProfile(courierDetails) } throws exception

        val thrown = assertThrows(RuntimeException::class.java) {
            courierProfileController.getCourierProfile(courierDetails)
        }

        assertEquals("profile not found", thrown.message)

        verify(exactly = 1) { courierProfileService.getCourierProfile(courierDetails) }
        confirmVerified(courierProfileService)
    }

    @Test
    fun `updateCourierProfile should return 200 ok with updated profile`() {
        val request = mockk<CourierProfileReqDTO>()
        val courierDetails = mockk<AccessTokenClaim>(relaxed = true)
        val updatedProfile = mockk<CourierProfileResDTO>()

        every {
            courierProfileService.updateCourierProfile(
                req = request,
                courierDetails = courierDetails
            )
        } returns updatedProfile

        val response = courierProfileController.updateCourierProfile(request, courierDetails)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertSame(updatedProfile, response.body)

        verify(exactly = 1) {
            courierProfileService.updateCourierProfile(
                req = request,
                courierDetails = courierDetails
            )
        }
        confirmVerified(courierProfileService)
    }

    @Test
    fun `updateCourierProfile should propagate exception when service throws`() {
        val request = mockk<CourierProfileReqDTO>()
        val courierDetails = mockk<AccessTokenClaim>(relaxed = true)
        val exception = RuntimeException("update failed")

        every {
            courierProfileService.updateCourierProfile(
                req = request,
                courierDetails = courierDetails
            )
        } throws exception

        val thrown = assertThrows(RuntimeException::class.java) {
            courierProfileController.updateCourierProfile(request, courierDetails)
        }

        assertEquals("update failed", thrown.message)

        verify(exactly = 1) {
            courierProfileService.updateCourierProfile(
                req = request,
                courierDetails = courierDetails
            )
        }
        confirmVerified(courierProfileService)
    }

    @Test
    fun `deleteCourierProfile should return 204 no content`() {
        val courierDetails = mockk<AccessTokenClaim>(relaxed = true)

        every { courierProfileService.deleteCourierProfile(courierDetails) } returns Unit

        val response = courierProfileController.deleteCourierProfile(courierDetails)

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
        assertNull(response.body)

        verify(exactly = 1) { courierProfileService.deleteCourierProfile(courierDetails) }
        confirmVerified(courierProfileService)
    }

    @Test
    fun `deleteCourierProfile should propagate exception when service throws`() {
        val courierDetails = mockk<AccessTokenClaim>(relaxed = true)
        val exception = RuntimeException("delete failed")

        every { courierProfileService.deleteCourierProfile(courierDetails) } throws exception

        val thrown = assertThrows(RuntimeException::class.java) {
            courierProfileController.deleteCourierProfile(courierDetails)
        }

        assertEquals("delete failed", thrown.message)

        verify(exactly = 1) { courierProfileService.deleteCourierProfile(courierDetails) }
        confirmVerified(courierProfileService)
    }

    @Test
    fun `updateLocation should return 200 ok with empty body`() {
        val currentLocation = mockk<Coordinate>()
        val courierDetails = mockk<AccessTokenClaim>(relaxed = true)
        val updatedProfile = mockk<CourierProfileResDTO>()

        every {
            courierProfileService.updateCourierLocation(
                courierDetails = courierDetails,
                newCurrentLocation = currentLocation
            )
        } returns updatedProfile

        val response = courierProfileController.updateLocation(currentLocation, courierDetails)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNull(response.body)

        verify(exactly = 1) {
            courierProfileService.updateCourierLocation(
                courierDetails = courierDetails,
                newCurrentLocation = currentLocation
            )
        }
        confirmVerified(courierProfileService)
    }

    @Test
    fun `updateLocation should propagate exception when service throws`() {
        val currentLocation = mockk<Coordinate>()
        val courierDetails = mockk<AccessTokenClaim>(relaxed = true)
        val exception = RuntimeException("location update failed")

        every {
            courierProfileService.updateCourierLocation(
                courierDetails = courierDetails,
                newCurrentLocation = currentLocation
            )
        } throws exception

        val thrown = assertThrows(RuntimeException::class.java) {
            courierProfileController.updateLocation(currentLocation, courierDetails)
        }

        assertEquals("location update failed", thrown.message)

        verify(exactly = 1) {
            courierProfileService.updateCourierLocation(
                courierDetails = courierDetails,
                newCurrentLocation = currentLocation
            )
        }
        confirmVerified(courierProfileService)
    }

    @Test
    fun `updateOnlineStatus should return 200 ok with empty body`() {
        val request = mockk<CourierStatusUpdateDTO>()
        val courierDetails = mockk<AccessTokenClaim>(relaxed = true)
        val updatedProfile = mockk<CourierProfileResDTO>()

        every {
            courierProfileService.updateCourierOnlineStatus(
                req = request,
                courierDetails = courierDetails
            )
        } returns updatedProfile

        val response = courierProfileController.updateOnlineStatus(request, courierDetails)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNull(response.body)

        verify(exactly = 1) {
            courierProfileService.updateCourierOnlineStatus(
                req = request,
                courierDetails = courierDetails
            )
        }
        confirmVerified(courierProfileService)
    }

    @Test
    fun `updateOnlineStatus should propagate exception when service throws`() {
        val request = mockk<CourierStatusUpdateDTO>()
        val courierDetails = mockk<AccessTokenClaim>(relaxed = true)
        val exception = RuntimeException("online status update failed")

        every {
            courierProfileService.updateCourierOnlineStatus(
                req = request,
                courierDetails = courierDetails
            )
        } throws exception

        val thrown = assertThrows(RuntimeException::class.java) {
            courierProfileController.updateOnlineStatus(request, courierDetails)
        }

        assertEquals("online status update failed", thrown.message)

        verify(exactly = 1) {
            courierProfileService.updateCourierOnlineStatus(
                req = request,
                courierDetails = courierDetails
            )
        }
        confirmVerified(courierProfileService)
    }

    @Test
    fun `getTaskHistory should return 200 ok with task history`() {
        val courierDetails = mockk<AccessTokenClaim>(relaxed = true)
        val taskHistory: List<CourierTaskHistoryDTO?> = listOf(mockk(), null)

        every {
            courierProfileService.getCourierOrderHistory(courierDetails = courierDetails)
        } returns taskHistory

        val response = courierProfileController.getTaskHistory(courierDetails)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertSame(taskHistory, response.body)

        verify(exactly = 1) {
            courierProfileService.getCourierOrderHistory(courierDetails = courierDetails)
        }
        confirmVerified(courierProfileService)
    }

    @Test
    fun `getTaskHistory should propagate exception when service throws`() {
        val courierDetails = mockk<AccessTokenClaim>(relaxed = true)
        val exception = RuntimeException("history fetch failed")

        every {
            courierProfileService.getCourierOrderHistory(courierDetails = courierDetails)
        } throws exception

        val thrown = assertThrows(RuntimeException::class.java) {
            courierProfileController.getTaskHistory(courierDetails)
        }

        assertEquals("history fetch failed", thrown.message)

        verify(exactly = 1) {
            courierProfileService.getCourierOrderHistory(courierDetails = courierDetails)
        }
        confirmVerified(courierProfileService)
    }
}