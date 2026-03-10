// https://spring.io/projects/spring-data-jpa
package com.ride.driver.backend.repositories

import org.springframework.data.repository.CrudRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import com.ride.driver.backend.models.logistics.Task
import java.util.UUID

@Repository
open interface TaskRepository : CrudRepository<Task, Long> {
   fun save(courierProfile: Task): Task
   fun findByAssignedCourierId(assignedCourierId: UUID): Task?
   fun findAll(pageable: Pageable): Page<Task>
}