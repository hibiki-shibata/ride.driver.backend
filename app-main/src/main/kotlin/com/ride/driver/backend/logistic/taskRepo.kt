// https://spring.io/projects/spring-data-jpa
package com.ride.driver.backend.logistic.repositories

import org.springframework.data.repository.CrudRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import com.ride.driver.backend.logistic.models.Task
import com.ride.driver.backend.logistic.models.TaskStatus
import com.ride.driver.backend.consumer.models.ConsumerProfile
import java.util.UUID

@Repository
interface TaskRepository: CrudRepository<Task, Long> {
   fun save(courierProfile: Task): Task
   fun findById(id: UUID): Task?
   fun findByConsumerProfile_Id(consumerProfileId: UUID): List<Task>
   fun findByCourierProfile_Id(courierProfileId: UUID): List<Task>
   fun findByMerchantProfile_Id(merchantProfileId: UUID): List<Task>
   fun findByConsumerProfile_IdAndTaskStatus(consumerProfileId: UUID, taskStatus: TaskStatus): List<Task>
   fun findByCourierProfile_IdAndTaskStatus(courierProfileId: UUID, taskStatus: TaskStatus): List<Task>
   fun findByTaskStatus(taskStatus: TaskStatus): List<Task>
   fun findAll(pageable: Pageable): Page<Task>
}