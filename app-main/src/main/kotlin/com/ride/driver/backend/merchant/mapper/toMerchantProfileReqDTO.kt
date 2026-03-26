package com.ride.driver.backend.merchant.mapper


import com.ride.driver.backend.merchant.dto.MerchantProfileReqDTO
import com.ride.driver.backend.merchant.model.MerchantProfile

fun MerchantProfile.toMerchantProfileReqDto(): MerchantProfileReqDTO {
    return MerchantProfileReqDTO(
        name = this.name,
        phoneNumber = this.phoneNumber,
        merchantAddress = this.merchantAddress,
        merchantComments = this.merchantComments,
        merchantStatus = this.merchantStatus.toString(),
        merchantAddressCoordinate = this.merchantAddressCoordinate        
    )
}