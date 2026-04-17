package com.ride.driver.backend.consumer.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
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
@RequestMapping("api/v1/explore")
class ProductExploreController ( 
    private val productExploreService: ProductExploreService
){
    private val logger = LoggerFactory.getLogger(ProductExploreController::class.java)

// pagination
    @GetMapping("/merchants")
    fun getAvailableMerchants(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int        
    ): ResponseEntity<List<MerchantProfileResDTO>> {
        val merchantProducts: List<MerchantProfileResDTO> = productExploreService.getPublishedMerchants(page, size)
        return ResponseEntity.ok(merchantProducts)
    }

    @GetMapping("/merchant/{merchantId}")
    fun getMerchantProfile(
        @PathVariable merchantId: String
    ): ResponseEntity<MerchantProfileResDTO> { 
        val merchantProfile: MerchantProfileResDTO = productExploreService.getMerchantProfile(merchantId)
        return ResponseEntity.ok(merchantProfile)
    }

    @GetMapping("/merchant/{merchantId}/products")
    fun getProductsByMerchant(
        @PathVariable merchantId: String
    ): ResponseEntity<List<MerchantProductResDTO>> { 
        val merchantProducts: List<MerchantProductResDTO> = productExploreService.getProductsByMerchantId(merchantId)
        return ResponseEntity.ok(merchantProducts)
    }   
}
        