// https://spring.io/projects/spring-data-jpa
package com.ride.driver.backend.consumer.repositories

import org.springframework.data.repository.CrudRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.util.UUID
import com.ride.driver.backend.consumer.models.ConsumerProfile

@Repository
open interface ConsumerProfileRepository : CrudRepository<ConsumerProfile, Long> {
   fun save(consumerProfile: ConsumerProfile): ConsumerProfile
   fun existsByEmailAddress(emailAddress: String): Boolean
   fun findById(id: UUID): ConsumerProfile
   fun findByEmailAddress(emailAddress: String): ConsumerProfile
   fun findAll(pageable: Pageable): Page<ConsumerProfile>
}