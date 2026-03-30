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
        val availableTasks: List<Task> = taskRepository.findByTaskStatus(TaskStatus.READY_FOR_ASSIGNMENT)
        val onlineCouriers: List<CourierProfile> = courierProfileRepository.findByCpStatus(CourierStatus.ONLINE)
        var shuffledCourierProfiles: List<CourierProfile?> = onlineCouriers.shuffled()

        for (task in availableTasks) {
            val assignedCourierProfile: CourierProfile = shuffledCourierProfiles.firstOrNull() ?: return // No more couriers available
            task.courierProfile = assignedCourierProfile
            taskRepository.save(task)
            shuffledCourierProfiles = shuffledCourierProfiles.drop(1) // Remove the assigned courier from the list
        }
        logger.info("event=scheduled_task_assignment_completed assignedTasksCount={} availableTasksCount={} onlineCouriersCount={}", availableTasks.size, availableTasks.size, onlineCouriers.size)
    }
}