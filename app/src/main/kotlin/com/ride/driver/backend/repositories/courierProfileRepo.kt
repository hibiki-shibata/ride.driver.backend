// https://spring.io/projects/spring-data-jpa
package com.ride.driver.backend.repositories

import com.ride.driver.backend.models.DriverDetails
import org.springframework.data.repository.Repository
import java.util.UUID


open interface CourierProfileRepository : Repository<DriverDetails, UUID> {
   fun save(driverDetails: DriverDetails): DriverDetails
   fun findById(id: UUID): DriverDetails?
}



// open interface CourierProfileRepositoryCustom: Repository<DriverDetails, Long> {
//     open fun findDriversByStatus(status: DriverStatus): List<DriverDetails>    
// }