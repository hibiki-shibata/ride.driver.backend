package com.ride.driver.backend.merchant.mapper

import com.ride.driver.backend.merchant.dto.MerchantItemResDTO
import com.ride.driver.backend.merchant.model.MerchantItem
import com.ride.driver.backend.shared.exception.ItemNotFoundException

fun MerchantItem.toMerchantItemResDTO(): MerchantItemResDTO {
    return MerchantItemResDTO(
        id = this.id?.toString() ?: throw ItemNotFoundException("Merchant item ID is null for item with name: ${this.name}"),
        name = this.name,
        description = this.description,
        price = this.price,
        enabled = this.enabled
    )
}