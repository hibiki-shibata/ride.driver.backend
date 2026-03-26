package com.ride.driver.backend.merchant.mapper


import com.ride.driver.backend.merchant.dto.MerchantProfileResDTO
import com.ride.driver.backend.merchant.model.MerchantProfile

fun MerchantProfile.toMerchantProfileResDto(): MerchantProfileResDTO {
    return MerchantProfileResDTO(
        id = this.id?.toString() ?: "Unknow merchant id",
        name = this.name,
        phoneNumber = this.phoneNumber,
        merchantAddress = this.merchantAddress,
        merchantComments = this.merchantComments,
        merchantStatus = this.merchantStatus.toString(),
        merchantAddressCoordinate = this.merchantAddressCoordinate        
    )
}