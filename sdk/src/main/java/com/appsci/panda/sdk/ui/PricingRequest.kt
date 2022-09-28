package com.appsci.panda.sdk.ui

import com.google.gson.annotations.SerializedName

data class ProductPricingRequest(
        @SerializedName("id")
        val id: String,
        @SerializedName("type")
        val type: String,
)

interface ProductDetails

data class SubscriptionDetails(
        @SerializedName("productId")
        val productId: String,
        @SerializedName("type")
        val type: String,
        @SerializedName("pricingPhases")
        val pricingPhases: List<PricingPhase>,
) : ProductDetails

data class InappDetails(
        @SerializedName("productId")
        val productId: String,
        @SerializedName("type")
        val type: String,
        @SerializedName("oneTimePurchaseOfferDetail")
        val oneTimePurchaseOfferDetail: OneTimePurchaseOfferDetails,
) : ProductDetails

data class PricingPhase(
        @SerializedName("priceAmountMicros")
        val priceAmountMicros: Long,
        @SerializedName("priceCurrencyCode")
        val priceCurrencyCode: String,
        @SerializedName("formattedPrice")
        val formattedPrice: String,
        @SerializedName("billingPeriod")
        val billingPeriod: String,
        @SerializedName("recurrenceMode")
        val recurrenceMode: Int,
        @SerializedName("billingCycleCount")
        val billingCycleCount: Int?,
)

data class OneTimePurchaseOfferDetails(
        @SerializedName("priceAmountMicros")
        val priceAmountMicros: Long,
        @SerializedName("priceCurrencyCode")
        val priceCurrencyCode: String,
        @SerializedName("formattedPrice")
        val formattedPrice: String,
)
