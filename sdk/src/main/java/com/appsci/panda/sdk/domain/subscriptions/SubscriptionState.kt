package com.appsci.panda.sdk.domain.subscriptions

sealed class SubscriptionState {

    companion object {
        fun map(serverName: String) = when (serverName) {
            "empty" -> Empty
            "ok" -> Success
            "canceled" -> Canceled
            "disabled_auto_renew" -> Canceled
            "failed_renew" -> Billing
            "refund" -> Refund
            else -> Empty
        }
    }

    object Empty : SubscriptionState() {
        override fun toString(): String = "Empty"
    }

    object Success : SubscriptionState() {
        override fun toString(): String = "Success"
    }

    object Canceled : SubscriptionState() {
        override fun toString(): String = "Canceled"
    }

    object Billing : SubscriptionState() {
        override fun toString(): String = "Billing"
    }

    object Refund : SubscriptionState() {
        override fun toString(): String = "Refund"
    }

}
