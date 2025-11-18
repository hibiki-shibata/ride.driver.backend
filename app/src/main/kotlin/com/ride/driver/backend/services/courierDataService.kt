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
                phoneNumber = "*810123456789",
                passwordHash = "Example Password Hash",
                name = "Example Courier Name",
                vehicleType = VehicleType.BIKE,
                rate = 5.0,
                status = CourierStatus.AVAILABLE,
                operationArea = OperationArea(name = "Downtown"),
                comments = "Example Comments"
            )
            return courierProfile
    }   
}