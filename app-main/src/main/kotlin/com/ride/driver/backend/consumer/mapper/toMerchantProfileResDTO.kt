package com.ride.driver.backend.consumer.mapper

import com.ride.driver.backend.consumer.dto.MerchantProfileResDTO
import com.ride.driver.backend.merchant.model.MerchantProfile
import com.ride.driver.backend.shared.exception.AccountInvalidValuesException

fun MerchantProfile.toMerchantProfileResDTO(): MerchantProfileResDTO {
    return MerchantProfileResDTO(
        id = this.id?.toString() ?: throw AccountInvalidValuesException("Merchant ID is null"),
        name = this.name,
        phoneNumber = this.phoneNumber,
        merchantComments = this.merchantComments,
        merchantAddress = this.merchantAddress,
        merchantStatus = this.merchantStatus.name,
        merchantAddressCoordinate = this.merchantAddressCoordinate
    )
}