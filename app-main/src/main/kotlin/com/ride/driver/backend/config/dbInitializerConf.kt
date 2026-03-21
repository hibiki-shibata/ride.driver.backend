package com.ride.driver.backend.config

import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import com.ride.driver.backend.courier.repository.CourierProfileRepository
import com.ride.driver.backend.courier.repository.OperationAreaRepository
import com.ride.driver.backend.courier.model.CourierProfile
import com.ride.driver.backend.courier.model.CourierStatus
import com.ride.driver.backend.courier.model.OperationArea
import com.ride.driver.backend.courier.model.VehicleType
import com.ride.driver.backend.shared.model.Coordinate
import com.ride.driver.backend.logistic.model.Task
import com.ride.driver.backend.logistic.model.TaskStatus
import com.ride.driver.backend.logistic.model.OrderedItem
import com.ride.driver.backend.logistic.repository.TaskRepository
import com.ride.driver.backend.merchant.model.MerchantProfile
import com.ride.driver.backend.merchant.model.MerchantStatus
import com.ride.driver.backend.merchant.repository.MerchantProfileRepository
import com.ride.driver.backend.merchant.repository.MerchantItemRepository
import com.ride.driver.backend.merchant.model.MerchantItem
import com.ride.driver.backend.consumer.model.ConsumerProfile
import com.ride.driver.backend.consumer.repository.ConsumerProfileRepository
import java.util.UUID

