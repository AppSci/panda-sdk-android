package com.appsci.panda.sdk.data.subscriptions.rest

import com.google.gson.annotations.SerializedName

data class ProductRequest(
        @SerializedName("product_id")
        val productId: String,
        @SerializedName("order_id")
        val orderId: String,
        @SerializedName("purchase_token")
        val purchaseToken: String
)
