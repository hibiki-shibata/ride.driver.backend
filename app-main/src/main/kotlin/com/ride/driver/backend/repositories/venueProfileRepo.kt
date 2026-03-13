// https://spring.io/projects/spring-data-jpa
package com.ride.driver.backend.repositories

import org.springframework.data.repository.CrudRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import com.ride.driver.backend.models.VenueProfile.VenueProfile
import java.util.UUID

@Repository
open interface VenueProfileRepository : CrudRepository<VenueProfile, Long> {
   fun save(VenueProfile: VenueProfile): VenueProfile
   fun findByPhoneNumber(phoneNumber: String): VenueProfile?
   fun findByVenueStatus(venueStatus: String): List<VenueProfile>
   fun findByVenueName(venueName: String): List<VenueProfile>
   fun findById(id: Long): VenueProfile?
}