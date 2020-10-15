package com.appsci.panda.sdk.data.subscriptions

import com.appsci.panda.sdk.data.device.DeviceDao
import com.appsci.panda.sdk.data.subscriptions.google.BillingValidator
import com.appsci.panda.sdk.data.subscriptions.google.PurchasesGoogleStore
import com.appsci.panda.sdk.data.subscriptions.local.PurchasesLocalStore
import com.appsci.panda.sdk.data.subscriptions.rest.PurchasesRestStore
import com.appsci.panda.sdk.data.subscriptions.rest.ScreenResponse
import com.appsci.panda.sdk.domain.subscriptions.SubscriptionScreen
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
        private val intentValidator: BillingValidator,
        private val deviceDao: DeviceDao
) : SubscriptionsRepository {

    private val loadedScreens = mutableMapOf<ScreenKey, ScreenResponse>()

    override fun sync(): Completable {
        return saveGooglePurchases()
                .andThen(deviceDao.requireUserId())
                .flatMapCompletable { userId ->
                    localStore.getNotSentPurchases()
                            .flatMapPublisher { Flowable.fromIterable(it) }
                            .concatMapCompletable { entity ->
                                val purchase = mapper.mapToDomain(entity)
                                return@concatMapCompletable restStore.sendPurchase(purchase, userId)
                                        .doOnComplete {
                                            localStore.markSynced(entity.productId)
                                        }
                            }

                }
    }

    override fun consumeProducts(): Completable =
            googleStore.consumeProducts()
                    .andThen(googleStore.fetchHistory())

    override fun prefetchSubscriptionScreen(type: String?, id: String?): Completable {
        return loadSubscriptionScreen(type, id)
                .ignoreElement()
    }

    override fun getSubscriptionScreen(type: String?, id: String?): Single<SubscriptionScreen> {
        val key = ScreenKey(id = id, type = type)
        val cachedScreen = loadedScreens[key]
        return (if (cachedScreen != null) {
            Single.just(cachedScreen)
        } else {
            loadSubscriptionScreen(type, id)
        }).map {
            SubscriptionScreen(
                    id = it.id,
                    name = it.name,
                    screenHtml = it.screenHtml
            )
        }

    }

    override fun getSubscriptionState(): Single<SubscriptionState> =
            deviceDao.requireUserId()
                    .flatMap { restStore.getSubscriptionState(it) }

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

    private fun loadSubscriptionScreen(type: String?, id: String?): Single<ScreenResponse> {
        val key = ScreenKey(id = id, type = type)
        return deviceDao.requireUserId()
                .flatMap {
                    restStore.getSubscriptionScreen(
                            userId = it,
                            type = type,
                            id = id)
                }.doOnSuccess {
                    loadedScreens[key] = it
                }
    }
}

data class ScreenKey(
        val id: String?,
        val type: String?
)
