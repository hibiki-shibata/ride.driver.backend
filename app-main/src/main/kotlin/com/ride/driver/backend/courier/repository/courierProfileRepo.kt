// https://spring.io/projects/spring-data-jpa
package com.ride.driver.backend.courier.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import com.ride.driver.backend.courier.model.CourierProfile
import com.ride.driver.backend.courier.model.OperationArea
import com.ride.driver.backend.courier.model.CourierStatus
import java.util.UUID

@Repository
open interface CourierProfileRepository : CrudRepository<CourierProfile, Long> {
   fun save(courierProfile: CourierProfile): CourierProfile
   fun existsByPhoneNumber(phoneNumber: String): Boolean
   fun findById(id: UUID): CourierProfile?
   fun findByCpStatus(cpStatus: CourierStatus): List<CourierProfile>
   fun findByPhoneNumber(phoneNumber: String): CourierProfile
//    fun findAll(pageable: Pageable): Page<CourierProfile>
   // next method is for paginated and sorted fetch all courier data but certain page
}

@Repository
open interface OperationAreaRepository : CrudRepository<OperationArea, Long> {
    override fun findAll(): List<OperationArea>
    fun findByName(name: String): List<OperationArea>?
    fun save(area: OperationArea?): OperationArea
}