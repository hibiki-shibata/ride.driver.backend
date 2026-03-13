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
import com.ride.driver.backend.models.consumerProfile.ConsumerProfile
import com.ride.driver.backend.models.courierProfile.CourierProfile
import com.ride.driver.backend.models.venueProfile.VenueProfile

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

    // @Column(name = "assigned_courier_id", nullable = false)
    // val assignedCourierId: UUID? = null,

    @Column(name = "order_time", nullable = false)
    val orderTime: Long = System.currentTimeMillis(),

    @Column(name = "task_note", nullable = true)
    val taskNote: String? = null,

    @ManyToOne
    // @JoinColumn(name = "courier_profile", referencedColumnName = "id", insertable = false, updatable = false)
    @JoinColumn(name = "courier_profile", nullable = true, referencedColumnName = "id")
    val courierProfile: CourierProfile? = null,
    
    @ManyToOne
    // @JoinColumn(name = "consumer_profile", referencedColumnName = "id", insertable = false, updatable = false)
    @JoinColumn(name = "consumer_profile", nullable = false, referencedColumnName = "id")
    val consumerProfile: ConsumerProfile,

    @ManyToOne
    // @JoinColumn(name = "venue_profile", referencedColumnName = "id", insertable = false, updatable = false)
    @JoinColumn(name = "venue_profile", nullable = false, referencedColumnName = "id")
    val venueProfile: VenueProfile,

    @Enumerated(EnumType.STRING)
    @Column(name = "task_status", nullable = false)
    val taskStatus: TaskStatus 
)