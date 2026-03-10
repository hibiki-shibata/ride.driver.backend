package com.ride.driver.backend.config

import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import com.ride.driver.backend.repositories.CourierProfileRepository
import com.ride.driver.backend.repositories.OperationAreaRepository
import com.ride.driver.backend.models.courierProfile.CourierProfile
import com.ride.driver.backend.models.courierProfile.CourierStatus
import com.ride.driver.backend.models.courierProfile.OperationArea
import com.ride.driver.backend.models.courierProfile.VehicleType
import com.ride.driver.backend.models.Coordinate
import java.util.UUID

@Configuration
class DbDemoDataInitializerConfig {
	@Bean
	fun databaseInitializer(
            courierProfileRepository: CourierProfileRepository, 
            operationAreaRepository: OperationAreaRepository 
    ) = ApplicationRunner {
        // Initialize the database with some default data
        if (operationAreaRepository.findByName("Tokyo").isNullOrEmpty()){
            val savedArea = operationAreaRepository.save(
                OperationArea(
                    name = "Tokyo")
            )
        }

        courierProfileRepository.save(
            CourierProfile(
                cpFirstName = "John",
                cpLastName = "Doe",
                phoneNumber = "123-456-7890",
                vehicleType = VehicleType.MOTORCYCLE,
                passwordHash = "hashed_password",
                cpRate = 4.5,
                cpStatus = CourierStatus.ONLINE,
                currentLocation = Coordinate(latitude = 0.0, longitude = 0.0),
                operationArea = operationAreaRepository.findByName("Tokyo")?.firstOrNull(),
                cpComments = "Reliable courier",
            )
        )

        courierProfileRepository.save(
            CourierProfile(
                cpFirstName = "Jane",
                cpLastName = "Smith",
                phoneNumber = "987-654-3210",
                vehicleType = VehicleType.CAR,
                passwordHash = "another_hashed_password",
                cpRate = 4.8,
                cpStatus = CourierStatus.AVAILABLE,
                currentLocation = Coordinate(latitude = 0.0, longitude = 0.0),
                cpComments = "Fast and efficient"
            )
        )

        courierProfileRepository.save(
            CourierProfile(
                cpFirstName = "Alice",
                cpLastName = "Johnson",
                phoneNumber = "555-123-4567",
                vehicleType = VehicleType.BICYCLE,
                passwordHash = "alice_hashed_password",
                cpRate = 4.2,
                cpStatus = CourierStatus.ONBOARDING,
                currentLocation = Coordinate(latitude = 0.0, longitude = 0.0),
                cpComments = "New courier"
            )
        )
    }
}
