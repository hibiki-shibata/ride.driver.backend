// package com.ride.driver.backend.consumer.serviceTest

// import com.ride.driver.backend.consumer.dto.ConsumerProfileReqDTO
// import com.ride.driver.backend.consumer.dto.ConsumerProfileResDTO
// import com.ride.driver.backend.consumer.mapper.toAccessTokenClaim
// import com.ride.driver.backend.consumer.mapper.toConsumerProfileResDTO
// import com.ride.driver.backend.consumer.model.ConsumerProfile
// import com.ride.driver.backend.consumer.service.ConsumerProfileService
// import com.ride.driver.backend.consumer.repository.ConsumerProfileRepository
// import com.ride.driver.backend.shared.model.Coordinate
// // import org.springframework.boot.test.context.SpringBootTest
// import org.junit.jupiter.api.Assertions.assertEquals
// import org.junit.jupiter.api.Test
// import java.util.UUID

// class ConsumerProfileServiceTest {

//     @Test
//     fun `test mapping from ConsumerProfile to ConsumerProfileResDTO`() {
//         val consumerProfile = ConsumerProfile(
//             id =  UUID.randomUUID(), 
//             name = "Hibiki Test",
//             emailAddress = "hibikitest@gmail.com",
//             consumerAddress = "123 Test Street",
//             consumerAddressCoordinate = Coordinate(37.7749, -122.4194),
//             passwordHash = "hashedpassword"
//         )

//         val expectedResDTO = ConsumerProfileResDTO(
//             name = "Hibiki Test",
//             emailAddress = "hibikitest@gmail.com",
//             consumerAddress = "123 Test Street",
//             consumerAddressCoordinate = Coordinate(37.7749, -122.4194)
//         )

//         val actualResDTO = consumerProfile.toConsumerProfileResDTO()
//         assertEquals(expectedResDTO, actualResDTO)
//     }
// }


// class BankAccountServiceTest {
//     val consumerProfileRepository: ConsumerProfileRepository = mockk()
//     val consumerProfileService = ConsumerProfileService(consumerProfileRepository)

//     @Test
//     fun whenGetBankAccount_thenReturnBankAccount() {
//         //given
//         every { consumerProfileRepository.findByIdOrNull(1) } returns consumerProfile

//         //when
//         val result = consumerProfileService.getConsumerProfile(1)

//         //then
//         verify(exactly = 1) { consumerProfileRepository.findByIdOrNull(1) };
//         assertEquals(consumerProfile, result)
//     }
// }