package com.appsci.panda.sdk.domain.subscriptions

import java.util.*

sealed class SkuItem(
        open val id: String,
        open val price: Double,
        open val fbPrice: Double,//price used for logging to FB
        val currency: Currency = Currency.getInstance(DEFAULT_CURRENCY)
) {

    companion object {
        private const val DEFAULT_CURRENCY = "USD"

        fun getAll(): List<SkuItem> {
            return listOf(
            )
        }

        fun byId(id: String): SkuItem? {
            return getAll().firstOrNull { it.id == id }
        }
    }

    sealed class Subscription(
            override val id: String,
            override val price: Double,
            override val fbPrice: Double,
            val trialDays: Int
    ) : SkuItem(id, price, fbPrice) {

    }

    sealed class Product(
            override val id: String,
            override val price: Double,
            override val fbPrice: Double
    ) : SkuItem(id, price, fbPrice) {

    }
}
