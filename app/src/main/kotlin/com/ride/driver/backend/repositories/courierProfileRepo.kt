// https://spring.io/projects/spring-data-jpa
package com.ride.driver.backend.repositories

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import com.ride.driver.backend.models.CourierProfile
import com.ride.driver.backend.models.OperationArea
import java.util.UUID

@Repository
open interface CourierProfileRepository : CrudRepository<CourierProfile, Long> {
   override fun findAll(): List<CourierProfile>
   fun save(courierProfile: CourierProfile): CourierProfile
   fun existsByPhoneNumber(phoneNumber: String): Boolean
   fun findById(id: UUID): CourierProfile?
   fun findByPhoneNumber(phoneNumber: String): CourierProfile?
}

@Repository
open interface OperationAreaRepository : CrudRepository<OperationArea, Long> {
    override fun findAll(): List<OperationArea>
    fun findByName(name: String): List<OperationArea>?
    fun save(area: OperationArea?): OperationArea
}