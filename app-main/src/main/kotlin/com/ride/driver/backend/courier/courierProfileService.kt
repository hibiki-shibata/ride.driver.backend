package com.ride.driver.backend.courier.services

import org.springframework.stereotype.Service
import com.ride.driver.backend.courier.models.CourierProfile
import com.ride.driver.backend.courier.models.CourierStatus 
import com.ride.driver.backend.courier.models.OperationArea
import com.ride.driver.backend.courier.models.VehicleType
import com.ride.driver.backend.courier.repositories.CourierProfileRepository
import com.ride.driver.backend.shared.models.Coordinate
import java.util.UUID

@Service
class CourierProfileService(){
    fun saveCoureirData(): CourierProfile {
            // Example data to save
            val courierProfile = CourierProfile(
                phoneNumber = "+1234567890",
                passwordHash = "hashed_password",
                name = "John Doe",
                vehicleType = VehicleType.CAR,
                cpRate = 4.5,
                cpStatus = CourierStatus.ONLINE,
                operationArea = OperationArea(
                    name = "Downtown"
                ),
                currentLocation = Coordinate(latitude = 0.0, longitude = 0.0),
                cpComments = "No comments"
            )
            return courierProfile
    }   
}