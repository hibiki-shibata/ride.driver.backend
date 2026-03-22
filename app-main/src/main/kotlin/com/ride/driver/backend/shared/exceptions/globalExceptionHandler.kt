package com.ride.driver.backend.shared.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.context.annotation.Configuration
import org.slf4j.Logger
import org.slf4j.LoggerFactory

data class ApiErrorResponseDTO(
    val error: String,
    val message: String?
)

@Configuration
@ControllerAdvice
class GlobalExceptionHandler {
    private val logger: Logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(HttpBaseException::class)
    fun handleHttpBaseException(e: HttpBaseException): ResponseEntity<ApiErrorResponseDTO> {
        logger.warn("HttpBaseException occured", e)
        return ResponseEntity
            .status(e.status)
            .body(ApiErrorResponseDTO(
                error = e.status.reasonPhrase, 
                message = e.message
            )
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralException(e: Exception): ResponseEntity<ApiErrorResponseDTO> {
        logger.error("An unexpected error occurred", e)
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiErrorResponseDTO(
                error = "Unexpected internal Server Error",
                message = e.message            
            )
        )
    }
}


