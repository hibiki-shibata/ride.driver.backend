package com.ride.driver.backend.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.http.ResponseEntity
import com.ride.driver.backend.repositories.CourierProfileRepository
import com.ride.driver.backend.models.CourierProfile
import com.ride.driver.backend.models.OperationArea
import com.ride.driver.backend.models.VehicleType
import com.ride.driver.backend.models.CourierStatus

data class CourierProfileDTO(
    val id: Int,
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
    @GetMapping("/findall")
    fun findCourier(): ResponseEntity<List<CourierProfileDTO>> {        
        println("Finding all couriers...")
        val couriers: List<CourierProfile> = repository.findAll()
        
        if (couriers.none()) throw Exception("No couriers found in the database")
        val result = couriers.map { courier ->
            CourierProfileDTO(
                id = courier.id ?: throw Exception("Courier ID is null"),
                name = courier.name,
                phoneNumber = courier.phoneNumber,
                vehicleType = courier.vehicleType,
                status = courier.status,
                rate = courier.rate,
                comments = courier.comments,
                operationArea = courier.operationArea
            )
        }
        return ResponseEntity.ok(result)
    }
}