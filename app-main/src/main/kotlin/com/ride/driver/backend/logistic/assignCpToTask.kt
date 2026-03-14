package com.ride.driver.backend.logistic.services

import java.util.UUID
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import com.ride.driver.backend.logistic.models.Task
import com.ride.driver.backend.logistic.models.TaskStatus
import com.ride.driver.backend.logistic.repositories.TaskRepository
import com.ride.driver.backend.courier.models.CourierStatus
import com.ride.driver.backend.courier.models.CourierProfile
import com.ride.driver.backend.courier.repositories.CourierProfileRepository

@Service
public class ScheduledTasks (
    private val courierProfileRepository: CourierProfileRepository,
    private val taskRepository: TaskRepository
){
	@Scheduled(fixedRate = 3000, initialDelay = 1000) // Run every 3 seconds with an initial delay of 5 seconds
    public fun assignCpToTask() {
        val availableTasks: List<Task> = taskRepository.findByTaskStatus(TaskStatus.READY_FOR_ASSIGNMENT) ?: return
        val onlineCouriers: List<CourierProfile> = courierProfileRepository.findByCpStatus(CourierStatus.ONLINE) ?: return
        var shuffledCourierProfiles: List<CourierProfile> = onlineCouriers.shuffled()

        for (task in availableTasks) {
            val assignedCourierProfile: CourierProfile = shuffledCourierProfiles.firstOrNull() ?: return // No more couriers available
            val updatedTask: Task = taskRepository.save(
                  task.copy(
                    courierProfile = assignedCourierProfile,
                )
            )
            shuffledCourierProfiles = shuffledCourierProfiles.drop(1) // Remove the assigned courier from the list
        }
    }
}