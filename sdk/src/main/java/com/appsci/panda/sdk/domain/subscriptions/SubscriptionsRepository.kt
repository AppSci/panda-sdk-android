package com.appsci.panda.sdk.domain.subscriptions

import io.reactivex.Completable
import io.reactivex.Single

interface SubscriptionsRepository {

    /**
     * returns [SubscriptionState] based on purchases from billing and local store
     */
    fun getSubscriptionState(): Single<SubscriptionState>

    /**
     * Fetches purchases from billing and sends to rest store
     */
    fun sync(): Completable

    /**
     * Consumes all available products and refreshes all purchases
     */
    fun consumeProducts(): Completable

    fun fetchHistory(): Completable

    fun prefetchSubscriptionScreen(type: String?, id: String?): Completable

    fun getSubscriptionScreen(type: String?, id: String?): Single<SubscriptionScreen>
}
