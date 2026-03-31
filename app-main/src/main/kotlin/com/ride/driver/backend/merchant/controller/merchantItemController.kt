package com.ride.driver.backend.merchant.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim
import com.ride.driver.backend.merchant.dto.MerchantItemReqDTO
import com.ride.driver.backend.merchant.dto.MerchantItemResDTO
import com.ride.driver.backend.merchant.service.MerchantItemService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@RestController
@RequestMapping("/api/v1/merchants/items")
class MerchantItemController (
    private val merchantItemService: MerchantItemService
){
    private val logger: Logger = LoggerFactory.getLogger(MerchantItemController::class.java)

    @GetMapping("/me")
    fun getMerchantItems(
        @AuthenticationPrincipal merchantDetails: AccessTokenClaim
    ): ResponseEntity<List<MerchantItemResDTO>> {
        logger.info("event=merchant_items_request_received merchantId={}", merchantDetails.accountId)
        val merchantItems: List<MerchantItemResDTO> = merchantItemService.getMerchantItems(merchantDetails)
        return ResponseEntity.ok(merchantItems)
    }

     @PutMapping("/me")
     fun updateMerchantItems(
         @RequestBody @Valid req: List<MerchantItemReqDTO>,
         @AuthenticationPrincipal merchantDetails: AccessTokenClaim
     ): ResponseEntity<List<MerchantItemResDTO>> {
         logger.info("event=merchant_items_update_request_received merchantId={}", merchantDetails.accountId)
         val updatedItems: List<MerchantItemResDTO> = merchantItemService.updateMerchantItems(
             merchantDetails = merchantDetails,
             req = req
         )
         return ResponseEntity.ok(updatedItems)
     }
 } 