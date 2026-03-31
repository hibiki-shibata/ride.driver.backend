package com.ride.driver.backend.merchant.mapper

import com.ride.driver.backend.merchant.dto.MerchantItemReqDTO
import com.ride.driver.backend.merchant.model.MerchantItem
import com.ride.driver.backend.merchant.model.MerchantProfile
import com.ride.driver.backend.shared.exception.ItemNotFoundException
import java.util.UUID

fun MerchantItemReqDTO.toMerchantItem(merchantProfile: MerchantProfile): MerchantItem {
    return MerchantItem(
        name = this.name,
        description = this.description,
        price = this.price,
        merchantProfile = merchantProfile
    )
}