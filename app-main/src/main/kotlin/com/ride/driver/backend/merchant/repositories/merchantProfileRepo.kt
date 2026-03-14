// https://spring.io/projects/spring-data-jpa
package com.ride.driver.backend.merchant.repositories

import org.springframework.data.repository.CrudRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import com.ride.driver.backend.merchant.models.MerchantProfile
import java.util.UUID

@Repository
open interface MerchantProfileRepository : CrudRepository<MerchantProfile, Long> {
   fun save(merchantProfile: MerchantProfile): MerchantProfile
   fun existsByPhoneNumber(phoneNumber: String): Boolean
   fun findByPhoneNumber(phoneNumber: String): MerchantProfile?
   fun findByMerchantStatus(merchantStatus: String): List<MerchantProfile>
   fun findByName(merchantName: String): List<MerchantProfile>
   fun findById(id: UUID): MerchantProfile?
}