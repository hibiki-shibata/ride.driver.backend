// package com.ride.driver.backend.merchant.service

// @Service
// class MerchanItemService (
//     val merchantItemRepository: MerchantItemRepository,
//     val passwordService: PasswordService,
// ) {
//     private val logger: Logger = LoggerFactory.getLogger(MerchantProfileService::class.java)

//     fun getMerchantProfile(merchantDetails: AccessTokenClaim): MerchantProfileResDTO {
//         val savedMerchant: MerchantProfile = getMerchantProfileById(merchantDetails.accountId)
//         logger.info("event=merchant_profile_fetched merchantId={}", savedMerchant.id)
//         return savedMerchant.toMerchantProfileResDto()
//     }

//     @Transactional
//     fun updateMerchantProfile(
//         merchantDetails: AccessTokenClaim,
//         req: MerchantProfileReqDTO
//     ): MerchantProfileResDTO {
//         val savedMerchant: MerchantProfile = getMerchantProfileById(merchantDetails.accountId)
//         savedMerchant.apply {
//             name = req.name;
//             phoneNumber = req.phoneNumber;
//             merchantAddress = req.merchantAddress;
//             merchantComments = req.merchantComments;
//             merchantAddressCoordinate = req.merchantAddressCoordinate
//         }
//         val updatedProfile: MerchantProfile = merchantProfileRepository.save(savedMerchant)
//         logger.info("event=merchant_profile_updated merchantId={}", savedMerchant.id)
//         return updatedProfile.toMerchantProfileResDto()
//     }

//     @Transactional
//     fun updateMerchantOpenStatus(
//         merchantDetails: AccessTokenClaim,
//         req: MerchantProfileReqDTO
//     ): MerchantProfileResDTO {
//         val savedMerchant: MerchantProfile = getMerchantProfileById(merchantDetails.accountId)
//         if (savedMerchant.merchantStatus == MerchantStatus.ADMINS_ONLY) throw AccountInvalidValuesException("Cannot change open status for an admin-only merchant")
//         savedMerchant.apply{
//             merchantStatus = if (req.isOpen) MerchantStatus.OPEN else MerchantStatus.CLOSED
//         }
//         val updatedProfile: MerchantProfile = merchantProfileRepository.save(savedMerchant)
//         logger.info("event=merchant_online_status_updated merchantId={}", savedMerchant.id)
//         return updatedProfile.toMerchantProfileResDto()
//     }

//     fun getMerchantOrderHistory(
//         merchantDetails: AccessTokenClaim
//     ): List<MerchantOrderHistoryDTO?> {
//         val taskHistory: Page<Task> = taskRepository.findByMerchantProfile_Id(merchantDetails.accountId, PageRequest.of(0, 100))
//         logger.info("event=merchant_orderHistory_fetched merchantId={}", merchantDetails.accountId)
//         return taskHistory.content.map { it.toMerchantOrderHistoryDto() }
//     }

//     private fun getMerchantProfileById(merchantId: UUID): MerchantProfile {
//             val savedMerchant = merchantProfileRepository.findById(merchantId).orElseThrow { 
//                 AccountNotFoundException("Merchant not found with ID: $merchantId") 
//             }
//             return savedMerchant
//     }              
// }