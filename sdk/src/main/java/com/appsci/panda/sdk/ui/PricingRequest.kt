package com.appsci.panda.sdk.ui

import com.android.billingclient.api.BillingClient
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
        val billingCycleCount: Int,
)

data class OneTimePurchaseOfferDetails(
        @SerializedName("priceAmountMicros")
        val priceAmountMicros: Long,
        @SerializedName("priceCurrencyCode")
        val priceCurrencyCode: String,
        @SerializedName("formattedPrice")
        val formattedPrice: String,
)

fun List<com.android.billingclient.api.ProductDetails>.toModels(): List<ProductDetails> {
    return this.mapNotNull { productDetails ->
        when (productDetails.productType) {
            BillingClient.ProductType.INAPP -> {
                val oneTimePurchaseOfferDetails = productDetails.oneTimePurchaseOfferDetails
                        ?: return@mapNotNull null
                InappDetails(
                        productId = productDetails.productId,
                        type = productDetails.productType,
                        oneTimePurchaseOfferDetail = OneTimePurchaseOfferDetails(
                                priceAmountMicros = oneTimePurchaseOfferDetails.priceAmountMicros,
                                priceCurrencyCode = oneTimePurchaseOfferDetails.priceCurrencyCode,
                                formattedPrice = oneTimePurchaseOfferDetails.formattedPrice,
                        )
                )
            }
            BillingClient.ProductType.SUBS -> {
                val subscriptionOfferDetails = productDetails.subscriptionOfferDetails?.first()
                        ?: return@mapNotNull null
                SubscriptionDetails(
                        productId = productDetails.productId,
                        type = productDetails.productType,
                        pricingPhases = subscriptionOfferDetails.pricingPhases.pricingPhaseList.map {
                            PricingPhase(
                                    priceAmountMicros = it.priceAmountMicros,
                                    priceCurrencyCode = it.priceCurrencyCode,
                                    formattedPrice = it.formattedPrice,
                                    recurrenceMode = it.recurrenceMode,
                                    billingPeriod = it.billingPeriod,
                                    billingCycleCount = it.billingCycleCount,
                            )
                        }
                )
            }
            else -> null
        }
    }
}
