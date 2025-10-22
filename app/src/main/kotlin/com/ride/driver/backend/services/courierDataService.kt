
package com.ride.driver.backend.services
import org.springframework.stereotype.Service

import com.ride.driver.backend.models.CourierProfile
import com.ride.driver.backend.models.CourierStatus 
import com.ride.driver.backend.models.OperationArea
import com.ride.driver.backend.models.VehicleType
import com.ride.driver.backend.repositories.CourierProfileRepository
import java.util.UUID

@Service
open class CourierDataService(){
        
        fun saveCoureirData(): CourierProfile {
                // Example data to save
                val courierProfile = CourierProfile(
                    id = UUID.randomUUID(),
                    phoneNumber = "123-456-7890",
                    name = "John Doe",
                    vehicleType = VehicleType.BIKE,
                    rate = 5.0,
                    status = CourierStatus.AVAILABLE,
                    operationArea = OperationArea(name = "Downtown"), // Example of setting an area
                    comments = "Ready to deliver"
                )
                return courierProfile
                // return courierProfileRepository.save(driverDetails)
        }   
    }