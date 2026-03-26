package com.ride.driver.backend.courier.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.UUID
import com.ride.driver.backend.courier.model.CourierProfile
import com.ride.driver.backend.courier.model.CourierStatus 
import com.ride.driver.backend.courier.model.OperationArea
import com.ride.driver.backend.courier.model.VehicleType
import com.ride.driver.backend.courier.mapper.toCourierProfileResDto
import com.ride.driver.backend.courier.mapper.toCourierTaskHistoryDto
import com.ride.driver.backend.courier.repository.CourierProfileRepository
import com.ride.driver.backend.courier.dto.CourierProfileResDTO
import com.ride.driver.backend.courier.dto.CourierProfileReqDTO
import com.ride.driver.backend.courier.dto.CourierStatusUpdateDTO
import com.ride.driver.backend.courier.dto.CourierTaskHistoryDTO
import com.ride.driver.backend.logistic.model.Task
import com.ride.driver.backend.logistic.model.TaskStatus
import com.ride.driver.backend.logistic.repository.TaskRepository
import com.ride.driver.backend.shared.exception.AccountNotFoundException
import com.ride.driver.backend.shared.model.Coordinate
import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim

@Service
class CourierProfileService(
    private val courierProfileRepository: CourierProfileRepository,
    private val taskRepository: TaskRepository
){
    private val logger: Logger = LoggerFactory.getLogger(CourierProfileService::class.java)

    fun getCourierProfile(courierDetails: AccessTokenClaim): CourierProfileResDTO {
        val savedCourierProfile: CourierProfile = getCourierProfileById(courierDetails.accountId)
        logger.info("event=courier_orderHistory_fetched courierId={}", courierDetails.accountId)
        return savedCourierProfile.toCourierProfileResDto()
    }

    @Transactional
    fun updateCourierLocation(
        courierDetails: AccessTokenClaim,
        newCurrentLocation: Coordinate
    ): CourierProfileResDTO {
        val savedCourierProfile: CourierProfile = getCourierProfileById(courierDetails.accountId)
        savedCourierProfile.apply {
            currentLocation = newCurrentLocation
        }
        val newSavedCourierProfile: CourierProfile = courierProfileRepository.save(savedCourierProfile)
             ?: throw AccountNotFoundException("Courier not found with ID: $courierDetails.accountId")
        logger.info("event=courier_location_update_completed courierId={}", courierDetails.accountId)
        return newSavedCourierProfile.toCourierProfileResDto()
    }
    
    @Transactional
    fun updateCourierProfile(
        req: CourierProfileReqDTO,
        courierDetails: AccessTokenClaim
    ): CourierProfileResDTO {
        val savedCourierProfile: CourierProfile = getCourierProfileById(courierDetails.accountId)
        savedCourierProfile.apply {
            name = req.name;
            phoneNumber = req.phoneNumber;
            vehicleType = req.vehicleType;
            cpStatus = req.cpStatus;
            cpComments = req.cpComments;
         }
        val updatedProfile: CourierProfile = courierProfileRepository.save(savedCourierProfile)
        logger.info("event=courier_profile_update_completed courierId={}", courierDetails.accountId)        
        return updatedProfile.toCourierProfileResDto()
    }
    
    @Transactional
    fun updateCourierOnlineStatus(
        req: CourierStatusUpdateDTO,
        courierDetails: AccessTokenClaim
    ): CourierProfileResDTO {
        val savedCourierProfile: CourierProfile = getCourierProfileById(courierDetails.accountId)
        savedCourierProfile.apply {
            cpStatus = if (req.isOnline) CourierStatus.ONLINE else CourierStatus.OFFLINE
        }
        val updatedProfile: CourierProfile = courierProfileRepository.save(savedCourierProfile)
        logger.info("event=courier_onlineStatus_update_completed courierId={}", courierDetails.accountId)
        return updatedProfile.toCourierProfileResDto()
    }

    fun getCourierOrderHistory(
        courierDetails: AccessTokenClaim
    ): List<CourierTaskHistoryDTO> {
        val taskHistories: List<Task> = taskRepository.findByCourierProfile_Id(courierDetails.accountId)
        logger.info("event=courier_orderHistory_fetched_courierId={}", courierDetails.accountId)
        return taskHistories.map { it.toCourierTaskHistoryDto() }
    }

    private fun getCourierProfileById(courierId: UUID): CourierProfile {
            return courierProfileRepository.findById(courierId).orElseThrow { 
                AccountNotFoundException("Courier not found with ID: $courierId") 
            }
    }    
}