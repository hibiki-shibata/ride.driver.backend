package com.ride.driver.backend.services

import org.springframework.stereotype.Service
import com.ride.driver.backend.dto.DriverDetailsDto
import com.ride.driver.backend.models.DriverDetails

import com.ride.driver.backend.models.VehicleType
import com.ride.driver.backend.models.Location
import com.ride.driver.backend.models.DriverStatus 

// import com.ride.driver.backend.repositories.CourierProfileRepository

import java.util.UUID


// @Service
// open class CourierDataService{

//     fun getCourierData(): DriverDetailsDto {
            
//             val driverDetails = db.findDriverById("randomID") ?: return DriverDetailsDto(
//                 id = UUID.randomUUID(),
//                 phoneNumber = "000-000-0000",
//                 name = "Unknown",
//                 vehicleType = VehicleType.CAR,
//                 location = Location(0.0, 0.0),
//                 assignID = "N/A",
//                 rate = 0.0,
//                 status = DriverStatus.UNAVAILABLE,
//                 area = "Unknown Area"
//             )

//             // Mapping to DTO
//             return DriverDetailsDto(
//                 id = driverDetails.id,
//                 phoneNumber = driverDetails.phoneNumber,
//                 name = driverDetails.name,
//                 vehicleType = driverDetails.vehicleType,
//                 location = driverDetails.location,
//                 assignID = driverDetails.assignID,
//                 rate = driverDetails.rate,
//                 status = driverDetails.status,
//                 area = driverDetails.area,
//                 driverComments = driverDetails.driverComments
//             )    
//         }
    

//         fun saveCoureirData(
//             db: CourierProfileRepository
//         ): DriverDetails {
//                 val driverDetails = DriverDetails(
//                     id = UUID.randomUUID(),
//                     phoneNumber = "123-456-7890",
//                     name = "John Doe",
//                     vehicleType = VehicleType.BIKE,
//                     location = Location(37.7749, -122.4194), // Example coordinates
//                     assignID = "assign123",
//                     rate = 5.0,
//                     status = DriverStatus.AVAILABLE,
//                     area = "Downtown",
//                     driverComments = "Ready to deliver"
//                 )
//                 return db.save(driverDetails)
//         }   

    
//     }