@Configuration
@Profile("demo", "test", "local", "staging", "development", "dev")
class DbDemoDataInitializerConfig{
	@Bean
	fun databaseInitializer(
            courierProfileRepository: CourierProfileRepository, 
            operationAreaRepository: OperationAreaRepository,
            taskRepository: TaskRepository,
            merchantProfileRepository: MerchantProfileRepository,
            merchantItemRepository: MerchantItemRepository,
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
                consumerAddress = "123 Main St, Tokyo",
                consumerAddressCoordinate = Coordinate(latitude = 0.0, longitude = 0.0),
            )
        )
        val consumerBob: ConsumerProfile = consumerProfileRepository.save(
            ConsumerProfile(
                name = "Bob Johnson",
                emailAddress = "bobjonhson@gmail.com",
                passwordHash = "another_hashed_password",
                consumerAddress = "456 Elm St, Tokyo",
                consumerAddressCoordinate = Coordinate(latitude = 0.0, longitude = 0.0),
            )
        )

    // merchant Profiles
        val merchantKfc: MerchantProfile = merchantProfileRepository.save(
            MerchantProfile(
                name = "KFC",
                phoneNumber = "111-222-3333",
                merchantStatus = MerchantStatus.OPEN,
                merchantAddress = "123 Fried Chicken St, Tokyo",
                merchantAddressCoordinate = Coordinate(latitude = 35.6895, longitude = 139.6917),
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
                merchantAddressCoordinate = Coordinate(latitude = 35.6762, longitude = 139.6503),                
                passwordHash = "fiveguys_hashed_password",
                merchantComments = "Popular burger joint"
            )
        )

        // Merchant Items
        val kfcMenuItem1: MerchantItem = merchantItemRepository.save(
            MerchantItem(
                name = "Original Recipe Chicken",
                description = "Classic KFC chicken with 11 herbs and spices",
                price = 5.99,
                merchantProfile = merchantKfc
            )
        )

        val kfcMenuItem2: MerchantItem = merchantItemRepository.save(
            MerchantItem(
                name = "Extra Crispy Chicken",
                description = "Crispy fried chicken with a crunchy coating",
                price = 6.49,
                merchantProfile = merchantKfc
            )
        )

        val fiveGuysMenuItem1: MerchantItem = merchantItemRepository.save(
            MerchantItem(
                name = "Bacon Cheeseburger",
                description = "Juicy burger with crispy bacon and melted cheese",
                price = 8.99,
                merchantProfile = merchantFiveGuys
            )
        )

        val fiveGuysMenuItem2: MerchantItem = merchantItemRepository.save(
            MerchantItem(
                name = "Fries",
                description = "Crispy golden fries",
                price = 3.49,
                merchantProfile = merchantFiveGuys
            )
        )

        // Tasks
        val firstTask: Task = taskRepository.save(
            Task(
                consumerProfile = consumerAlice,
                merchantProfile = merchantKfc,          
                taskStatus = TaskStatus.READY_FOR_ASSIGNMENT,
                orderedItems = listOf(
                    OrderedItem(
                        itemId = kfcMenuItem1.id ?: throw Exception("Menu item ID is null"),
                        name = kfcMenuItem1.name,
                        description = kfcMenuItem1.description,
                        price = kfcMenuItem1.price,
                        merchantId = kfcMenuItem1.merchantProfile.id ?: throw Exception("Merchant profile ID is null")
                    ),
                    OrderedItem(
                        itemId = kfcMenuItem2.id ?: throw Exception("Menu item ID is null"),
                        name = kfcMenuItem2.name,
                        description = kfcMenuItem2.description,
                        price = kfcMenuItem2.price,
                        merchantId = kfcMenuItem2.merchantProfile.id ?: throw Exception("Merchant profile ID is null")
                    )
                )
            )
        )

        val secondTask: Task = taskRepository.save(
            Task(
                consumerProfile = consumerBob,
                merchantProfile = merchantFiveGuys,
                taskStatus = TaskStatus.READY_FOR_ASSIGNMENT,
                orderedItems = listOf(
                    OrderedItem(
                        itemId = fiveGuysMenuItem1.id ?: throw Exception("Menu item ID is null"),
                        name = fiveGuysMenuItem1.name,
                        description = fiveGuysMenuItem1.description,
                        price = fiveGuysMenuItem1.price,
                        merchantId = fiveGuysMenuItem1.merchantProfile.id ?: throw Exception("Merchant profile ID is null")
                    ),
                    OrderedItem(
                        itemId = fiveGuysMenuItem2.id ?: throw Exception("Menu item ID is null"),
                        name = fiveGuysMenuItem2.name,
                        description = fiveGuysMenuItem2.description,
                        price = fiveGuysMenuItem2.price,
                        merchantId = fiveGuysMenuItem2.merchantProfile.id ?: throw Exception("Merchant profile ID is null")
                    )
                )
             )
         )

        val thirdTask: Task = taskRepository.save(
            Task(
                consumerProfile = consumerBob,
                merchantProfile = merchantFiveGuys,
                taskStatus = TaskStatus.CREATED,
                orderedItems = listOf(
                    OrderedItem(
                        itemId = fiveGuysMenuItem1.id ?: throw Exception("Menu item ID is null"),
                        name = fiveGuysMenuItem1.name,
                        description = fiveGuysMenuItem1.description,
                        price = fiveGuysMenuItem1.price,
                        merchantId = fiveGuysMenuItem1.merchantProfile.id ?: throw Exception("Merchant profile ID is null")
                    )
                )
             )
         )

        val forthTask: Task = taskRepository.save(
        Task(
            consumerProfile = consumerAlice,
            merchantProfile = merchantFiveGuys,
            taskStatus = TaskStatus.DELIVERED,
            courierProfile = courierJohn,
            orderedItems = listOf(
                OrderedItem(
                    itemId = fiveGuysMenuItem2.id ?: throw Exception("Menu item ID is null"),
                    name = fiveGuysMenuItem2.name,
                    description = fiveGuysMenuItem2.description,
                    price = fiveGuysMenuItem2.price,
                    merchantId = fiveGuysMenuItem2.merchantProfile.id ?: throw Exception("Merchant profile ID is null")
                ),
                OrderedItem(
                    itemId = fiveGuysMenuItem1.id ?: throw Exception("Menu item ID is null"),
                    name = fiveGuysMenuItem1.name,
                    description = fiveGuysMenuItem1.description,
                    price = fiveGuysMenuItem1.price,
                    merchantId = fiveGuysMenuItem1.merchantProfile.id ?: throw Exception("Merchant profile ID is null")
                )
            )
         )
        )
    }
}
