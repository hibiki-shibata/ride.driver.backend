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
	@Scheduled(fixedRate = 3000) // Run every 17 seconds
    public fun assignCpToTask() {
        val availableTasks: List<Task> = taskRepository.findByTaskStatus(TaskStatus.READY_FOR_ASSIGNMENT)
        if (availableTasks.isEmpty()) return
        
        val onlineCouriers: List<CourierProfile> = courierProfileRepository.findByCpStatus(CourierStatus.ONLINE)
        if (onlineCouriers.isEmpty()) return
        
        var shuffledCouriers: List<CourierProfile> = onlineCouriers.shuffled()

        for (task in availableTasks) {
            val courierToAssign: CourierProfile = shuffledCouriers.firstOrNull() ?: return // No more couriers available            
            taskRepository.save(
                task.copy(
                    assignedCourierId = courierToAssign.id ?: throw Exception("Failed to assign courier to task: Courier ID is null"),
                )
            )
            shuffledCouriers = shuffledCouriers.drop(1) // Remove the assigned courier from the list
        }
    }
}