package com.ride.driver.backend.config

import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import com.ride.driver.backend.courier.repositories.CourierProfileRepository
import com.ride.driver.backend.courier.repositories.OperationAreaRepository
import com.ride.driver.backend.courier.models.CourierProfile
import com.ride.driver.backend.courier.models.CourierStatus
import com.ride.driver.backend.courier.models.OperationArea
import com.ride.driver.backend.courier.models.VehicleType
import com.ride.driver.backend.shared.models.Coordinate
import com.ride.driver.backend.logistic.models.Task
import com.ride.driver.backend.logistic.models.TaskStatus
import com.ride.driver.backend.logistic.repositories.TaskRepository
import com.ride.driver.backend.merchant.models.MerchantProfile
import com.ride.driver.backend.merchant.models.MerchantStatus
import com.ride.driver.backend.merchant.repositories.MerchantProfileRepository
import com.ride.driver.backend.consumer.models.ConsumerProfile
import com.ride.driver.backend.consumer.repositories.ConsumerProfileRepository
import java.util.UUID

@Configuration
class DbDemoDataInitializerConfig {
	@Bean
	fun databaseInitializer(
            courierProfileRepository: CourierProfileRepository, 
            operationAreaRepository: OperationAreaRepository,
            taskRepository: TaskRepository,
            merchantProfileRepository: MerchantProfileRepository,
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
                name = "John Doe",
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
                name = "Jane Smith",
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
                name = "Alice Johnson",
                phoneNumber = "555-123-4567",
                vehicleType = VehicleType.BICYCLE,
                passwordHash = "alice_hashed_password",
                cpRate = 4.2,
                cpStatus = CourierStatus.ONLINE,
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
                passwordHash = "hashed_password",
                homeAddress = "123 Main St, Tokyo",
                homeAddressCoordinate = Coordinate(latitude = 0.0, longitude = 0.0),
            )
        )
        val consumerBob: ConsumerProfile = consumerProfileRepository.save(
            ConsumerProfile(
                name = "Bob Johnson",
                emailAddress = "bobjonhson@gmail.com",
                passwordHash = "another_hashed_password",
                homeAddress = "456 Elm St, Tokyo",
                homeAddressCoordinate = Coordinate(latitude = 0.0, longitude = 0.0),
            )
        )

    // merchant Profiles
        val merchantKfc: MerchantProfile = merchantProfileRepository.save(
            MerchantProfile(
                name = "KFC",
                phoneNumber = "111-222-3333",
                merchantStatus = MerchantStatus.OPEN,
                merchantAddress = "123 Fried Chicken St, Tokyo",
                merchantAddressCoordiate = Coordinate(latitude = 35.6895, longitude = 139.6917),
                passwordHash = "kfc_hashed_password",
                merchantComments = "Famous fried chicken restaurant"
            )
        )

        val merchantFiveGuys: MerchantProfile = merchantProfileRepository.save(
            MerchantProfile(
                name = "Five Guys",
                phoneNumber = "444-555-6666",
                merchantStatus = MerchantStatus.OPEN,
                merchantAddress = "456 Burger Ave, Tokyo",
                merchantAddressCoordiate = Coordinate(latitude = 35.6762, longitude = 139.6503),                
                passwordHash = "fiveguys_hashed_password",
                merchantComments = "Popular burger joint"
            )
        )
        
        taskRepository.save(
            Task(
                consumerProfile = consumerAlice,
                merchantProfile = merchantKfc,          
                taskStatus = TaskStatus.READY_FOR_ASSIGNMENT
            )
        )

        taskRepository.save(
            Task(
                consumerProfile = consumerBob,
                merchantProfile = merchantFiveGuys,
                taskStatus = TaskStatus.READY_FOR_ASSIGNMENT
             )
         )

        taskRepository.save(
            Task(
                consumerProfile = consumerBob,
                merchantProfile = merchantFiveGuys,
                taskStatus = TaskStatus.CREATED
             )
         )

        taskRepository.save(
        Task(
            consumerProfile = consumerAlice,
            merchantProfile = merchantFiveGuys,
            taskStatus = TaskStatus.DELIVERED,
            courierProfile = courierJohn
            )
         )
    }
}
