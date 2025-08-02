// https://spring.io/projects/spring-data-jpa

package com.ride.driver.backend.repositories


// import java.util.List;

import org.springframework.data.repository.CrudRepository;
import com.ride.driver.backend.models.DriverDetails

// open interface CustomerRepository extends CrudRepository<Customer, Long> {

//   List<Customer> findByLastName(String lastName);

//   Customer findById(long id);
// }


open interface CourierProfileRepository : CrudRepository<DriverDetails, Long> {
    open fun findById(id: String): DriverDetails?

    open fun findByStatus(status: String): List<DriverDetails>
}
