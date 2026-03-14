// https://spring.io/projects/spring-data-jpa
package com.ride.driver.backend.merchant.repositories

import org.springframework.data.repository.CrudRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import com.ride.driver.backend.merchant.models.MerchantItem
import java.util.UUID

@Repository
open interface MerchantItemRepository : CrudRepository<MerchantItem, Long> {
   fun save(merchantItem: MerchantItem): MerchantItem
   fun findById(id: UUID): MerchantItem?
   fun findByMerchantProfile_Id(merchantProfileId: UUID): List<MerchantItem>
   fun findByName(name: String): List<MerchantItem>
   fun findAll(pageable: Pageable): Page<MerchantItem>
}