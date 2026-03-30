// https://spring.io/projects/spring-data-jpa
package com.ride.driver.backend.merchant.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import com.ride.driver.backend.merchant.model.MerchantItem
import java.util.UUID

@Repository
open interface MerchantItemRepository : JpaRepository<MerchantItem, UUID> {
   fun findByMerchantProfile_Id(merchantProfileId: UUID): List<MerchantItem>
   fun findByName(name: String): List<MerchantItem>
   override fun findAll(pageable: Pageable): Page<MerchantItem>
}