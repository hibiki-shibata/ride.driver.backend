package com.ride.driver.backend.courier.controller

// import org.junit.jupiter.api.Test
// import org.springframework.beans.factory.annotation.Autowired
// import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient
// import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
// import org.springframework.test.web.servlet.client.RestTestClient
// import org.springframework.test.web.servlet.client.expectBody

// @WebMvcTest(CourierAuthController::class)
// @AutoConfigureRestTestClient
// class WebLayerTest(@Autowired private val restTestClient: RestTestClient) {

//     @Test
//     fun greetingShouldReturnDefaultMessage() {
//         // Import Kotlin .expectBody() extension that allows using reified type parameters
//         restTestClient.get()
//             .uri("/api/v1/couriers/auth/signup")
//             .exchange()
//             .expectStatus().isOk()
//             .expectBody<String>()
//             .isEqualTo("Hello, World")
//     }

// }