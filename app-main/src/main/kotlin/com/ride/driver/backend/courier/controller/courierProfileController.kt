package com.ride.driver.backend.courier.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import jakarta.validation.Valid
import com.ride.driver.backend.courier.model.CourierProfile
import com.ride.driver.backend.courier.service.CourierProfileService
import com.ride.driver.backend.courier.dto.CourierProfileResDTO
import com.ride.driver.backend.courier.dto.CourierProfileReqDTO
import com.ride.driver.backend.courier.dto.CourierTaskHistoryDTO
import com.ride.driver.backend.courier.dto.CourierStatusUpdateDTO
import com.ride.driver.backend.logistic.model.Task
import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
import com.ride.driver.backend.shared.model.Coordinate


@RestController
@RequestMapping("api/v1/couriers")
class CourierProfileController ( 
    private val courierProfileService: CourierProfileService, 
){
    @GetMapping("/me")
    fun getCourierProfile(
        @AuthenticationPrincipal courierDetails: AccessTokenClaim
    ): ResponseEntity<CourierProfileResDTO> {        
        val courierProfile: CourierProfileResDTO = courierProfileService.getCourierProfile(courierDetails)
        return ResponseEntity.ok(courierProfile)
    }

    @PutMapping("/me")
    fun updateCourierProfile(
        @RequestBody @Valid req: CourierProfileReqDTO,
        @AuthenticationPrincipal courierDetails: AccessTokenClaim
    ): ResponseEntity<CourierProfileResDTO> {
        val updatedProfile: CourierProfileResDTO = courierProfileService.updateCourierProfile(
            req = req,
            courierDetails = courierDetails
        )
        return ResponseEntity.ok(updatedProfile)
    }

    @PutMapping("/location")
    fun updateLocation(
        @RequestBody @Valid location: Coordinate,
        @AuthenticationPrincipal courierDetails: AccessTokenClaim
    ): ResponseEntity<Void> {
        val updatedProfile: CourierProfile = courierProfileService.updateCourierLocation(
            courierId = courierDetails.accountID,
            location = location
        )
        return ResponseEntity.ok().build()
    }    

    @PutMapping("/online")
    fun updateOnlineStatus(
        @RequestBody @Valid courierStatusUpdateDTO: CourierStatusUpdateDTO, 
        @AuthenticationPrincipal courierDetails: AccessTokenClaim
    ): ResponseEntity<Void> {
        val updatedProfile: CourierProfile = courierProfileService.updateCourierOnlineStatus(
            courierId = courierDetails.accountID,
            isOnline = courierStatusUpdateDTO.isOnline
        )
        return ResponseEntity.ok().build()
    }

    @GetMapping("/history")
    fun getTaskHistory(
        @AuthenticationPrincipal courierDetails: AccessTokenClaim
    ): ResponseEntity<List<CourierTaskHistoryDTO>> {
        val taskHistory: List<Task?> = courierProfileService.getCourierOrderHistory(
            courierId = courierDetails.accountID
        )
        return ResponseEntity.ok(
            taskHistory.map { task ->
                CourierTaskHistoryDTO(
                    taskId = task?.id.toString(),
                    courierEarning = task?.courierEarning ?: 0.0,
                    orderTime = task?.orderTime.toString(),
                    consumerName = task?.consumerProfile?.name ?: "Unknown Consumer",
                    merchantName = task?.merchantProfile?.name ?: "Unknown Merchant"
                )
            }
        )
    }
}