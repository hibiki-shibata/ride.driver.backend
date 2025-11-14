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

    @ExceptionHandler(CustomExceptionDemo::class)
    fun handleCustomException(e: CustomExceptionDemo): ResponseEntity<Map<String, String?>> {
        val body = mapOf("error" to "Custom exception occurred", "message" to e.message)
        return ResponseEntity(body, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(e: AuthenticationException): ResponseEntity<Map<String, String?>> {
        val body = mapOf("error" to "Authentication error:", "message" to e.message)
        return ResponseEntity(body, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralException(e: Exception): ResponseEntity<Map<String, String?>> {
        val body = mapOf("error" to "Unexpected error happened", "message" to e.message)
        return ResponseEntity(body, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}