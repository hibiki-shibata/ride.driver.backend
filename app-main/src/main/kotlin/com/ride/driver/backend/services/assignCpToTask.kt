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
	@Scheduled(fixedRate = 4000) // Run every 60 seconds
    public fun assignCpToTask() {
        val availableTasks: List<Task> = taskRepository.findByTaskStatus(TaskStatus.READY_FOR_ASSIGNMENT)
        if (availableTasks.isEmpty()) println("No available tasks to assign")// return
        
        val onlineCouriers: List<CourierProfile> = courierProfileRepository.findByCpStatus(CourierStatus.ONLINE)
        if (onlineCouriers.isEmpty()) return
        

        var shuffledCouriers: List<CourierProfile> = onlineCouriers.shuffled()
        println("Assigning ${availableTasks.size} tasks to ${shuffledCouriers.size} online couriers")
        for (task in availableTasks) {
            val courierToAssign: CourierProfile = shuffledCouriers.firstOrNull() ?: return // No more couriers available            
            taskRepository.save(
                task.copy(
                    assignedCourierId = courierToAssign.id,
                )
            )
            shuffledCouriers = shuffledCouriers.drop(1) // Remove the assigned courier from the list
            println("Assigned task ${task.id} to courier ${courierToAssign.id}")
        }
    }
}