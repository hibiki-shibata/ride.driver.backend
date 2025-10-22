package com.ride.driver.backend.models

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Index
import jakarta.validation.constraints.NotBlank

@Entity
@Table(
    name = "area",
    indexes = [
        Index(name = "idx_area_name", columnList = "name")
    ]
    )

data class Area (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null,
    
    @NotBlank
    val name: String,
)