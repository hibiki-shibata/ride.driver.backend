// https://spring.io/projects/spring-data-jpa
package com.ride.driver.backend.repositories

import com.ride.driver.backend.models.DriverDetails
import org.springframework.data.repository.CrudRepository
import java.util.UUID


open interface CourierProfileRepository : CrudRepository<DriverDetails, Long> {
   fun findById(id: UUID): DriverDetails?
}


