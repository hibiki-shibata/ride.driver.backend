// paxkage com.ride.driver.backend.consumer.controllerTest

// import com.fasterxml.jackson.databind.ObjectMapper
// import io.mockk.every
// import org.junit.jupiter.api.Test
// import org.springframework.beans.factory.annotation.Autowired
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
// import org.springframework.boot.test.context.TestConfiguration
// import org.springframework.context.annotation.Bean
// import org.springframework.context.annotation.Import
// import org.springframework.http.MediaType
// import org.springframework.test.web.servlet.MockMvc
// import org.springframework.test.web.servlet.post
// import org.springframework.test.web.servlet.get

// @WebMvcTest(UserController::class)
// @Import(UserControllerTest.MockkConfig::class)
// class UserControllerTest(
//     @Autowired private val mockMvc: MockMvc,
//     @Autowired private val objectMapper: ObjectMapper,
//     @Autowired private val userService: UserService
// ) {

//     @TestConfiguration
//     class MockkConfig {
//         @Bean
//         fun userService(): UserService = io.mockk.mockk()
//     }

//     @Test
//     fun `POST users should return 201 and response body`() {
//         // given
//         val request = CreateUserRequest(
//             email = "test@example.com",
//             name = "Hibiki"
//         )

//         every { userService.createUser(any()) } returns UserResponse(
//             id = 1L,
//             email = "test@example.com",
//             name = "Hibiki"
//         )

//         // when & then
//         mockMvc.post("/users") {
//             contentType = MediaType.APPLICATION_JSON
//             content = objectMapper.writeValueAsString(request)
//         }
//             .andExpect {
//                 status { isCreated() }
//                 jsonPath("$.id") { value(1) }
//                 jsonPath("$.email") { value("test@example.com") }
//                 jsonPath("$.name") { value("Hibiki") }
//             }
//     }

//     @Test
//     fun `GET user by id should return 200 and response body`() {
//         // given
//         every { userService.getUser(1L) } returns UserResponse(
//             id = 1L,
//             email = "test@example.com",
//             name = "Hibiki"
//         )

//         // when & then
//         mockMvc.get("/users/1")
//             .andExpect {
//                 status { isOk() }
//                 jsonPath("$.id") { value(1) }
//                 jsonPath("$.email") { value("test@example.com") }
//                 jsonPath("$.name") { value("Hibiki") }
//             }
//     }
// }