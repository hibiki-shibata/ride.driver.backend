// https://spring.io/guides/gs/accessing-data-jpa
package com.ride.driver.backend.models

import java.util.UUID

import com.ride.driver.backend.models.*
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;




@Entity
data class DriverDetails(
    
    @Id
    @GeneratedValue(generator = "UUID", strategy = jakarta.persistence.GenerationType.AUTO)
    val id: UUID,
    val phoneNumber: String,
    val name: String,
    val vehicleType: VehicleType,
    val location: Location,
    val assignID: String,
    val rate: Double,
    val status: DriverStatus = DriverStatus.AVAILABLE,
    val area: String,
    val driverComments: String = "",
) {
    override fun toString(): String {
        return "DriverDetails(id=$id, phoneNumber='$phoneNumber', name='$name', vehicleType=$vehicleType, location=$location, assignID='$assignID', rate=$rate, status=$status, area='$area', driverComments='$driverComments')"
    }

    open fun getById(): String {
        return id.toString()
    }
        


    open fun getByStatus(): String {
        return status.toString()
    }


}







