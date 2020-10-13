package com.appsci.panda.sdk.data.subscriptions.rest

import com.google.gson.annotations.SerializedName

data class ScreenResponse(
        @SerializedName("screen_html")
        val screenHtml: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("id")
        val id: String
)
