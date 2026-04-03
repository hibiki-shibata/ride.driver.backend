package com.ride.driver.backend.courier.service

import com.ride.driver.backend.courier.dto.CourierProfileReqDTO
import com.ride.driver.backend.courier.dto.CourierProfileResDTO
import com.ride.driver.backend.courier.dto.CourierStatusUpdateDTO
import com.ride.driver.backend.courier.dto.CourierTaskHistoryDTO
import com.ride.driver.backend.courier.mapper.toCourierProfileResDto
import com.ride.driver.backend.courier.mapper.toCourierTaskHistoryDto
import com.ride.driver.backend.courier.model.CourierProfile
import com.ride.driver.backend.courier.model.CourierStatus
import com.ride.driver.backend.courier.model.VehicleType
import com.ride.driver.backend.courier.repository.CourierProfileRepository
import com.ride.driver.backend.logistic.model.Task
import com.ride.driver.backend.logistic.repository.TaskRepository
import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
import com.ride.driver.backend.shared.exception.AccountNotFoundException
import com.ride.driver.backend.shared.model.Coordinate
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import java.util.Optional
import java.util.UUID
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest

class CourierProfileServiceTest {

    private lateinit var courierProfileRepository: CourierProfileRepository
    private lateinit var taskRepository: TaskRepository
    private lateinit var courierProfileService: CourierProfileService

