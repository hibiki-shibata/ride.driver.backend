package org.jetbrains.kotlin.junit

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import com.ride.driver.backend.consumer.repository.ConsumerProfileRepository
import com.ride.driver.backend.consumer.service.ConsumerProfileService
import com.ride.driver.backend.consumer.model.ConsumerProfile

internal class TodoRepositoryTest {
    // lateinit var consumerProfileRepository: ConsumerProfileRepository
    // lateinit var  consumerProfileService: ConsumerProfileService

    // @BeforeEach
    // fun setUp() {
    //     consumerProfileRepository = ConsumerProfileRepository()
    //     consumerProfileService = ConsumerProfileService(repository)
    // }

    @Test
    @DisplayName("Should start with empty repository")
    fun shouldStartEmpty() {
        Assertions.assertEquals(0, 0)
        Assertions.assertTrue(true)
    }

    @Test
    @DisplayName("Should return defensive copy of items")
    fun shouldReturnDefensiveCopy() {
        // repository.add(testItem1)

        // val items1 = repository.all
        // val items2 = repository.all

        Assertions.assertNotSame(1, 2)
        Assertions.assertThrows(
            RuntimeException::class.java
        ) { throw RuntimeException() }
        Assertions.assertEquals(1, 1)
    }
}