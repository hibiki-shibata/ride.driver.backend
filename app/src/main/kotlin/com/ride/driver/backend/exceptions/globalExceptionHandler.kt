package com.ride.driver.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.http.ResponseEntity;

// Custom exception handlers
import com.ride.driver.backend.exceptions.HibikiSpecialException;
import com.ride.driver.backend.exceptions.httpException;


@ControllerAdvice
class GlobalDefaultExceptionHandler {
  public val DEFAULT_ERROR_VIEW: String = "error";

  @ExceptionHandler(IllegalArgumentException::class)
  fun defaultErrorHandler(e: IllegalArgumentException): ResponseEntity<Map<String, String?>> {
    
    
    // if (e is IllegalArgumentException) {
    //   // Handle specific exception
    //   val body = mapOf("error" to "Invalid argument", "message" to e.message)
    //   return ResponseEntity(body, HttpStatus.BAD_REQUEST);
    // }

    // if (e is HibikiSpecialException) {
    //   // Handle custom exception
    //   val body = mapOf("error" to "Custom exception", "message" to e.message)
    //   return ResponseEntity(body, HttpStatus.BAD_REQUEST);
    // }
    
    
    // Otherwise setup and send the user to a default error-view.
    val body = mapOf("error" to "An unexpected error occurred", "message" to e.message)
    return ResponseEntity(body,  HttpStatus.INTERNAL_SERVER_ERROR);  
  }


    @ExceptionHandler(HibikiSpecialException::class)
    fun handleCustomException(e: HibikiSpecialException): ResponseEntity<Map<String, String?>> {
        val body = mapOf("error" to "CCCustom exception occurred", "message" to e.message)
        return ResponseEntity(body, HttpStatus.BAD_REQUEST)
    }
}