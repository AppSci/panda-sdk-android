package com.appsci.panda.sdk.data.subscriptions.rest

import com.google.gson.annotations.SerializedName

data class SubscriptionStateResponse(
        @SerializedName("state")
        val state: String
)
