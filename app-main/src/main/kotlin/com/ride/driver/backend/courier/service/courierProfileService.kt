package com.ride.driver.backend.courier.service

import org.springframework.stereotype.Service
import com.ride.driver.backend.courier.model.CourierProfile
import com.ride.driver.backend.courier.model.CourierStatus 
import com.ride.driver.backend.courier.model.OperationArea
import com.ride.driver.backend.courier.model.VehicleType
import com.ride.driver.backend.courier.repository.CourierProfileRepository
import com.ride.driver.backend.shared.model.Coordinate
import com.ride.driver.backend.logistic.model.Task
import com.ride.driver.backend.logistic.model.TaskStatus
import com.ride.driver.backend.logistic.repository.TaskRepository
import java.util.UUID

@Service
class CourierProfileService(
    private val courierProfileRepository: CourierProfileRepository,
    private val taskRepository: TaskRepository
){
    fun getCourierProfile(courierId: UUID): CourierProfile{
        val courier: CourierProfile = courierProfileRepository.findById(courierId) ?: throw Exception("Courier not found with ID: $courierId")
        return courier
    }

    fun updateCourierLocation(
        courierId: UUID,
        location: Coordinate
    ): CourierProfile {
        val courierProfile: CourierProfile = courierProfileRepository.save(
            courierProfileRepository.findById(courierId)?.copy(
                currentLocation = location                
            ) ?: throw Exception("Courier not found with ID: $courierId")
        )
        return courierProfile
    }

    fun updateCourierProfile(
        courierId: UUID,
        newName: String?,
        newPhoneNumber: String?,
        newVehicleType: VehicleType?,
        newOperationArea: OperationArea?,
        newComments: String?
    ): CourierProfile {
        val existingProfile = courierProfileRepository.findById(courierId) ?: throw Exception("Courier not found with ID: $courierId")
        val updatedProfile = existingProfile.copy(
            name = newName ?: existingProfile.name,
            phoneNumber = newPhoneNumber ?: existingProfile.phoneNumber,
            vehicleType = newVehicleType ?: existingProfile.vehicleType,
            operationArea = newOperationArea ?: existingProfile.operationArea,
            cpComments = newComments ?: existingProfile.cpComments
        )
        courierProfileRepository.save(updatedProfile)
        return updatedProfile
    }

    fun updateCourierOnlineStatus(
        courierId: UUID,
        isOnline: Boolean
    ): CourierProfile {
        val updatedProfile = courierProfileRepository.save(
            courierProfileRepository.findById(courierId)?.copy(
                cpStatus = if (isOnline) CourierStatus.ONLINE else CourierStatus.OFFLINE
            ) ?: throw Exception("Courier not found with ID: $courierId")
        )
        return updatedProfile
    }

    fun getCourierOrderHistory(courierId: UUID): List<Task?> {
        val taskHistories: List<Task?> = taskRepository.findByCourierProfile_Id(courierId)  
        return taskHistories ?: emptyList()
    }
}