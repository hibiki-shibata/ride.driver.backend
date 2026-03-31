// // https://spring.io/projects/spring-data-jpa
// package com.ride.driver.backend.consumer.repository

// import org.springframework.data.repository.CrudRepository
// import org.springframework.data.domain.Page
// import org.springframework.data.domain.Pageable
// import org.springframework.stereotype.Repository
// import java.util.UUID
// import com.ride.driver.backend.consumer.model.ConsumerProfile

// @Repository
// open interface ConsumerProfileRepository : CrudRepository<ConsumerProfile, Long> {
//    fun save(consumerProfile: ConsumerProfile): ConsumerProfile
//    fun existsByEmailAddress(emailAddress: String): Boolean
//    fun findById(id: UUID): ConsumerProfile
//    fun findByEmailAddress(emailAddress: String): ConsumerProfile
//    fun findAll(pageable: Pageable): Page<ConsumerProfile>
// }


package com.ride.driver.backend.consumer.repository

import com.ride.driver.backend.consumer.model.ConsumerProfile
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ConsumerProfileRepository : JpaRepository<ConsumerProfile, UUID> {
    fun existsByEmailAddress(emailAddress: String): Boolean
    fun findByEmailAddress(emailAddress: String): ConsumerProfile?
    fun findByEmailAddressIgnoreCase(emailAddress: String): ConsumerProfile?
   //  fun findAllByEmailAddress(emailAddress: String, pageable: Pageable): Page<ConsumerProfile>
}

// import org.springframework.data.domain.Page
// import org.springframework.data.domain.Pageable
// import org.springframework.data.domain.PageRequest

// val pageRequest = PageRequest.of(0, 20) // page 0, size 20
// val pagePage<ConsumerProfile> = consumerProfileRepository.findAll(pageRequest)