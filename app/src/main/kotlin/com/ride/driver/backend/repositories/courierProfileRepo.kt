// https://spring.io/projects/spring-data-jpa
package com.ride.driver.backend.repositories

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

import com.ride.driver.backend.models.CourierProfile
import com.ride.driver.backend.models.Area


// Spring's reflection mechanism will automatically read the annotations of the DriverDetails class
@Repository
open interface CourierProfileRepository : CrudRepository<CourierProfile, Long> {
   override fun findAll(): List<CourierProfile>
   fun findById(id: UUID): CourierProfile?
   fun save(courier: CourierProfile): CourierProfile
}

@Repository
open interface AreaRepository : CrudRepository<Area, Long> {
    override fun findAll(): List<Area>
    fun findByName(name: String): List<Area>?
    fun save(area: Area?): Area
}