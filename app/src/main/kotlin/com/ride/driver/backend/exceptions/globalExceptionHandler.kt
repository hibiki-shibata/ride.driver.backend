package com.ride.driver.backend.exceptions

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.context.annotation.Configuration
import com.ride.driver.backend.exceptions.CustomExceptionDemo
import com.ride.driver.backend.exceptions.HttpException

@ControllerAdvice
class GlobalDefaultExceptionHandler {
  public val DEFAULT_ERROR_VIEW: String = "error";

  @ExceptionHandler(IllegalArgumentException::class)
  fun defaultErrorHandler(e: IllegalArgumentException): ResponseEntity<Map<String, String?>> { 
    val body = mapOf("error" to "An unexpected error occurred", "message" to e.message)
    return ResponseEntity(body,  HttpStatus.INTERNAL_SERVER_ERROR);  
  }
    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(e: AuthenticationException): ResponseEntity<Map<String, String?>> {
        val body = mapOf("error" to "Authentication error:", "message" to e.message)
        return ResponseEntity(body, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequestException(e: BadRequestException): ResponseEntity<Map<String, String?>> {
        val body = mapOf("error" to "Bad request error", "message" to e.message)
        return ResponseEntity(body, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralException(e: Exception): ResponseEntity<Map<String, String?>> {
        val body = mapOf("error" to "Internal server error", "message" to e.message)
        return ResponseEntity(body, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}