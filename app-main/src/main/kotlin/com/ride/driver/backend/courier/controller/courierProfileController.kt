package com.ride.driver.backend.courier.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import jakarta.validation.Valid
import com.ride.driver.backend.courier.repository.CourierProfileRepository
import com.ride.driver.backend.courier.model.CourierProfile
import com.ride.driver.backend.courier.dto.CourierProfileDTO
import com.ride.driver.backend.courier.dto.CourierStatusUpdateDTO
import com.ride.driver.backend.logistic.model.Task
import com.ride.driver.backend.logistic.repository.TaskRepository
import com.ride.driver.backend.courier.service.CourierProfileService
import com.ride.driver.backend.shared.auth.domain.AccessTokenData
import com.ride.driver.backend.shared.model.Coordinate


@RestController
@RequestMapping("api/v1/couriers")
class CourierProfileController ( 
    private val courierProfileService: CourierProfileService, 
){
    @GetMapping("/me")
    fun getCourierProfile(
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

    @PutMapping("/me")
    fun updateCourierProfile(
        @RequestBody @Valid courierProfileDTO: CourierProfileDTO,
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

    @PutMapping("/location")
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

    @PutMapping("/online")
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

    @GetMapping("/history")
    fun getTaskHistory(
        @AuthenticationPrincipal courierDetails: AccessTokenData
    ): ResponseEntity<List<Task?>> {
        val taskHistory: List<Task?> = courierProfileService.getCourierOrderHistory(
            courierId = courierDetails.accountID
        )
        return ResponseEntity.ok(taskHistory)
    }
}