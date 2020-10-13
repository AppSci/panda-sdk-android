package com.appsci.panda.sdk.domain.subscriptions

data class Purchase(
        val id: String,
        val type: SkuType,
        val orderId: String,
        val token: String
)
