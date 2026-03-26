// https://spring.io/projects/spring-data-jpa
package com.ride.driver.backend.merchant.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import com.ride.driver.backend.merchant.model.MerchantProfile
import java.util.UUID

@Repository
open interface MerchantProfileRepository : JpaRepository<MerchantProfile, UUID> {
   fun existsByPhoneNumber(phoneNumber: String): Boolean
   fun findByPhoneNumber(phoneNumber: String): MerchantProfile?
   fun findByMerchantStatus(merchantStatus: String): List<MerchantProfile>
   fun findByName(merchantName: String): List<MerchantProfile>
}