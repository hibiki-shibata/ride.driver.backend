package com.ride.driver.backend.logistic.mapper

import com.ride.driver.backend.merchant.model.MerchantItem
import com.ride.driver.backend.logistic.model.OrderedItem
import com.ride.driver.backend.shared.exception.AccountNotFoundException
import com.ride.driver.backend.shared.exception.ItemNotFoundException

fun MerchantItem.toOrderedItem(): OrderedItem {
    return OrderedItem(
        itemId = this.id ?: throw ItemNotFoundException("Merchant item ID is null for item with name: ${this.name}"),
        name = this.name,
        description = this.description,
        price = this.price,
        merchantId = this.merchantProfile?.id ?: throw AccountNotFoundException("Mapped merchant profile ID is null for item ID: ${this.id}")
    )
}