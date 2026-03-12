package com.ride.driver.backend.config

import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import com.ride.driver.backend.repositories.CourierProfileRepository
import com.ride.driver.backend.repositories.OperationAreaRepository
import com.ride.driver.backend.repositories.TaskRepository
import com.ride.driver.backend.models.courierProfile.CourierProfile
import com.ride.driver.backend.models.courierProfile.CourierStatus
import com.ride.driver.backend.models.courierProfile.OperationArea
import com.ride.driver.backend.models.courierProfile.VehicleType
import com.ride.driver.backend.models.Coordinate
import com.ride.driver.backend.models.logistics.Task
import com.ride.driver.backend.models.logistics.TaskStatus
import java.util.UUID

@Configuration
class DbDemoDataInitializerConfig {
	@Bean
	fun databaseInitializer(
            courierProfileRepository: CourierProfileRepository, 
            operationAreaRepository: OperationAreaRepository,
            taskRepository: TaskRepository
    ) = ApplicationRunner {
        // Initialize the database with some default data
        if (operationAreaRepository.findByName("Tokyo").isNullOrEmpty()){
            val savedArea = operationAreaRepository.save(
                OperationArea(
                    name = "Tokyo")
            )
        }


        val john: CourierProfile = courierProfileRepository.save(
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

        val jane: CourierProfile = courierProfileRepository.save(
            CourierProfile(
                cpFirstName = "Jane",
                cpLastName = "Smith",
                phoneNumber = "987-654-3210",
                vehicleType = VehicleType.CAR,
                passwordHash = "another_hashed_password",
                cpRate = 4.8,
                cpStatus = CourierStatus.ONLINE,
                currentLocation = Coordinate(latitude = 0.0, longitude = 0.0),
                operationArea = operationAreaRepository.findByName("Tokyo")?.firstOrNull(),
                cpComments = "Fast and efficient"
            )
        )

        val alice: CourierProfile = courierProfileRepository.save(
            CourierProfile(
                cpFirstName = "Alice",
                cpLastName = "Johnson",
                phoneNumber = "555-123-4567",
                vehicleType = VehicleType.BICYCLE,
                passwordHash = "alice_hashed_password",
                cpRate = 4.2,
                cpStatus = CourierStatus.ONBOARDING,
                currentLocation = Coordinate(latitude = 0.0, longitude = 0.0),
                operationArea = operationAreaRepository.findByName("Tokyo")?.firstOrNull(),
                cpComments = "New courier"
            )
        )
        
        taskRepository.save(
            Task(
                assignedCourierId = UUID.randomUUID(),
                consumerId = alice.id ?: throw Exception("Alice ID is null"),
                pickupLocation = Coordinate(latitude = 35.6895, longitude = 139.6917),
                dropoffLocation = Coordinate(latitude = 35.6762, longitude = 139.6503),
                consumerName = "hibiki",
                venueName = "KFC",
                taskStatus = TaskStatus.READY_FOR_ASSIGNMENT
            )
        )

        taskRepository.save(
            Task(
                assignedCourierId = UUID.randomUUID(),
                consumerId = john.id ?: throw Exception("John ID is null"),
                pickupLocation = Coordinate(latitude = 33.6895, longitude = 129.6917),
                dropoffLocation = Coordinate(latitude = 35.6762, longitude = 139.6503),
                consumerName = "Shibata",
                venueName = "FIve guys",
                taskStatus = TaskStatus.READY_FOR_ASSIGNMENT
            )
        )
    }
}
