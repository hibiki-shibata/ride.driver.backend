package com.ride.driver.backend.models

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Index
import jakarta.validation.constraints.NotBlank
import jakarta.persistence.Column

@Entity
@Table(
    name = "operation_area",
    indexes = [
        Index(name = "idx_area_name", columnList = "name")
    ]
)

data class OperationArea (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    val id: Long? = null,

    @Column(name = "name", unique = true, nullable = false)
    val name: String,
)