    @BeforeEach
    fun setUp() {
        courierProfileRepository = mockk()
        taskRepository = mockk()
        courierProfileService = CourierProfileService(
            courierProfileRepository = courierProfileRepository,
            taskRepository = taskRepository
        )

        mockkStatic(CourierProfile::toCourierProfileResDto)
        mockkStatic(Task::toCourierTaskHistoryDto)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getCourierProfile should return mapped courier profile`() {
        val courierId = UUID.randomUUID()
        val courierDetails = mockCourierDetails(courierId)
        val savedCourier = mockk<CourierProfile>(relaxed = true)
        val responseDto = mockk<CourierProfileResDTO>()

        every { courierProfileRepository.findById(courierId) } returns Optional.of(savedCourier)
        every { savedCourier.toCourierProfileResDto() } returns responseDto

        val result = courierProfileService.getCourierProfile(courierDetails)

        assertSame(responseDto, result)

        verify(exactly = 1) { courierProfileRepository.findById(courierId) }
        verify(exactly = 1) { savedCourier.toCourierProfileResDto() }
    }

    @Test
    fun `getCourierProfile should throw AccountNotFoundException when courier does not exist`() {
        val courierId = UUID.randomUUID()
        val courierDetails = mockCourierDetails(courierId)

        every { courierProfileRepository.findById(courierId) } returns Optional.empty()

        val thrown = assertThrows(AccountNotFoundException::class.java) {
            courierProfileService.getCourierProfile(courierDetails)
        }

        assertEquals("Courier not found with ID: $courierId", thrown.message)

        verify(exactly = 1) { courierProfileRepository.findById(courierId) }
    }

    @Test
    fun `updateCourierLocation should update location save courier and return mapped dto`() {
        val courierId = UUID.randomUUID()
        val courierDetails = mockCourierDetails(courierId)
        val newLocation = Coordinate(latitude = 35.0, longitude = 139.0)
        val savedCourier = mockk<CourierProfile>(relaxed = true)
        val updatedCourier = mockk<CourierProfile>(relaxed = true)
        val responseDto = mockk<CourierProfileResDTO>()

        every { courierProfileRepository.findById(courierId) } returns Optional.of(savedCourier)
        every { courierProfileRepository.save(savedCourier) } returns updatedCourier
        every { updatedCourier.toCourierProfileResDto() } returns responseDto

        val result = courierProfileService.updateCourierLocation(
            courierDetails = courierDetails,
            newCurrentLocation = newLocation
        )

        assertSame(responseDto, result)

        verify(exactly = 1) { courierProfileRepository.findById(courierId) }
        verify(exactly = 1) { savedCourier.currentLocation = newLocation }
        verify(exactly = 1) { courierProfileRepository.save(savedCourier) }
        verify(exactly = 1) { updatedCourier.toCourierProfileResDto() }
    }

    @Test
    fun `updateCourierLocation should throw AccountNotFoundException when courier does not exist`() {
        val courierId = UUID.randomUUID()
        val courierDetails = mockCourierDetails(courierId)
        val newLocation = Coordinate(latitude = 35.0, longitude = 139.0)

        every { courierProfileRepository.findById(courierId) } returns Optional.empty()

        val thrown = assertThrows(AccountNotFoundException::class.java) {
            courierProfileService.updateCourierLocation(
                courierDetails = courierDetails,
                newCurrentLocation = newLocation
            )
        }

        assertEquals("Courier not found with ID: $courierId", thrown.message)

        verify(exactly = 1) { courierProfileRepository.findById(courierId) }
    }

    @Test
    fun `updateCourierProfile should update mutable fields save courier and return mapped dto`() {
        val courierId = UUID.randomUUID()
        val courierDetails = mockCourierDetails(courierId)
        val req = mockk<CourierProfileReqDTO>()
        val savedCourier = mockk<CourierProfile>(relaxed = true)
        val updatedCourier = mockk<CourierProfile>(relaxed = true)
        val responseDto = mockk<CourierProfileResDTO>()
        val newVehicleType = enumValues<VehicleType>().first()
        val newStatus = enumValues<CourierStatus>().first()

        every { req.name } returns "Updated Name"
        every { req.phoneNumber } returns "+819012345678"
        every { req.vehicleType } returns newVehicleType
        every { req.cpStatus } returns newStatus
        every { req.cpComments } returns "updated comments"

        every { courierProfileRepository.findById(courierId) } returns Optional.of(savedCourier)
        every { courierProfileRepository.save(savedCourier) } returns updatedCourier
        every { updatedCourier.toCourierProfileResDto() } returns responseDto

        val result = courierProfileService.updateCourierProfile(
            req = req,
            courierDetails = courierDetails
        )

        assertSame(responseDto, result)

        verify(exactly = 1) { courierProfileRepository.findById(courierId) }
        verify(exactly = 1) { savedCourier.name = "Updated Name" }
        verify(exactly = 1) { savedCourier.phoneNumber = "+819012345678" }
        verify(exactly = 1) { savedCourier.vehicleType = newVehicleType }
        verify(exactly = 1) { savedCourier.cpStatus = newStatus }
        verify(exactly = 1) { savedCourier.cpComments = "updated comments" }
        verify(exactly = 1) { courierProfileRepository.save(savedCourier) }
        verify(exactly = 1) { updatedCourier.toCourierProfileResDto() }
    }

    @Test
    fun `deleteCourierProfile should delete saved courier`() {
        val courierId = UUID.randomUUID()
        val courierDetails = mockCourierDetails(courierId)
        val savedCourier = mockk<CourierProfile>(relaxed = true)

        every { courierProfileRepository.findById(courierId) } returns Optional.of(savedCourier)
        every { courierProfileRepository.delete(savedCourier) } returns Unit

        courierProfileService.deleteCourierProfile(courierDetails)

        verify(exactly = 1) { courierProfileRepository.findById(courierId) }
        verify(exactly = 1) { courierProfileRepository.delete(savedCourier) }
    }

    @Test
    fun `deleteCourierProfile should throw AccountNotFoundException when courier does not exist`() {
        val courierId = UUID.randomUUID()
        val courierDetails = mockCourierDetails(courierId)

        every { courierProfileRepository.findById(courierId) } returns Optional.empty()

        val thrown = assertThrows(AccountNotFoundException::class.java) {
            courierProfileService.deleteCourierProfile(courierDetails)
        }

        assertEquals("Courier not found with ID: $courierId", thrown.message)

        verify(exactly = 1) { courierProfileRepository.findById(courierId) }
    }

    @Test
    fun `updateCourierOnlineStatus should set ONLINE when request is online`() {
        val courierId = UUID.randomUUID()
        val courierDetails = mockCourierDetails(courierId)
        val req = mockk<CourierStatusUpdateDTO>()
        val savedCourier = mockk<CourierProfile>(relaxed = true)
        val updatedCourier = mockk<CourierProfile>(relaxed = true)
        val responseDto = mockk<CourierProfileResDTO>()

        every { req.isOnline } returns true
        every { courierProfileRepository.findById(courierId) } returns Optional.of(savedCourier)
        every { courierProfileRepository.save(savedCourier) } returns updatedCourier
        every { updatedCourier.toCourierProfileResDto() } returns responseDto

        val result = courierProfileService.updateCourierOnlineStatus(
            req = req,
            courierDetails = courierDetails
        )

        assertSame(responseDto, result)

        verify(exactly = 1) { courierProfileRepository.findById(courierId) }
        verify(exactly = 1) { savedCourier.cpStatus = CourierStatus.ONLINE }
        verify(exactly = 1) { courierProfileRepository.save(savedCourier) }
        verify(exactly = 1) { updatedCourier.toCourierProfileResDto() }
    }

    @Test
    fun `updateCourierOnlineStatus should set OFFLINE when request is offline`() {
        val courierId = UUID.randomUUID()
        val courierDetails = mockCourierDetails(courierId)
        val req = mockk<CourierStatusUpdateDTO>()
        val savedCourier = mockk<CourierProfile>(relaxed = true)
        val updatedCourier = mockk<CourierProfile>(relaxed = true)
        val responseDto = mockk<CourierProfileResDTO>()

        every { req.isOnline } returns false
        every { courierProfileRepository.findById(courierId) } returns Optional.of(savedCourier)
        every { courierProfileRepository.save(savedCourier) } returns updatedCourier
        every { updatedCourier.toCourierProfileResDto() } returns responseDto

        val result = courierProfileService.updateCourierOnlineStatus(
            req = req,
            courierDetails = courierDetails
        )

        assertSame(responseDto, result)

        verify(exactly = 1) { courierProfileRepository.findById(courierId) }
        verify(exactly = 1) { savedCourier.cpStatus = CourierStatus.OFFLINE }
        verify(exactly = 1) { courierProfileRepository.save(savedCourier) }
        verify(exactly = 1) { updatedCourier.toCourierProfileResDto() }
    }

    @Test
    fun `getCourierOrderHistory should request first page with size 20 and map tasks`() {
        val courierId = UUID.randomUUID()
        val courierDetails = mockCourierDetails(courierId)
        val task1 = mockk<Task>()
        val task2 = mockk<Task>()
        val history1 = mockk<CourierTaskHistoryDTO>()
        val history2 = mockk<CourierTaskHistoryDTO>()
        val pageRequest = PageRequest.of(0, 20)
        val taskPage = PageImpl(listOf(task1, task2))

        every {
            taskRepository.findByCourierProfile_Id(courierId, pageRequest)
        } returns taskPage
        every { task1.toCourierTaskHistoryDto() } returns history1
        every { task2.toCourierTaskHistoryDto() } returns history2

        val result = courierProfileService.getCourierOrderHistory(courierDetails)

        assertEquals(listOf(history1, history2), result)

        verify(exactly = 1) {
            taskRepository.findByCourierProfile_Id(courierId, pageRequest)
        }
        verify(exactly = 1) { task1.toCourierTaskHistoryDto() }
        verify(exactly = 1) { task2.toCourierTaskHistoryDto() }
    }

    @Test
    fun `getCourierOrderHistory should return empty list when no tasks exist`() {
        val courierId = UUID.randomUUID()
        val courierDetails = mockCourierDetails(courierId)
        val pageRequest = PageRequest.of(0, 20)
        val emptyPage = PageImpl<Task>(emptyList())

        every {
            taskRepository.findByCourierProfile_Id(courierId, pageRequest)
        } returns emptyPage

        val result = courierProfileService.getCourierOrderHistory(courierDetails)

        assertEquals(emptyList<CourierTaskHistoryDTO>(), result)

        verify(exactly = 1) {
            taskRepository.findByCourierProfile_Id(courierId, pageRequest)
        }
    }

    private fun mockCourierDetails(courierId: UUID): AccessTokenClaim {
        val courierDetails = mockk<AccessTokenClaim>()
        every { courierDetails.accountId } returns courierId
        return courierDetails
    }
}