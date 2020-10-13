package com.appsci.panda.sdk.data.subscriptions

import com.appsci.panda.sdk.data.subscriptions.google.BillingValidator
import com.appsci.panda.sdk.data.subscriptions.google.PurchasesGoogleStore
import com.appsci.panda.sdk.data.subscriptions.local.PurchasesLocalStore
import com.appsci.panda.sdk.data.subscriptions.rest.PurchasesRestStore
import com.appsci.panda.sdk.data.subscriptions.rest.ScreenResponse
import com.appsci.panda.sdk.domain.subscriptions.SubscriptionState
import com.appsci.panda.sdk.domain.subscriptions.SubscriptionsRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import timber.log.Timber

class SubscriptionsRepositoryImpl(
        private val localStore: PurchasesLocalStore,
        private val googleStore: PurchasesGoogleStore,
        private val restStore: PurchasesRestStore,
        private val mapper: PurchasesMapper,
        private val intentValidator: BillingValidator
) : SubscriptionsRepository {

    private val loadedScreens = mutableMapOf<ScreenKey, ScreenResponse>()

    override fun sync(): Completable {
        return saveGooglePurchases()
                .andThen(localStore.getNotSentPurchases())
                .flatMapPublisher { Flowable.fromIterable(it) }
                .concatMapCompletable { entity ->
                    val purchase = mapper.mapToDomain(entity)
                    return@concatMapCompletable restStore.sendPurchase(purchase)
                            .doOnComplete {
                                localStore.markSynced(entity.productId)
                            }
                }
    }

    override fun consumeProducts(): Completable =
            googleStore.consumeProducts()
                    .andThen(googleStore.fetchHistory())

    override fun prefetchSubscriptionScreen(userId: String, type: String?, id: String?): Completable {
        val key = ScreenKey(id = id, type = type)
        return restStore.getSubscriptionScreen(
                userId = userId,
                type = type,
                id = id)
                .doOnSuccess {
                    loadedScreens[key] = it
                }
                .ignoreElement()
    }

    override fun getSubscriptionState(userId: String): Single<SubscriptionState> =
            restStore.getSubscriptionState(userId)

    override fun fetchHistory(): Completable {
        return googleStore.fetchHistory()
                .doOnComplete {
                    Timber.d("fetchHistory success")
                }.doOnError {
                    Timber.e(it)
                }
    }

    private fun saveGooglePurchases(): Completable {

        //active subscriptions from billing client
        return googleStore.getPurchases()
                .flatMap { purchases ->
                    intentValidator.validateIntent()
                            .toSingle { purchases }
                            .onErrorReturnItem(emptyList())
                }
                .doOnSuccess { purchases ->
                    localStore.savePurchases(purchases)
                }.ignoreElement()
    }
}

data class ScreenKey(
        val id: String?,
        val type: String?
)
