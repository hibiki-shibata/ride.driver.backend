package com.ride.driver.backend.config

import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import com.ride.driver.backend.repositories.CourierProfileRepository
import com.ride.driver.backend.repositories.OperationAreaRepository
import com.ride.driver.backend.models.CourierProfile
import com.ride.driver.backend.models.OperationArea
import com.ride.driver.backend.models.VehicleType
import com.ride.driver.backend.models.CourierStatus
import java.util.UUID

@Configuration
class DbDemoDataInitializerConfig {
	@Bean
	fun databaseInitializer(
            courierProfileRepository: CourierProfileRepository, 
            operationAreaRepository: OperationAreaRepository 
    ) = ApplicationRunner {
        // Initialize the database with some default data
        val savedArea = operationAreaRepository.save(
            OperationArea(
                name = "Tokyo")
        )

        val tokyoArea = operationAreaRepository.findByName("Tokyo")
            ?: throw IllegalStateException("Area 'Tokyo' not found")

        courierProfileRepository.save(
            CourierProfile(
                cpFirstName = "John",
                cpLastName = "Doe",
                phoneNumber = "123-456-7890",
                vehicleType = VehicleType.MOTORCYCLE,
                passwordHash = "hashed_password",
                cpRate = 4.5,
                cpStatus = CourierStatus.ON_DUTY,
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
                cpComments = "New courier"
            )
        )
    }
}
