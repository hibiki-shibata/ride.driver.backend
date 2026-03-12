package com.ride.driver.backend.models.logistics

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.validation.constraints.NotBlank
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.JoinColumn
import jakarta.persistence.Index
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.AttributeOverrides
import jakarta.persistence.AttributeOverride
import com.ride.driver.backend.models.Coordinate
import java.util.UUID

@Entity
@Table(
    name = "Task",
    indexes = [
        Index(name = "idx_assigned_courier_id", columnList = "assigned_courier_id"),
        Index(name = "idx_consumer_id", columnList = "consumer_id"),
        Index(name = "idx_task_status", columnList = "task_status")
    ]
)

data class Task(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    val id: Long? = null,

    @Column(name = "assigned_courier_id", nullable = false)
    val assignedCourierId: UUID? = null,

    @Column(name = "consumer_id", nullable = false)
    val consumerId: UUID,

    @Column(name = "venue_id", nullable = false)
    val venueId: UUID,

    @Column(name = "order_time", nullable = false)
    val orderTime: Long = System.currentTimeMillis(),

    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "latitude", column = Column(name = "pickup_latitude")),
        AttributeOverride(name = "longitude", column = Column(name = "pickup_longitude"))
    )
    val pickupLocation: Coordinate,

    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "latitude", column = Column(name = "dropoff_latitude")),
        AttributeOverride(name = "longitude", column = Column(name = "dropoff_longitude"))
    )
    val dropoffLocation: Coordinate,

    @Column(name = "task_note", nullable = true)
    val taskNote: String? = null,

    @Column(name = "consumer_name", nullable = false)
    val consumerName: String,

    @Column(name = "venue_name", nullable = false)
    val venueName: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "task_status", nullable = false)
    val taskStatus: TaskStatus 
)