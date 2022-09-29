package com.appsci.panda.sdk.data.subscriptions

import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.QueryProductDetailsParams
import com.appsci.panda.sdk.data.device.DeviceDao
import com.appsci.panda.sdk.data.subscriptions.google.BillingValidator
import com.appsci.panda.sdk.data.subscriptions.google.PurchasesGoogleStore
import com.appsci.panda.sdk.data.subscriptions.local.FileStore
import com.appsci.panda.sdk.data.subscriptions.local.PurchasesLocalStore
import com.appsci.panda.sdk.data.subscriptions.rest.PurchasesRestStore
import com.appsci.panda.sdk.data.subscriptions.rest.ScreenResponse
import com.appsci.panda.sdk.domain.subscriptions.*
import com.appsci.panda.sdk.domain.utils.rx.DefaultCompletableObserver
import com.gen.rxbilling.client.RxBilling
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import kotlinx.coroutines.*
import kotlinx.coroutines.rx2.await
import timber.log.Timber

class SubscriptionsRepositoryImpl(
        private val localStore: PurchasesLocalStore,
        private val googleStore: PurchasesGoogleStore,
        private val restStore: PurchasesRestStore,
        private val mapper: PurchasesMapper,
        private val intentValidator: BillingValidator,
        private val deviceDao: DeviceDao,
        private val fileStore: FileStore,
        private val billing: RxBilling,
) : SubscriptionsRepository {

    private val loadedScreens = mutableMapOf<ScreenKey, ScreenResponse>()

    override fun sync(): Completable {
        return fetchHistory()
                .andThen(saveGooglePurchases())
                .doOnComplete {
                    acknowledge()
                }
                .andThen(deviceDao.requireUserId())
                .flatMapCompletable { userId ->
                    localStore.getNotSentPurchases()
                            .flatMapPublisher { Flowable.fromIterable(it) }
                            .concatMapCompletable { entity ->
                                val purchase = mapper.mapToDomain(entity)
                                return@concatMapCompletable restStore.sendPurchase(purchase, userId)
                                        .doOnSuccess {
                                            localStore.markSynced(entity.productId)
                                        }.ignoreElement()
                            }
                }
    }

    override fun validatePurchase(purchase: Purchase): Single<Boolean> {

        return saveGooglePurchases()
                .andThen(deviceDao.requireUserId())
                .flatMap {
                    restStore.sendPurchase(purchase, it)
                            .doOnSuccess {
                                localStore.markSynced(purchase.id)
                            }
                }.doAfterSuccess {
                    acknowledge()
                }
    }

    override fun restore(): Single<List<String>> =
            fetchHistory()
                    .andThen(saveGooglePurchases())
                    .andThen(deviceDao.requireUserId())
                    .flatMap { userId ->
                        googleStore.getPurchases()
                                .flatMapPublisher { Flowable.fromIterable(it) }
                                .flatMapMaybe { entity ->
                                    val purchase = mapper.mapToDomain(entity)
                                    return@flatMapMaybe restStore.sendPurchase(purchase, userId)
                                            .doOnSuccess {
                                                localStore.markSynced(entity.productId)
                                            }.filter { it }
                                            .map { entity.productId }
                                }.toList()
                    }

    override fun consumeProducts(): Completable =
            googleStore.consumeProducts()
                    .andThen(googleStore.fetchHistory())

    override fun prefetchSubscriptionScreen(type: ScreenType?, id: String?): Single<SubscriptionScreen> {
        return loadSubscriptionScreen(type, id)
                .map {
                    SubscriptionScreen(
                            id = it.id,
                            name = it.name,
                            screenHtml = it.screenHtml
                    )
                }
    }

    override fun getSubscriptionScreen(type: ScreenType?, id: String?): Single<SubscriptionScreen> {
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

    override fun getCachedScreen(type: ScreenType?, id: String?): SubscriptionScreen? {
        val key = ScreenKey(id = id, type = type)
        return loadedScreens[key]?.let {
            SubscriptionScreen(
                    id = it.id,
                    name = it.name,
                    screenHtml = it.screenHtml
            )
        }
    }

    override fun getCachedOrDefaultScreen(id: String): Single<SubscriptionScreen> {
        val cachedScreen = loadedScreens.values.firstOrNull {
            it.id == id
        }?.let {
            SubscriptionScreen(
                    id = it.id,
                    name = it.name,
                    screenHtml = it.screenHtml
            )
        }
        val cachedMaybe = cachedScreen?.let {
            Maybe.just(it)
        } ?: Maybe.empty()
        return cachedMaybe
                .switchIfEmpty(getFallbackScreen().toMaybe())
                .toSingle()
    }

    override fun getFallbackScreen(): Single<SubscriptionScreen> =
            fileStore.getSubscriptionScreen()

    override suspend fun getProductsDetails(requests: Map<String, List<String>>): List<ProductDetails> =
            withContext(Dispatchers.IO) {
                val scope = CoroutineScope(SupervisorJob())

                val params: List<QueryProductDetailsParams> = requests
                        .map { group ->
                            val type = group.key
                            val ids = group.value
                            QueryProductDetailsParams.newBuilder()
                                    .setProductList(
                                            ids.map {
                                                QueryProductDetailsParams.Product.newBuilder()
                                                        .setProductId(it)
                                                        .setProductType(type)
                                                        .build()
                                            }
                                    ).build()
                        }

                return@withContext params.map {
                    scope.async {
                        billing.getProductDetails(it).await()
                    }
                }.awaitAll().flatten()
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

    private fun acknowledge() {
        googleStore.acknowledge()
                .subscribe(DefaultCompletableObserver())
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

    private fun loadSubscriptionScreen(type: ScreenType?, id: String?): Single<ScreenResponse> {
        Timber.d("loadSubscriptionScreen $type\n$id")
        val key = ScreenKey(id = id, type = type)
        return deviceDao.requireUserId()
                .flatMap {
                    restStore.getSubscriptionScreen(
                            userId = it,
                            type = type?.requestName,
                            id = id,
                    )
                }.doOnSuccess {
                    loadedScreens[key] = it
                }
    }
}

val ScreenType.requestName: String
    get() = when (this) {
        ScreenType.Sales -> "sales"
    }

data class ScreenKey(
        val id: String?,
        val type: ScreenType?,
)
