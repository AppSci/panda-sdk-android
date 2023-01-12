package com.appsci.panda.sdk.domain.feedback

interface FeedbackRepository {

    suspend fun sendFeedback(screenId: String, answer: String)
}
