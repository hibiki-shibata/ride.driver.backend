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
import com.ride.driver.backend.repositories.TaskRepository
import com.ride.driver.backend.models.courierProfile.CourierProfile
import com.ride.driver.backend.models.courierProfile.OperationArea
import com.ride.driver.backend.models.courierProfile.VehicleType
import com.ride.driver.backend.models.courierProfile.CourierStatus
import com.ride.driver.backend.models.logistics.Task
import com.ride.driver.backend.models.logistics.TaskStatus
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
class CourierProfileController (   
    private val courierProfileRepository: CourierProfileRepository,
    private val taskRepository: TaskRepository
){
    @GetMapping("/courier/me")
    fun findCourierProfile(): ResponseEntity<CourierProfileDTO> {        
        println("Finding all couriers...")
        val courierDetails: AccessTokenData = SecurityContextHolder.getContext().authentication?.principal as AccessTokenData ?: return ResponseEntity.status(401).build()
        val courierId: UUID = courierDetails.additonalClaims.accountID
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

    @GetMapping("/courier/task-history")
    fun getTaskHistory(): ResponseEntity<List<Task>> {
        val courierDetails: AccessTokenData = SecurityContextHolder.getContext().authentication?.principal as AccessTokenData ?: return ResponseEntity.status(401).build()
        val courierId: UUID = courierDetails.additonalClaims.accountID
        val taskHistory: List<Task> = taskRepository.findByCourierProfile_IdAndTaskStatus(courierId, TaskStatus.DELIVERED)
        return ResponseEntity.ok(taskHistory)
    }
}