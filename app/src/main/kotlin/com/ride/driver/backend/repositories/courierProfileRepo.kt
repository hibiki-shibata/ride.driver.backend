// https://spring.io/projects/spring-data-jpa
package com.ride.driver.backend.repositories

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

import com.ride.driver.backend.models.CourierProfile
import com.ride.driver.backend.models.OperationArea


// Spring's reflection mechanism will automatically read the annotations of the DriverDetails class
@Repository
open interface CourierProfileRepository : CrudRepository<CourierProfile, Long> {
   override fun findAll(): List<CourierProfile>
   fun findById(id: UUID): CourierProfile?
   fun save(courier: CourierProfile): CourierProfile
}

@Repository
open interface OperationAreaRepository : CrudRepository<OperationArea, Long> {
    override fun findAll(): List<OperationArea>
    fun findByName(name: String): List<OperationArea>?
    fun save(area: OperationArea?): OperationArea
}