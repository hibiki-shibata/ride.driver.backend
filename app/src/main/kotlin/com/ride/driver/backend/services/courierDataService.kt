package com.ride.driver.backend.services

import org.springframework.stereotype.Service

import com.ride.driver.backend.models.CourierProfile
import com.ride.driver.backend.models.CourierStatus 
import com.ride.driver.backend.models.OperationArea
import com.ride.driver.backend.models.VehicleType
import com.ride.driver.backend.repositories.CourierProfileRepository
import java.util.UUID

@Service
class CourierDataService(){
    fun saveCoureirData(): CourierProfile {
            // Example data to save
            val courierProfile = CourierProfile(
                phoneNumber = "+1234567890",
                passwordHash = "hashed_password",
                cpFirstName = "John",
                cpLastName = "Doe",
                vehicleType = VehicleType.CAR,
                cpRate = 4.5,
                cpStatus = CourierStatus.AVAILABLE,
                operationArea = OperationArea(
                    name = "Downtown"
                ),
                cpComments = "No comments"
            )
            return courierProfile
    }   
}