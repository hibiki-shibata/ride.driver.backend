// Doc: https://docs.spring.io/spring-framework/reference/testing/mockmvc.html
package com.ride.driver.backend.consumer.controllerTest

import com.ride.driver.backend.consumer.repository.ConsumerProfileRepository
import com.ride.driver.backend.consumer.controller.ConsumerProfileController
import com.ride.driver.backend.consumer.service.ConsumerProfileService
import com.ride.driver.backend.consumer.model.ConsumerProfile

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

// @SpringBootTest // I gotta run Postgres in advance when i use Spring test
// class ConsumerProfileController(
//     @Autowired private val controller: ConsumerProfileController
// ) {

//     @Test
//     fun contextLoads() {
//         assertThat(controller).isNotNull()

//     }
// }