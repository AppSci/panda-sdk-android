package com.appsci.panda.sdk.data.subscriptions.rest

import com.google.gson.annotations.SerializedName

data class SubscriptionStateResponse(
        @SerializedName("state")
        val state: String,
        @SerializedName("subscriptions")
        val subscriptions: SubscriptionsResponse
)

data class SubscriptionsResponse(
        @SerializedName("android")
        val android: List<SubscriptionResponse>?,
        @SerializedName("ios")
        val ios: List<SubscriptionResponse>?,
        @SerializedName("web")
        val web: List<SubscriptionResponse>?
)

data class SubscriptionResponse(
        @SerializedName("subscription_id")
        val subscriptionId: String,
        @SerializedName("is_trial_period")
        val isTrial: Boolean,
        @SerializedName("product_id")
        val productId: String,
        @SerializedName("state")
        val state: String,
        @SerializedName("is_intro_offer")
        val isIntroOffer: Boolean?,
)
