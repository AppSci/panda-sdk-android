package com.appsci.panda.sdk

sealed class PandaEvent {

    data class SubscriptionSelect(
            val screenId: String,
            val screenName: String,
            val productId: String
    ) : PandaEvent()

    data class SuccessfulPurchase(
            val screenId: String,
            val screenName: String,
            val productId: String
    ) : PandaEvent()

    data class ScreenShowed(
            val screenId: String,
            val screenName: String,
    ) : PandaEvent()

    data class DismissClick(
            val screenId: String,
            val screenName: String,
    ) : PandaEvent()

    object TermsClick : PandaEvent()
    object PolicyClick : PandaEvent()

    data class OpenExternal(
            val screenId: String,
            val url: String,
    ) : PandaEvent()

    data class Redirect(
        val screenId: String,
        val url: String,
    ) : PandaEvent()
}

typealias PandaAnalyticsListener = ((PandaEvent) -> Unit)
