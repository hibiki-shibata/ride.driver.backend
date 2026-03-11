// https://spring.io/projects/spring-data-jpa
package com.ride.driver.backend.repositories

import org.springframework.data.repository.CrudRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import com.ride.driver.backend.models.consumerProfile.ConsumerProfile
import java.util.UUID

@Repository
open interface ConsumerProfileRepository : CrudRepository<ConsumerProfile, Long> {
   fun save(consumerProfile: ConsumerProfile): ConsumerProfile
   fun existsByEmailAddress(emailAddress: String): Boolean
   fun findById(id: UUID): ConsumerProfile?
   fun findByEmailAddress(emailAddress: String): ConsumerProfile?
   fun findAll(pageable: Pageable): Page<ConsumerProfile>
}