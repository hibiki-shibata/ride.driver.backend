package com.ride.driver.backend.consumer.mapper

import com.ride.driver.backend.consumer.dto.MerchantProductResDTO
import com.ride.driver.backend.merchant.model.MerchantItem
import com.ride.driver.backend.shared.exception.AccountInvalidValuesException

fun MerchantItem.toMerchantProductResDTO(): MerchantProductResDTO {
    return MerchantProductResDTO(
        id = this.id?.toString() ?: throw AccountInvalidValuesException("Merchant Item ID is null"),
        name = this.name,
        productDescription = this.description,
        price = this.price,
        enabled = this.enabled
    )
}