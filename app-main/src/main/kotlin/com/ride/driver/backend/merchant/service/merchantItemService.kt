// package com.ride.driver.backend.merchant.service

// @Service
// class MerchanItemService (
//     val merchantItemRepository: MerchantItemRepository,
// ) {
//     private val logger: Logger = LoggerFactory.getLogger(MerchanItemService::class.java)

//     fun getMerchantItems(merchantId: UUID): List<MerchantItemResDTO> {
//         val merchantItems: List<MerchantItem> = merchantItemRepository.findByMerchantId(merchantId)
//         logger.info("event=merchant_items_fetched merchantId={} itemCount={}", merchantId, merchantItems.size)
//         return merchantItems.map { it.toMerchantItemResDto() }
//     }

//     fun createMerchantItems(
//         merchantId: UUID,
//         req: List<MerchantItemReqDTO>
//     ): List<MerchantItemResDTO> {
//         val merchantItemsToCreate: List<MerchantItem> = req.map { it.toMerchantItem(merchantId) }
//         val createdMerchantItems: List<MerchantItem> = merchantItemRepository.saveAll(merchantItemsToCreate)
//         logger.info("event=merchant_items_created merchantId={} itemCount={}", merchantId, createdMerchantItems.size)
//         return createdMerchantItems.map { it.toMerchantItemResDto() }
//     }

//     fun deleteMerchantItems(
//         merchantId: UUID,
//         itemIdsToDelete: List<UUID>
//     ) {
//         val merchantItemsToDelete: List<MerchantItem> = merchantItemRepository.findByIdInAndMerchantProfile_Id(itemIdsToDelete, merchantId)
//         if (merchantItemsToDelete.size != itemIdsToDelete.size) throw ItemNotFoundException("One or more items not found for the given merchant with ID: $merchantId")
//         merchantItemRepository.deleteAll(merchantItemsToDelete)
//         logger.info("event=merchant_items_deleted merchantId={} itemCount={}", merchantId, merchantItemsToDelete.size)
//     }
// }