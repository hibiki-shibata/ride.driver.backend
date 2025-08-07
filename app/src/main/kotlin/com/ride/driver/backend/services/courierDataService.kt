
package com.ride.driver.backend.services
import org.springframework.stereotype.Service

import com.ride.driver.backend.models.DriverDetails
import com.ride.driver.backend.models.Location
import com.ride.driver.backend.models.Area
import com.ride.driver.backend.models.VehicleType
import com.ride.driver.backend.models.DriverStatus 

import com.ride.driver.backend.repositories.CourierProfileRepository

import java.util.UUID


@Service
open class CourierDataService(){
        
        fun saveCoureirData(): DriverDetails {

                // Example data to save
                val driverDetails = DriverDetails(
                    id = UUID.randomUUID(),
                    phoneNumber = "123-456-7890",
                    name = "John Doe",
                    vehicleType = VehicleType.BIKE,
                    location = Location(37.7749, -122.4194), 
                    assignID = "assign123",
                    rate = 5.0,
                    status = DriverStatus.AVAILABLE,
                    area = Area(name = "Downtown"), // Example of setting an area
                    driverComments = "Ready to deliver"
                )

                return driverDetails
                // return courierProfileRepository.save(driverDetails)
        }   

    
    }