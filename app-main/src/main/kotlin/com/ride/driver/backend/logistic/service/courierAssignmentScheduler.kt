package com.ride.driver.backend.logistic.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
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
class CourierAssignmentScheduler(
    private val courierProfileRepository: CourierProfileRepository,
    private val taskRepository: TaskRepository
){
    private val logger: Logger = LoggerFactory.getLogger(CourierAssignmentScheduler::class.java)

	@Scheduled(fixedRate = 15000, initialDelay = 1000) // every 15 seconds
    @Transactional
    fun assignOnlineCpToPendingTasks() {
        var onlineCouriers: MutableList<CourierProfile> = courierProfileRepository
            .findByCpStatus(CourierStatus.ONLINE, PageRequest.of(0, 500))
            .shuffled()
            .toMutableList()
        val pendingTasks: Page<Task> = taskRepository.findByTaskStatusIn(
            listOf(TaskStatus.ASSIGNED_TO_COURIER, TaskStatus.READY_FOR_ASSIGNMENT),
            PageRequest.of(0, 500)
        )
        for (task in pendingTasks) {
            val courier: CourierProfile? = onlineCouriers.removeFirstOrNull()
            if (courier == null) {
                logger.info("event=no_available_courier_for_task taskId={}", task.id)
                updateTask(task, TaskStatus.READY_FOR_ASSIGNMENT, null)
                continue
            }
            updateTask(task, TaskStatus.ASSIGNED_TO_COURIER, courier)
        }            
        logger.info("event=scheduled_task_assignment_completed availableTasksCount={}", pendingTasks.size)
    }

    private fun updateTask(
        task: Task,
        taskStatus: TaskStatus,
        assignedCourierProfile: CourierProfile?
    ): Task {
        task.taskStatus = taskStatus
        task.courierProfile = assignedCourierProfile
        return taskRepository.save(task)
    }
}