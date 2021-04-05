package com.appsci.panda.sdk.data.subscriptions.rest

import com.appsci.panda.sdk.data.network.RestApi
import com.appsci.panda.sdk.domain.subscriptions.Purchase
import com.appsci.panda.sdk.domain.subscriptions.SkuType
import com.appsci.panda.sdk.domain.subscriptions.SubscriptionState
import io.reactivex.Single

interface PurchasesRestStore {

    fun sendPurchase(purchase: Purchase, userId: String): Single<Boolean>

    fun getSubscriptionState(userId: String): Single<SubscriptionState>

    fun getSubscriptionScreen(userId: String, type: String?, id: String?): Single<ScreenResponse>
}

class PurchasesRestStoreImpl(
        private val restApi: RestApi
) : PurchasesRestStore {

    override fun sendPurchase(purchase: Purchase, userId: String): Single<Boolean> {
        return when (purchase.type) {
            SkuType.SUBSCRIPTION ->
                restApi.sendSubscription(
                        SubscriptionRequest(
                                productId = purchase.id,
                                orderId = purchase.orderId,
                                purchaseToken = purchase.token),
                        userId = userId
                )
            SkuType.INAPP ->
                restApi.sendProduct(
                        ProductRequest(
                                productId = purchase.id,
                                orderId = purchase.orderId,
                                purchaseToken = purchase.token,
                        ),
                        userId = userId)
        }.map { it.active }
    }

    override fun getSubscriptionState(userId: String): Single<SubscriptionState> =
            restApi.getSubscriptionStatus(userId)
                    .map { SubscriptionState.map(it) }

    override fun getSubscriptionScreen(userId: String, type: String?, id: String?): Single<ScreenResponse> =
            restApi.getSubscriptionScreen(
                    userId = userId,
                    type = type,
                    id = id
            )

}
