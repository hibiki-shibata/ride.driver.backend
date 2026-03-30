// https://spring.io/projects/spring-data-jpa
package com.ride.driver.backend.logistic.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import com.ride.driver.backend.logistic.model.Task
import com.ride.driver.backend.logistic.model.TaskStatus
import com.ride.driver.backend.consumer.model.ConsumerProfile
import java.util.UUID

@Repository
interface TaskRepository: JpaRepository<Task, UUID> {
   fun findByConsumerProfile_Id(consumerProfileId: UUID): List<Task>
   fun findByCourierProfile_Id(courierProfileId: UUID): List<Task>
   fun findByMerchantProfile_Id(merchantProfileId: UUID): List<Task>
   fun findByIdAndConsumerProfile_IdAndTaskStatus(taskId: UUID, consumerProfileId: UUID, taskStatus: TaskStatus): Task?
   fun findByIdAndCourierProfile_IdAndTaskStatus(taskId: UUID, courierProfileId: UUID, taskStatus: TaskStatus): Task?
   fun findByIdAndMerchantProfile_IdAndTaskStatus(taskId: UUID, merchantProfileId: UUID, taskStatus: TaskStatus): Task?
   fun findByCourierProfile_IdAndTaskStatus(courierProfileId: UUID, taskStatus: TaskStatus): List<Task>
   // fun findByConsumerProfile_IdAndTaskStatus(consumerProfileId: UUID, taskStatus: TaskStatus): List<Task>
   // fun findByMerchantProfile_IdAndTaskStatus(merchantProfileId: UUID, taskStatus: TaskStatus): List<Task>
   fun findByTaskStatus(taskStatus: List<TaskStatus>): List<Task>
   override fun findAll(pageable: Pageable): Page<Task>
}