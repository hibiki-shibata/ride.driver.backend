package com.ride.driver.backend.consumer.serviceTest

import com.ride.driver.backend.consumer.dto.ConsumerProfileReqDTO
import com.ride.driver.backend.consumer.dto.ConsumerProfileResDTO
import com.ride.driver.backend.consumer.mapper.toAccessTokenClaim
import com.ride.driver.backend.consumer.mapper.toConsumerProfileResDTO
import com.ride.driver.backend.consumer.model.ConsumerProfile
import com.ride.driver.backend.shared.model.Coordinate
// import org.springframework.boot.test.context.SpringBootTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.UUID

class ConsumerProfileServiceTest {

    @Test
    fun `test mapping from ConsumerProfile to ConsumerProfileResDTO`() {
        val consumerProfile = ConsumerProfile(
            id =  UUID.randomUUID(), 
            name = "Hibiki Test",
            emailAddress = "hibikitest@gmail.com",
            consumerAddress = "123 Test Street",
            consumerAddressCoordinate = Coordinate(37.7749, -122.4194),
            passwordHash = "hashedpassword"
        )

        val expectedResDTO = ConsumerProfileResDTO(
            name = "Hibiki Test",
            emailAddress = "hibikitest@gmail.com",
            consumerAddress = "123 Test Street",
            consumerAddressCoordinate = Coordinate(37.7749, -122.4194)
        )

        val actualResDTO = consumerProfile.toConsumerProfileResDTO()
        assertEquals(expectedResDTO, actualResDTO)
    }
}