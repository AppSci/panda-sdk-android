package com.appsci.panda.sdk.domain.subscriptions

import com.appsci.panda.sdk.data.subscriptions.rest.SubscriptionResponse
import com.appsci.panda.sdk.data.subscriptions.rest.SubscriptionStateResponse

data class SubscriptionState(
        val status: SubscriptionStatus,
        val subscriptions: Subscriptions
) {
    companion object {
        private fun mapStatus(status: String): SubscriptionStatus {
            return when (status) {
                "empty" -> SubscriptionStatus.Empty
                "ok" -> SubscriptionStatus.Success
                "canceled" -> SubscriptionStatus.Canceled
                "disabled_auto_renew" -> SubscriptionStatus.Canceled
                "failed_renew" -> SubscriptionStatus.Billing
                "refund" -> SubscriptionStatus.Refund
                else -> SubscriptionStatus.Empty
            }
        }

        fun map(response: SubscriptionStateResponse): SubscriptionState {
            val status = mapStatus(response.state)
            val mapSubscription: (SubscriptionResponse) -> Subscription = {
                Subscription(
                        isTrial = it.isTrial,
                        productId = it.productId,
                        subscriptionId = it.subscriptionId,
                        status = mapStatus(it.state)
                )
            }
            return SubscriptionState(
                    status = status,
                    subscriptions = Subscriptions(
                            android = response.subscriptions.android.orEmpty().map(mapSubscription),
                            ios = response.subscriptions.ios.orEmpty().map(mapSubscription),
                            web = response.subscriptions.web.orEmpty().map(mapSubscription)
                    )
            )
        }
    }
}

sealed class SubscriptionStatus {

    object Empty : SubscriptionStatus() {
        override fun toString(): String = "Empty"
    }

    object Success : SubscriptionStatus() {
        override fun toString(): String = "Success"
    }

    object Canceled : SubscriptionStatus() {
        override fun toString(): String = "Canceled"
    }

    object Billing : SubscriptionStatus() {
        override fun toString(): String = "Billing"
    }

    object Refund : SubscriptionStatus() {
        override fun toString(): String = "Refund"
    }

}

data class Subscriptions(
        val android: List<Subscription>,
        val ios: List<Subscription>,
        val web: List<Subscription>
)

data class Subscription(
        val subscriptionId: String,
        val isTrial: Boolean,
        val productId: String,
        val status: SubscriptionStatus
)
