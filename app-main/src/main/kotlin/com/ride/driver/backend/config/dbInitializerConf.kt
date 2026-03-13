package com.ride.driver.backend.config

import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import com.ride.driver.backend.repositories.CourierProfileRepository
import com.ride.driver.backend.repositories.OperationAreaRepository
import com.ride.driver.backend.repositories.TaskRepository
import com.ride.driver.backend.repositories.VenueProfileRepository
import com.ride.driver.backend.repositories.ConsumerProfileRepository
import com.ride.driver.backend.models.courierProfile.CourierProfile
import com.ride.driver.backend.models.courierProfile.CourierStatus
import com.ride.driver.backend.models.courierProfile.OperationArea
import com.ride.driver.backend.models.courierProfile.VehicleType
import com.ride.driver.backend.models.Coordinate
import com.ride.driver.backend.models.logistics.Task
import com.ride.driver.backend.models.logistics.TaskStatus
import com.ride.driver.backend.models.venueProfile.VenueProfile
import com.ride.driver.backend.models.venueProfile.VenueStatus
import com.ride.driver.backend.models.consumerProfile.ConsumerProfile

import java.util.UUID

@Configuration
class DbDemoDataInitializerConfig {
	@Bean
	fun databaseInitializer(
            courierProfileRepository: CourierProfileRepository, 
            operationAreaRepository: OperationAreaRepository,
            taskRepository: TaskRepository,
            venueProfileRepository: VenueProfileRepository,
            consumerProfileRepository: ConsumerProfileRepository
    ) = ApplicationRunner {
        // Initialize the database with some default data
        if (operationAreaRepository.findByName("Tokyo").isNullOrEmpty()){
            val savedArea = operationAreaRepository.save(
                OperationArea(
                    name = "Tokyo")
            )
        }

// Courier Profiles
        val courierJohn: CourierProfile = courierProfileRepository.save(
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

        val courierJane: CourierProfile = courierProfileRepository.save(
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

        val courierAlice: CourierProfile = courierProfileRepository.save(
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
    // Consumer Profiles
        val consumerAlice: ConsumerProfile = consumerProfileRepository.save(
            ConsumerProfile(
                name = "Alice Smith",
                emailAddress = "alicesmith@gmail.com",
                hashPassword = "hashed_password",
                homeAddress = "123 Main St, Tokyo",
                homeAddressCoordinate = Coordinate(latitude = 0.0, longitude = 0.0),
            )
        )
        val consumerBob: ConsumerProfile = consumerProfileRepository.save(
            ConsumerProfile(
                name = "Bob Johnson",
                emailAddress = "bobjonhson@gmail.com",
                hashPassword = "another_hashed_password",
                homeAddress = "456 Elm St, Tokyo",
                homeAddressCoordinate = Coordinate(latitude = 0.0, longitude = 0.0),
            )
        )


    // Venue Profiles
        val venueKfc: VenueProfile = venueProfileRepository.save(
            VenueProfile(
                venueName = "KFC",
                phoneNumber = "111-222-3333",
                venueStatus = VenueStatus.OPEN,
                venueAddress = "123 Fried Chicken St, Tokyo",
                venueAddressCoordiate = Coordinate(latitude = 35.6895, longitude = 139.6917),                
                venueComments = "Famous fried chicken restaurant"
            )
        )

        val venueFiveGuys: VenueProfile = venueProfileRepository.save(
            VenueProfile(
                venueName = "Five Guys",
                phoneNumber = "444-555-6666",
                venueStatus = VenueStatus.OPEN,
                venueAddress = "456 Burger Ave, Tokyo",
                venueAddressCoordiate = Coordinate(latitude = 35.6762, longitude = 139.6503),                
                venueComments = "Popular burger joint"
            )
        )
        
        taskRepository.save(
            Task(
                consumerProfile = consumerAlice,
                venueProfile = venueKfc,          
                taskStatus = TaskStatus.READY_FOR_ASSIGNMENT
            )
        )

        taskRepository.save(
            Task(
                consumerProfile = consumerBob,
                venueProfile = venueFiveGuys,
                taskStatus = TaskStatus.CREATED
             )
         )

         taskRepository.save(
            Task(
                consumerProfile = consumerAlice,
                venueProfile = venueFiveGuys,
                taskStatus = TaskStatus.DELIVERED,
                courierProfile = courierJohn
             )
         )
    }
}
