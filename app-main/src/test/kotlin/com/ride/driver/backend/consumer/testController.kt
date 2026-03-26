// package com.ride.driver.backend.consumer.controller

// import com.ride.driver.backend.consumer.dto.ConsumerProfileResDTO
// import com.ride.driver.backend.consumer.service.ConsumerProfileService
// import org.junit.jupiter.api.Test
// import org.mockito.BDDMockito.given
// import org.springframework.beans.factory.annotation.Autowired
// import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
// import org.springframework.test.context.bean.override.mockito.MockitoBean
// import org.springframework.test.web.servlet.MockMvc
// import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
// import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

// @WebMvcTest(ConsumerProfileController::class)
// @AutoConfigureMockMvc(addFilters = false) // Disable security filters for testing
// class ConsumerProfileControllerTest(
//     @Autowired private val mockMvc: MockMvc
// ) {

//     @MockitoBean
//     lateinit var consumerProfileService: ConsumerProfileService

//     @Test
//     fun `request should reach controller`() {
//         // stub service here with given(...).willReturn(...)
//         mockMvc.perform(get("/api/v1/consumers/me"))
//             .andExpect(status().isOk)
//     }
// }

package com.ride.driver.backend.consumer.controller

import com.ride.driver.backend.consumer.dto.ConsumerProfileResDTO
import com.ride.driver.backend.consumer.service.ConsumerProfileService
import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
import com.ride.driver.backend.shared.auth.domain.AccountRoles
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.util.UUID
import com.ride.driver.backend.shared.model.Coordinate

class ConsumerProfileControllerUnitTest {

    private val consumerProfileService = mockk<ConsumerProfileService>()
    private val controller = ConsumerProfileController(consumerProfileService)

    @Test
    fun `getConsumerProfile returns 200 with body`() {
        val consumerId = UUID.randomUUID()

        val claim = AccessTokenClaim(
            accountId = consumerId,
            accountName = "hibiki",
            accountRoles = listOf(AccountRoles.BASE_CONSUMER_ROLE)
        )

        val response = ConsumerProfileResDTO(
            id = "UUID is expected here",
            name = "Hibiki",
            emailAddress = "hibiki@example.com",
            consumerAddress = "Tokyo",
            consumerAddressCoordinate = Coordinate(35.6895, 139.6917)
        )

        every { consumerProfileService.getConsumerProfile(claim) } returns response

        val result = controller.getConsumerProfile(claim)
        println(result.body)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(response, result.body)
    }
}