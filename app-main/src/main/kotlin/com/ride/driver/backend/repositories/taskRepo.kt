// https://spring.io/projects/spring-data-jpa
package com.ride.driver.backend.repositories

import org.springframework.data.repository.CrudRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import com.ride.driver.backend.models.logistics.Task
import com.ride.driver.backend.models.logistics.TaskStatus
import com.ride.driver.backend.models.consumerProfile.ConsumerProfile
import java.util.UUID

@Repository
interface TaskRepository: CrudRepository<Task, Long> {
   fun save(courierProfile: Task): Task
   fun findByConsumerProfile_Id(consumerProfileId: UUID): List<Task>
   fun findByCourierProfile_Id(courierProfileId: UUID): List<Task>
   fun findByVenueProfile_Id(venueProfileId: UUID): List<Task>
   fun findByTaskStatus(taskStatus: TaskStatus): List<Task>
   // fun findByAssignedCourierIdAndTaskStatus(assignedCourierId: UUID, taskStatus: TaskStatus): List<Task>
   // fun findByConsumerProfileIdAndTaskStatus(consumerProfileId: UUID, taskStatus: TaskStatus): List<Task>
   fun findByConsumerProfile_IdAndTaskStatus(consumerProfileId: UUID, taskStatus: TaskStatus): List<Task>
   fun findByCourierProfile_IdAndTaskStatus(courierProfileId: UUID, taskStatus: TaskStatus): List<Task>
   fun findAll(pageable: Pageable): Page<Task>
}