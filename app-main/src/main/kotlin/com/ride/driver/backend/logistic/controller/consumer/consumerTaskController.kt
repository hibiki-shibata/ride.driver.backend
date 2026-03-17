package com.ride.driver.backend.logistic.controller.consumer

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import com.ride.driver.backend.logistic.dto.TaskStatusActionDTO
import com.ride.driver.backend.logistic.service.LogisticsService
import com.ride.driver.backend.logistic.model.Task
import com.ride.driver.backend.shared.auth.domain.AccessTokenData
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import java.util.UUID

data class CreateTaskDTO(
    @field:NotBlank
    val merchantID: UUID,

    @field:NotEmpty
    val orderedItemIDs: List<String>
)

@RestController
@RequestMapping("/api/v1/logistics/consumer")
class ConsumerTaskController (
    private val logisticsService: LogisticsService
){
    @PostMapping("/task/create")
    fun cxCreateTask(
        @RequestBody createTaskDTO: CreateTaskDTO,
        @AuthenticationPrincipal consumerDetails: AccessTokenData        
    ): ResponseEntity<Task> {
        val createdTask: Task = logisticsService.createTask(
            consumerId = consumerDetails.accountID,
            merchantId = createTaskDTO.merchantID,
            orderedItemIDs = createTaskDTO.orderedItemIDs
        )
        return ResponseEntity.ok(createdTask)
    }
}