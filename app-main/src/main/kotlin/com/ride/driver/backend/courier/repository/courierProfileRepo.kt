// https://spring.io/projects/spring-data-jpa
package com.ride.driver.backend.courier.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import com.ride.driver.backend.courier.model.CourierProfile
import com.ride.driver.backend.courier.model.OperationArea
import com.ride.driver.backend.courier.model.CourierStatus
import java.util.UUID

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

@Repository
interface CourierProfileRepository : JpaRepository<CourierProfile, UUID> {   
   fun existsByPhoneNumber(phoneNumber: String): Boolean   
   fun findByCpStatus(cpStatus: CourierStatus): List<CourierProfile>
   fun findByPhoneNumber(phoneNumber: String): CourierProfile?
//    fun findAll(pageable: Pageable): Page<CourierProfile>
   // next method is for paginated and sorted fetch all courier data but certain page
}

@Repository
interface OperationAreaRepository : JpaRepository<OperationArea, Long> {
    fun findByName(name: String): List<OperationArea>
    override fun findAll(pageable: Pageable): Page<OperationArea>
}