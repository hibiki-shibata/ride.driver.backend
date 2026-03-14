package com.ride.driver.backend.courier.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import jakarta.validation.Valid
import com.ride.driver.backend.courier.repositories.CourierProfileRepository
import com.ride.driver.backend.courier.models.CourierProfile
import com.ride.driver.backend.courier.models.OperationArea
import com.ride.driver.backend.courier.models.VehicleType
import com.ride.driver.backend.courier.models.CourierStatus
import com.ride.driver.backend.logistic.models.Task
import com.ride.driver.backend.logistic.models.TaskStatus
import com.ride.driver.backend.logistic.repositories.TaskRepository
import com.ride.driver.backend.auth.services.AccessTokenData
import com.ride.driver.backend.shared.models.Coordinate
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

data class CourierStatusUpdateDTO(
    val isOnline: Boolean,
)

@RestController
@RequestMapping("api/v1/couriers")
class CourierProfileController (   
    private val courierProfileRepository: CourierProfileRepository,
    private val taskRepository: TaskRepository
){
    @GetMapping("/courier/me")
    fun findCourierProfile(): ResponseEntity<CourierProfileDTO> {        
        val courierDetails: AccessTokenData = SecurityContextHolder.getContext().authentication?.principal as AccessTokenData ?: return ResponseEntity.status(401).build()
        val courierId: UUID = courierDetails.accountID
        val courier: CourierProfile = courierProfileRepository.findById(courierId) ?: throw Exception("Courier not found with ID: $courierId")
        val courierDTO = CourierProfileDTO(
            id = courier.id,
            name = courier.name,
            phoneNumber = courier.phoneNumber,
            vehicleType = courier.vehicleType,
            rate = courier.cpRate,
            status = courier.cpStatus,
            operationArea = courier.operationArea,
            comments = courier.cpComments
        )
        return ResponseEntity.ok(courierDTO)
    }

    @PostMapping("/update/mylocation")
    fun updateLocation(@RequestBody @Valid location: Coordinate): ResponseEntity<String> {
        val courierDetails: AccessTokenData = SecurityContextHolder.getContext().authentication?.principal as AccessTokenData ?: return ResponseEntity.status(401).build()
        val courierId: UUID = courierDetails.accountID
        courierProfileRepository.save(
            courierProfileRepository.findById(courierId)?.copy(
                currentLocation = location                
            ) ?: return ResponseEntity.status(404).body("Courier not found")
        )
        return ResponseEntity.ok("Location updated successfully")
    }    

    @PostMapping("/update/online")
    fun updateStatus(@RequestBody @Valid courierStatusUpdateDTO: CourierStatusUpdateDTO): ResponseEntity<String> {
        val courierDetails: AccessTokenData = SecurityContextHolder.getContext().authentication?.principal as AccessTokenData ?: return ResponseEntity.status(401).build()
        val isOnline: Boolean = courierStatusUpdateDTO.isOnline
        // val isOnline: Boolean = statusUpdateDTO.isOnline
        val courierId: UUID = courierDetails.accountID
        courierProfileRepository.save(
            courierProfileRepository.findById(courierId)?.copy(
                cpStatus = if (isOnline) CourierStatus.ONLINE else CourierStatus.OFFLINE
            ) ?: return ResponseEntity.status(404).body("Courier not found")
        )
        return ResponseEntity.ok("Status updated successfully")
    }

    @GetMapping("/courier/task-history")
    fun getTaskHistory(): ResponseEntity<List<Task>> {
        val courierDetails: AccessTokenData = SecurityContextHolder.getContext().authentication?.principal as AccessTokenData ?: return ResponseEntity.status(401).build()
        val courierId: UUID = courierDetails.accountID
        val taskHistory: List<Task> = taskRepository.findByCourierProfile_IdAndTaskStatus(courierId, TaskStatus.DELIVERED)
        return ResponseEntity.ok(taskHistory)
    }
}