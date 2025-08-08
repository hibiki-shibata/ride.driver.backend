// https://spring.io/projects/spring-data-jpa
package com.ride.driver.backend.repositories

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID
import com.ride.driver.backend.models.DriverDetails
import com.ride.driver.backend.models.Area


// Spring's reflection mechanism will automatically read the annotations of the DriverDetails class
@Repository
open interface CourierProfileRepository : CrudRepository<DriverDetails, Long> {
   override fun findAll(): Iterable<DriverDetails>
   fun findById(id: UUID): DriverDetails?
}


@Repository
open interface AreaRepository : CrudRepository<Area, Long> {
    override fun findAll(): Iterable<Area>
    fun findByName(name: String): List<Area>?
}