package com.ride.driver.backend.config

import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import com.ride.driver.backend.repositories.CourierProfileRepository
import com.ride.driver.backend.repositories.AreaRepository

import com.ride.driver.backend.models.DriverDetails
import com.ride.driver.backend.models.Area
import com.ride.driver.backend.models.Location
import com.ride.driver.backend.models.VehicleType
import com.ride.driver.backend.models.DriverStatus
import java.util.UUID


@Configuration
class BlogConfiguration {

	@Bean
	fun databaseInitializer(courierProfileRepository: CourierProfileRepository, areaRepository: AreaRepository ) = ApplicationRunner {

        // Initialize the database with some default data
        val savedArea = areaRepository.save(
            Area(
                name = "Tokyo")
        )

        val tokyoArea = areaRepository.findByName("Tokyo")
            ?: throw IllegalStateException("Area 'Tokyo' not found")
        
        
        courierProfileRepository.save(
            DriverDetails(
                id = null,
                phoneNumber = "111-111-1111",
                name = "Initial Driver",
                vehicleType = VehicleType.BIKE,
                location = Location(0.0, 0.0), // Default locations
                assignId = "assign123",
                rate = 5.0,
                status = DriverStatus.AVAILABLE,
                area = tokyoArea[0],
                driverComments = "Ready to deliver"
            )
        )
	
	}
}
