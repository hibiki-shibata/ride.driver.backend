package com.ride.driver.backend.consumer.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import jakarta.validation.Valid
import com.ride.driver.backend.consumer.service.ProductExploreService
import com.ride.driver.backend.consumer.dto.MerchantProductResDTO
import com.ride.driver.backend.consumer.dto.MerchantProfileResDTO
import com.ride.driver.backend.shared.auth.domain.AccessTokenClaim

@RestController
@RequestMapping("api/v1/consumer/product")
class ProductExploreController ( 
    private val productExploreService: ProductExploreService
){
    private val logger = LoggerFactory.getLogger(ProductExploreController::class.java)

// pagination
    @GetMapping("/merchant/{page}/{size}")
    fun getAvailableMerchants(
        @AuthenticationPrincipal consumerDetails: AccessTokenClaim,
        @PathVariable page: Int,
        @PathVariable size: Int
    ): ResponseEntity<List<MerchantProfileResDTO>> {
        val merchantProducts: List<MerchantProfileResDTO> = productExploreService.getAvailableMerchants(page, size)
        return ResponseEntity.ok(merchantProducts)
    }

    @GetMapping("/merchant/{merchantId}")
    fun getMerchantProfile(
        @AuthenticationPrincipal consumerDetails: AccessTokenClaim,
        @PathVariable merchantId: String
    ): ResponseEntity<MerchantProfileResDTO> { 
        val merchantProfile: MerchantProfileResDTO = productExploreService.getMerchantProfile(merchantId)
        return ResponseEntity.ok(merchantProfile)
    }

    @GetMapping("/merchant/{merchantId}/products")
    fun getProductsByMerchant(
        @AuthenticationPrincipal consumerDetails: AccessTokenClaim,
        @PathVariable merchantId: String
    ): ResponseEntity<List<MerchantProductResDTO>> { 
        val merchantProducts: List<MerchantProductResDTO> = productExploreService.getProductsByMerchant(merchantId)
        return ResponseEntity.ok(merchantProducts)
    }   
}
        