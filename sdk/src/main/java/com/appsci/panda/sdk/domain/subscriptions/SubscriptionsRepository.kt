package com.appsci.panda.sdk.domain.subscriptions

import io.reactivex.Completable
import io.reactivex.Single

interface SubscriptionsRepository {

    /**
     * returns [SubscriptionStatus] based on purchases from billing and local store
     */
    fun getSubscriptionState(): Single<SubscriptionState>

    /**
     * Fetches purchases from billing and sends to rest store
     */
    fun sync(): Completable

    fun restore(): Single<List<String>>

    fun validatePurchase(purchase: Purchase): Single<Boolean>

    /**
     * Consumes all available products and refreshes all purchases
     */
    fun consumeProducts(): Completable

    fun fetchHistory(): Completable

    fun prefetchSubscriptionScreen(type: ScreenType?, id: String?): Single<SubscriptionScreen>

    fun getSubscriptionScreen(type: ScreenType?, id: String?): Single<SubscriptionScreen>

    fun getCachedOrDefaultScreen(id: String): Single<SubscriptionScreen>

    fun getFallbackScreen(): Single<SubscriptionScreen>
}
