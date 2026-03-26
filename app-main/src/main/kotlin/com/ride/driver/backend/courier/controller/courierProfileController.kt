package com.ride.driver.backend.courier.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import jakarta.validation.Valid
import com.ride.driver.backend.courier.service.CourierProfileService
import com.ride.driver.backend.courier.dto.CourierProfileResDTO
import com.ride.driver.backend.courier.dto.CourierProfileReqDTO
import com.ride.driver.backend.courier.dto.CourierTaskHistoryDTO
import com.ride.driver.backend.courier.dto.CourierStatusUpdateDTO
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
        @RequestBody @Valid currentLocation: Coordinate,
        @AuthenticationPrincipal courierDetails: AccessTokenClaim
    ): ResponseEntity<Void> {
        val updatedProfile: CourierProfileResDTO = courierProfileService.updateCourierLocation(
            courierDetails = courierDetails,
            newCurrentLocation = currentLocation
        )
        return ResponseEntity.ok().build()
    }    

    @PutMapping("/online")
    fun updateOnlineStatus(
        @RequestBody @Valid req: CourierStatusUpdateDTO, 
        @AuthenticationPrincipal courierDetails: AccessTokenClaim
    ): ResponseEntity<Void> {
        val updatedProfile: CourierProfileResDTO = courierProfileService.updateCourierOnlineStatus(
            req = req,
            courierDetails = courierDetails
        )
        return ResponseEntity.ok().build()
    }

    @GetMapping("/history")
    fun getTaskHistory(
        @AuthenticationPrincipal courierDetails: AccessTokenClaim
    ): ResponseEntity<List<CourierTaskHistoryDTO>> {
        val courierTaskHistory: List<CourierTaskHistoryDTO> = courierProfileService.getCourierOrderHistory(courierDetails = courierDetails)
        return ResponseEntity.ok(courierTaskHistory)
    }
}