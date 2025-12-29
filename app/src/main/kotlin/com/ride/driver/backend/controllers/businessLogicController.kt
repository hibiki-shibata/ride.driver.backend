package com.ride.driver.backend.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import com.ride.driver.backend.repositories.CourierProfileRepository
import com.ride.driver.backend.models.CourierProfile
import com.ride.driver.backend.models.OperationArea
import com.ride.driver.backend.models.VehicleType
import com.ride.driver.backend.models.CourierStatus
import com.ride.driver.backend.services.AccessTokenData
import java.util.UUID

data class CourierProfileDTO(
    val id: UUID?,
    val name: String,
    val phoneNumber: String,
    val vehicleType: VehicleType?,
    val rate: Double?,
    val status: CourierStatus,
    val operationArea: OperationArea?,
    val comments: String?
)

@RestController
@RequestMapping("api/v1/couriers")
class BusinessLogicController (   
    private val repository: CourierProfileRepository,
){
    @GetMapping("/courier/me")
    fun findCourierProfile(): ResponseEntity<CourierProfileDTO> {        
        println("Finding all couriers...")
        val courierDetails: AccessTokenData = SecurityContextHolder.getContext().authentication.principal as AccessTokenData
        val courierId: UUID = courierDetails.additonalClaims.courierId
        val courier: CourierProfile = repository.findById(courierId) ?: throw Exception("Courier not found with ID: $courierId")
        val courierDTO = CourierProfileDTO(
            id = courier.id,
            name = courier.name,
            phoneNumber = courier.phoneNumber,
            vehicleType = courier.vehicleType,
            rate = courier.rate,
            status = courier.status,
            operationArea = courier.operationArea,
            comments = courier.comments
        )
        return ResponseEntity.ok(courierDTO)
    }

    @GetMapping("/all")
    fun getAllCouriersPagedAndSorted(
        @RequestParam page: Int,
        @RequestParam size: Int
    ): ResponseEntity<List<CourierProfileDTO>> {
        val pageable = Pageable.ofSize(size).withPage(page)
        val courierPage: Page<CourierProfile> = repository.findAll(pageable)
        val courierDTOs = courierPage.content.map { courier ->
            CourierProfileDTO(
                id = courier.id,
                name = courier.name,
                phoneNumber = courier.phoneNumber,
                vehicleType = courier.vehicleType,
                rate = courier.rate,
                status = courier.status,
                operationArea = courier.operationArea,
                comments = courier.comments
            )
        }
        return ResponseEntity.ok(courierDTOs)
    }   
}