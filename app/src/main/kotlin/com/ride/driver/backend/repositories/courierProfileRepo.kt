// https://spring.io/projects/spring-data-jpa

package com.ride.driver.backend.repositories


// import java.util.List;

import com.ride.driver.backend.models.DriverDetails
import com.ride.driver.backend.models.DriverStatus
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository



open interface CourierProfileRepository : CrudRepository<DriverDetails, Long> {
    open fun findDriverById(id: String): DriverDetails?
    open fun findDriversByStatus(status: String): List<DriverDetails>
}


open interface CourierProfileRepositoryCustom: Repository<DriverDetails, Long> {
    open fun findDriversByStatus(status: DriverStatus): List<DriverDetails>    
}