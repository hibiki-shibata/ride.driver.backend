package com.ride.driver.backend.services

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import com.ride.driver.backend.repositories.CourierProfileRepository
import com.ride.driver.backend.repositories.TaskRepository
import com.ride.driver.backend.models.courierProfile.CourierProfile
import com.ride.driver.backend.models.logistics.Task
import com.ride.driver.backend.models.logistics.TaskStatus
import com.ride.driver.backend.models.courierProfile.CourierStatus
import java.util.UUID

@Service
public class ScheduledTasks (
    private val courierProfileRepository: CourierProfileRepository,
    private val taskRepository: TaskRepository
){
	@Scheduled(fixedRate = 3000, initialDelay = 1000) // Run every 3 seconds with an initial delay of 5 seconds
    public fun assignCpToTask() {
        val availableTasks: List<Task> = taskRepository.findByTaskStatus(TaskStatus.READY_FOR_ASSIGNMENT)
        if (availableTasks.isEmpty()) return
        
        val onlineCouriers: List<CourierProfile> = courierProfileRepository.findByCpStatus(CourierStatus.ONLINE)
        if (onlineCouriers.isEmpty()) return
        
        var shuffledCourierProfiles: List<CourierProfile> = onlineCouriers.shuffled()

        for (task in availableTasks) {
            val assignedCourierProfile: CourierProfile? = shuffledCourierProfiles.firstOrNull() ?: return // No more couriers available
            val updatedTask: Task = taskRepository.save(
                  task.copy(
                    courierProfile = assignedCourierProfile,
                )
            )
            shuffledCourierProfiles = shuffledCourierProfiles.drop(1) // Remove the assigned courier from the list
        }
    }
}