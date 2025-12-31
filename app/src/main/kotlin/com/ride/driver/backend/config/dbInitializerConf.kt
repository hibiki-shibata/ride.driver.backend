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

// @Configuration
// class DbDemoDataInitializerConfig {
// 	@Bean
// 	fun databaseInitializer(
//             courierProfileRepository: CourierProfileRepository, 
//             operationAreaRepository: OperationAreaRepository 
//     ) = ApplicationRunner {
//         // Initialize the database with some default data
//         val savedArea = operationAreaRepository.save(
//             OperationArea(
//                 name = "Tokyo")
//         )

//         val tokyoArea = operationAreaRepository.findByName("Tokyo")
//             ?: throw IllegalStateException("Area 'Tokyo' not found")

//         courierProfileRepository.save(
//             CourierProfile(
//                 phoneNumber = "+819012345678",
//                 passwordHash = "hashed_password_1",
//                 name = "Test Courier 1",
//                 vehicleType = VehicleType.BIKE,
//                 rate = 5.0,
//                 status = CourierStatus.AVAILABLE,
//                 operationArea = tokyoArea[0],
//                 comments = "No comments"
//             )
//         )

//         courierProfileRepository.save(
//             CourierProfile(
//                 phoneNumber = "+819098765432",
//                 passwordHash = "hashed_password_2",
//                 name = "Test Courier 2",
//                 vehicleType = VehicleType.CAR,
//                 rate = 4.5,
//                 status = CourierStatus.UNAVAILABLE,
//                 operationArea = tokyoArea[0],
//                 comments = "No comments"
//             )
//         )

//         courierProfileRepository.save(
//             CourierProfile(
//                 phoneNumber = "+819011122233",
//                 passwordHash = "hashed_password_3",
//                 name = "Test Courier 3",
//                 vehicleType = VehicleType.BIKE,
//                 rate = 4.8,
//                 status = CourierStatus.AVAILABLE,
//                 operationArea = tokyoArea[0],
//                 comments = "No comments"
//             )
//         )

//         courierProfileRepository.save(
//             CourierProfile(
//                 phoneNumber = "+819044455566",
//                 passwordHash = "hashed_password_4",
//                 name = "Test Courier 4",
//                 vehicleType = VehicleType.CAR,
//                 rate = 5.0,
//                 status = CourierStatus.AVAILABLE,
//                 operationArea = tokyoArea[0],
//                 comments = "No comments"
//             )
//         )

//         courierProfileRepository.save(
//             CourierProfile(
//                 phoneNumber = "+819077788899",
//                 passwordHash = "hashed_password_5",
//                 name = "Test Courier 5",
//                 vehicleType = VehicleType.BIKE,
//                 rate = 4.2,
//                 status = CourierStatus.UNAVAILABLE,
//                 operationArea = tokyoArea[0],
//                 comments = "No comments"
//             )
//         )

//         courierProfileRepository.save(
//             CourierProfile(
//                 phoneNumber = "+819000111222",
//                 passwordHash = "hashed_password_6",
//                 name = "Test Courier 6",
//                 vehicleType = VehicleType.CAR,
//                 rate = 4.9,
//                 status = CourierStatus.AVAILABLE,
//                 operationArea = tokyoArea[0],
//                 comments = "No comments"
//             )
//         )

//         courierProfileRepository.save(
//             CourierProfile(
//                 phoneNumber = "+819033344455",
//                 passwordHash = "hashed_password_7",
//                 name = "Test Courier 7",
//                 vehicleType = VehicleType.BIKE,
//                 rate = 5.0,
//                 status = CourierStatus.AVAILABLE,
//                 operationArea = tokyoArea[0],
//                 comments = "No comments"
//             )
//         )
//     }
// }