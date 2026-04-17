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
import org.springframework.data.domain.PageRequest

@Configuration
@Profile("demo", "test", "local", "staging", "development", "dev")
class DemoDBDataInitializerConfig {
	@Bean
	fun databaseInitializer(
            consumerProfileRepository: ConsumerProfileRepository,
            courierProfileRepository: CourierProfileRepository, 
            merchantProfileRepository: MerchantProfileRepository,
            merchantItemRepository: MerchantItemRepository,
            operationAreaRepository: OperationAreaRepository,
            taskRepository: TaskRepository
    ) = ApplicationRunner {
        // Operation Areas
        for (i in 1..5) {
                if (operationAreaRepository.findByName("Area $i") == null) {
                    operationAreaRepository.save(
                        OperationArea(
                            name = "Area $i"
                        )
                    )
                }
            }

        // Courier Profiles
        for (i in 1..10) {
            courierProfileRepository.save(
                CourierProfile(
                    name = "Courier $i",
                    phoneNumber = "123-456-78${String.format("%02d", i)}",
                    vehicleType = VehicleType.BICYCLE,
                    passwordHash = "hashed_password_$i",
                    cpRate = 4.0 + (i % 5) * 0.5, // Varying ratings between 4.0 and 4.5
                    cpStatus = (i % 2 == 0).let { if (it) CourierStatus.ONLINE else CourierStatus.OFFLINE },
                    currentLocation = Coordinate(latitude = 35.0 + i * 0.1, longitude = 139.0 + i * 0.1),
                    operationArea = operationAreaRepository.findByName("Area ${(i % 5) + 1}") ?: throw Exception("Operation area not found"),
                    cpComments = "Courier number $i"
                )
            )
        }
        
        // Consumer Profiles
        for (i in 1..10) {
            consumerProfileRepository.save(
                ConsumerProfile(
                    name = "Consumer $i",
                    emailAddress = "$i" + "_test@gmail.com",
                    passwordHash = "hashed_password_$i",
                    consumerAddress = "St ${i}, Alexanderplatz, Berlin",
                    consumerAddressCoordinate = Coordinate(latitude = 35.0 + i * 0.1, longitude = 139.0 + i * 0.1),
                )
            )
        }

        // merchant Profiles
        for (i in 1..10) {
            merchantProfileRepository.save(
                MerchantProfile(
                    name = (i % 2 == 0).let { if (it) "KFC $i" else "Five Guys $i" },
                    phoneNumber = "111-222-33${String.format("%02d", i)}",
                    merchantStatus = MerchantStatus.OPEN,
                    merchantAddress = "${i} St, Shibuya, Tokyo",
                    merchantAddressCoordinate = Coordinate(latitude = 35.0 + i * 0.1, longitude = 139.0 + i * 0.1),
                    passwordHash = "hashed_password_$i",
                    merchantComments = "Hi! Please Order from our restaurant $i"
                )
            )
        }

        // Merchant Items
        val merchantProfiles: List<MerchantProfile> = merchantProfileRepository.findAll()
        for (merchantProfile in merchantProfiles) {
            for (j in 1..30) {
                merchantItemRepository.save(
                    MerchantItem(
                        name = "Burger set $j for ${merchantProfile.name}",
                        description = "Dip potate with fuckin ketchap $j of ${merchantProfile.name}",
                        // random price
                        price = (0..30).random().toDouble(),
                        merchantProfile = merchantProfile
                    )
                )
            }
        }

        // Tasks
        val consumerProfiles: List<ConsumerProfile> = consumerProfileRepository.findAll()
        val merchantItems: List<MerchantItem> = merchantItemRepository.findAll()
        for (i in 1..20) {
            taskRepository.save(
                Task(
                    consumerProfile = consumerProfiles[i % consumerProfiles.size],
                    merchantProfile = merchantItems[i % merchantItems.size].merchantProfile,          
                    taskStatus = TaskStatus.READY_FOR_ASSIGNMENT,
                    orderedItems = listOf(
                        OrderedItem(
                            itemId = merchantItems[i % merchantItems.size].id ?: throw Exception("Menu item ID is null"),
                            name = merchantItems[i % merchantItems.size].name,
                            description = merchantItems[i % merchantItems.size].description,
                            price = merchantItems[i % merchantItems.size].price,
                            merchantId = merchantItems[i % merchantItems.size].merchantProfile.id ?: throw Exception("Merchant profile ID is null")
                        ),
                        OrderedItem(
                            itemId = merchantItems[(i + 1) % merchantItems.size].id ?: throw Exception("Menu item ID is null"),
                            name = merchantItems[(i + 1) % merchantItems.size].name,
                            description = merchantItems[(i + 1) % merchantItems.size].description,
                            price = merchantItems[(i + 1) % merchantItems.size].price,
                            merchantId = merchantItems[(i + 1) % merchantItems.size].merchantProfile.id ?: throw Exception("Merchant profile ID is null")
                        )
                    )
                )
            )
        }
        // val firstTask: Task = taskRepository.save(
        //     Task(
        //         consumerProfile = consumerAlice,
        //         merchantProfile = merchantKfc,          
        //         taskStatus = TaskStatus.READY_FOR_ASSIGNMENT,
        //         orderedItems = listOf(
        //             OrderedItem(
        //                 itemId = kfcMenuItem1.id ?: throw Exception("Menu item ID is null"),
        //                 name = kfcMenuItem1.name,
        //                 description = kfcMenuItem1.description,
        //                 price = kfcMenuItem1.price,
        //                 merchantId = kfcMenuItem1.merchantProfile.id ?: throw Exception("Merchant profile ID is null")
        //             ),
        //             OrderedItem(
        //                 itemId = kfcMenuItem2.id ?: throw Exception("Menu item ID is null"),
        //                 name = kfcMenuItem2.name,
        //                 description = kfcMenuItem2.description,
        //                 price = kfcMenuItem2.price,
        //                 merchantId = kfcMenuItem2.merchantProfile.id ?: throw Exception("Merchant profile ID is null")
        //             )
        //         )
        //     )
        // )
    }
} 
