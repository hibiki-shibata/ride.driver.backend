package com.ride.driver.backend.courier.service

import org.springframework.stereotype.Service
import java.util.UUID
import com.ride.driver.backend.courier.model.CourierProfile
import com.ride.driver.backend.courier.model.CourierStatus 
import com.ride.driver.backend.courier.model.OperationArea
import com.ride.driver.backend.courier.model.VehicleType
import com.ride.driver.backend.courier.repository.CourierProfileRepository
import com.ride.driver.backend.shared.model.Coordinate
import com.ride.driver.backend.logistic.model.Task
import com.ride.driver.backend.logistic.model.TaskStatus
import com.ride.driver.backend.logistic.repository.TaskRepository
import com.ride.driver.backend.shared.exception.AccountNotFoundException

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import com.ride.driver.backend.courier.dto.CourierProfileResDTO
import com.ride.driver.backend.courier.dto.CourierProfileReqDTO
import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim

@Service
class CourierProfileService(
    private val courierProfileRepository: CourierProfileRepository,
    private val taskRepository: TaskRepository
){
    private val logger: Logger = LoggerFactory.getLogger(CourierProfileService::class.java)

    fun getCourierProfile(courierDetails: AccessTokenClaim): CourierProfileResDTO {
        return courierProfileRepository.findById(courierDetails.accountID) ?: throw AccountNotFoundException("Courier not found with ID: ${courierDetails.accountID}")
    }

    fun updateCourierLocation(
        courierId: UUID,
        location: Coordinate
    ): CourierProfile {
        val savedCourierProfile: CourierProfile = courierProfileRepository.save(
            courierProfileRepository.findById(courierId)?.copy(
                currentLocation = location                
            ) ?: throw Exception("Courier not found with ID: $courierId")
        )
        return savedCourierProfile
    }

    fun updateCourierProfile(
        req: CourierProfileReqDTO,
        courierDetails: AccessTokenClaim
    ): CourierProfileResDTO {
        val savedCourierProfile = courierProfileRepository.findById(courierDetails.accountID)
         ?: throw AccountNotFoundException("Courier not found with ID: $courierId")
         savedCourierProfile.apply {
            name = req.name,
            phonenumber = req.phonenumber,
            vehicleType = req.vehiclyType,
            cpStatus = req.cpStatus,
            cpComments = req.cpComments
         }
        val updatedProfile = existingProfile.save(savedCourierProfile)
        logger.info("event=courier_profile_update_completed consumerId={}", consumerDataInToken.accountID)        
        return updatedProfile
    }

    fun updateCourierOnlineStatus(
        courierId: UUID,
        isOnline: Boolean
    ): CourierProfile {
        val updatedProfile = courierProfileRepository.save(
            courierProfileRepository.findById(courierId)?.copy(
                cpStatus = if (isOnline) CourierStatus.ONLINE else CourierStatus.OFFLINE
            ) ?: throw AccountNotFoundException("Courier not found with ID: $courierId")
        )
        return updatedProfile
    }

    fun getCourierOrderHistory(courierId: UUID): List<Task?> {
        val taskHistories: List<Task?> = taskRepository.findByCourierProfile_Id(courierId)  
        return taskHistories ?: emptyList()
    }
}