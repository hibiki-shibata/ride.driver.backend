package com.ride.driver.backend.courier.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import jakarta.validation.Valid
import com.ride.driver.backend.courier.repository.CourierProfileRepository
import com.ride.driver.backend.courier.model.CourierProfile
import com.ride.driver.backend.courier.model.OperationArea
import com.ride.driver.backend.courier.model.VehicleType
import com.ride.driver.backend.courier.model.CourierStatus
import com.ride.driver.backend.logistic.model.Task
import com.ride.driver.backend.logistic.model.TaskStatus
import com.ride.driver.backend.shared.auth.domain.AccessTokenData
import com.ride.driver.backend.shared.model.Coordinate
import java.util.UUID
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import com.ride.driver.backend.courier.service.CourierProfileService

data class CourierProfileDTO(
    @field:NotBlank
    val id: UUID?,

    @field:NotBlank
    val name: String,

    @field:NotBlank
    @field:Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    val phoneNumber: String,

    @field:NotBlank
    val vehicleType: VehicleType?,

    @field:NotBlank
    val rate: Double?,

    @field:NotBlank
    val status: CourierStatus,

    @field:NotBlank
    val operationArea: OperationArea?,

    @field:NotBlank
    val comments: String?
)

data class CourierStatusUpdateDTO(
    val isOnline: Boolean,
)

@RestController
@RequestMapping("api/v1/couriers")
class CourierProfileController ( 
    private val courierProfileService: CourierProfileService, 
){
    @GetMapping("/courier/me")
    fun findCourierProfile(
        @AuthenticationPrincipal courierDetails: AccessTokenData
    ): ResponseEntity<CourierProfileDTO> {        
        val courierProfile: CourierProfile = courierProfileService.getCourierProfile(
            courierId = courierDetails.accountID
        )
        return ResponseEntity.ok(
            CourierProfileDTO(
                id = courierProfile.id,
                name = courierProfile.name,
                phoneNumber = courierProfile.phoneNumber,
                vehicleType = courierProfile.vehicleType,
                rate = courierProfile.cpRate,
                status = courierProfile.cpStatus,
                operationArea = courierProfile.operationArea,
                comments = courierProfile.cpComments
            )
        )
    }

    @PutMapping("/courier/update")
    fun updateCourierProfile(
        @RequestBody courierProfileDTO: CourierProfileDTO,
        @AuthenticationPrincipal courierDetails: AccessTokenData
    ): ResponseEntity<CourierProfileDTO> {
        val updatedProfile: CourierProfile = courierProfileService.updateCourierProfile(
            courierId = courierDetails.accountID,
            newName = courierProfileDTO.name,
            newPhoneNumber = courierProfileDTO.phoneNumber,
            newVehicleType = courierProfileDTO.vehicleType,
            newOperationArea = courierProfileDTO.operationArea,
            newComments = courierProfileDTO.comments
        )
        return ResponseEntity.ok(
            CourierProfileDTO(
                id = updatedProfile.id,
                name = updatedProfile.name,
                phoneNumber = updatedProfile.phoneNumber,
                vehicleType = updatedProfile.vehicleType,
                rate = updatedProfile.cpRate,
                status = updatedProfile.cpStatus,
                operationArea = updatedProfile.operationArea,
                comments = updatedProfile.cpComments
            )
        )
    }

    @PutMapping("/courier/location")
    fun updateLocation(
        @RequestBody @Valid location: Coordinate,
        @AuthenticationPrincipal courierDetails: AccessTokenData
    ): ResponseEntity<String> {
        val updatedProfile: CourierProfile = courierProfileService.updateCourierLocation(
            courierId = courierDetails.accountID,
            location = location
        )
        return ResponseEntity.ok("Location updated successfully")
    }    

    @PutMapping("/courier/online")
    fun updateStatus(
        @RequestBody @Valid courierStatusUpdateDTO: CourierStatusUpdateDTO, 
        @AuthenticationPrincipal courierDetails: AccessTokenData
    ): ResponseEntity<String> {
        val updatedProfile: CourierProfile = courierProfileService.updateCourierOnlineStatus(
            courierId = courierDetails.accountID,
            isOnline = courierStatusUpdateDTO.isOnline
        )
        return ResponseEntity.ok("Courier status updated to ${if (courierStatusUpdateDTO.isOnline) "ONLINE" else "OFFLINE"} successfully")
    }

    @GetMapping("/courier/history")
    fun getTaskHistory(
        @AuthenticationPrincipal courierDetails: AccessTokenData
    ): ResponseEntity<List<Task?>> {
        val taskHistory: List<Task?> = courierProfileService.getCourierOrderHistory(
            courierId = courierDetails.accountID
        )
        return ResponseEntity.ok(taskHistory)
    }
}