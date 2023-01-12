package com.appsci.panda.sdk.data.feedback

import com.appsci.panda.sdk.data.device.DeviceDao
import com.appsci.panda.sdk.data.network.RestApi
import com.appsci.panda.sdk.domain.feedback.FeedbackRepository
import kotlinx.coroutines.rx2.await
import javax.inject.Inject

class FeedbackRepositoryImpl @Inject constructor(
    private val restApi: RestApi,
    private val deviceDao: DeviceDao,
) : FeedbackRepository {

    override suspend fun sendFeedback(
        screenId: String,
        answer: String,
    ) {
        val userId = deviceDao.requireUserId().await()
        restApi.sendFeedback(
            FeedbackRequest(
                userId = userId,
                screenId = screenId,
                answer = answer,
            )
        )
    }
}
