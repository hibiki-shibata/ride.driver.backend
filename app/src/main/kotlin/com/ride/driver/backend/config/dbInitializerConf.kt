// package com.ride.driver.backend.config

// import org.springframework.boot.ApplicationRunner
// import org.springframework.context.annotation.Bean
// import org.springframework.context.annotation.Configuration
// import com.ride.driver.backend.repositories.CourierProfileRepository
// import com.ride.driver.backend.repositories.OperationAreaRepository
// import com.ride.driver.backend.models.CourierProfile
// import com.ride.driver.backend.models.OperationArea
// import com.ride.driver.backend.models.VehicleType
// import com.ride.driver.backend.models.CourierStatus
// import java.util.UUID

// @Configuration
// class BlogConfiguration {
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
//                 id = null,
//                 phoneNumber = "+819012345678",
//                 name = "Test Courier 1",
//                 vehicleType = VehicleType.BIKE,
//                 rate = 5.0,
//                 status = CourierStatus.AVAILABLE,
//                 operationArea = tokyoArea[0],
//                 comments = "Ready to deliver"
//             )
//         )
// 	}
// }