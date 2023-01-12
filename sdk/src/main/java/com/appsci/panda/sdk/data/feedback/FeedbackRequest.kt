package com.appsci.panda.sdk.data.feedback

import com.google.gson.annotations.SerializedName

data class FeedbackRequest(
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("screen_id")
    val screenId: String,
    @SerializedName("answer")
    val answer: String,
)
