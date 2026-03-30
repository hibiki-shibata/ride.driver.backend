package com.ride.driver.backend.logistic.service

import java.util.UUID
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.ride.driver.backend.logistic.model.Task
import com.ride.driver.backend.logistic.model.TaskStatus
import com.ride.driver.backend.logistic.repository.TaskRepository
import com.ride.driver.backend.courier.model.CourierStatus
import com.ride.driver.backend.courier.model.CourierProfile
import com.ride.driver.backend.courier.repository.CourierProfileRepository

@Service
public class ScheduledTasks (
    private val courierProfileRepository: CourierProfileRepository,
    private val taskRepository: TaskRepository
){
    private val logger: Logger = LoggerFactory.getLogger(ScheduledTasks::class.java)

	@Scheduled(fixedRate = 3000, initialDelay = 1000) // Run every 3 seconds with an initial delay of 5 seconds
    @Transactional
    public fun assignCpToTask() {
        val cpRequiredTasks: List<Task> = taskRepository.findByTaskStatus(
            listOf(TaskStatus.ASSIGNED_TO_COURIER, TaskStatus.READY_FOR_ASSIGNMENT)
        )
        var shuffledOnlineCouriers: List<CourierProfile> = courierProfileRepository.findByCpStatus(CourierStatus.ONLINE).shuffled()

        for (task in cpRequiredTasks) {
            val randomOnlineCourier: CourierProfile? = shuffledOnlineCouriers.firstOrNull()
            if (randomOnlineCourier == null) {
                logger.info("event=no_available_courier_for_task taskId={}", task.id)
                updateTaskStatusAndCourier(task, TaskStatus.READY_FOR_ASSIGNMENT, null)
                continue
            }
            updateTaskStatusAndCourier(task, TaskStatus.ASSIGNED_TO_COURIER, randomOnlineCourier)
            shuffledOnlineCouriers = shuffledOnlineCouriers.drop(1) // Remove the assigned courier from the list
        }            
        logger.info("event=scheduled_task_assignment_completedavailableTasksCount={}", cpRequiredTasks.size)
    }

    private fun updateTaskStatusAndCourier(task: Task,  taskStatus: TaskStatus, courierProfile: CourierProfile?): Task {
        return taskRepository.save(
            task.apply {
                this.courierProfile = courierProfile
                this.taskStatus = taskStatus
            }
        )
    }
}