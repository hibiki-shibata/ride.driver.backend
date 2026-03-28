package com.ride.driver.backend.merchant.mapper


import com.ride.driver.backend.merchant.dto.MerchantProfileResDTO
import com.ride.driver.backend.merchant.model.MerchantProfile
import com.ride.driver.backend.shared.exception.AccountInvalidValuesException

fun MerchantProfile.toMerchantProfileResDto(): MerchantProfileResDTO {
    return MerchantProfileResDTO(
        id = this.id?.toString() ?: throw AccountInvalidValuesException("Profile mapping failed, Merchant ID is null"),
        name = this.name,
        phoneNumber = this.phoneNumber,
        merchantAddress = this.merchantAddress,
        merchantComments = this.merchantComments,
        merchantStatus = this.merchantStatus.toString(),
        merchantAddressCoordinate = this.merchantAddressCoordinate        
    )
}