package com.appsci.panda.sdk.data.subscriptions.google

import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.ConsumeParams
import com.appsci.panda.sdk.data.subscriptions.PurchasesMapper
import com.appsci.panda.sdk.data.subscriptions.local.PurchaseEntity
import com.appsci.panda.sdk.data.subscriptions.local.TYPE_PRODUCT
import com.appsci.panda.sdk.data.subscriptions.local.TYPE_SUBSCRIPTION
import com.appsci.panda.sdk.domain.utils.rx.Schedulers
import com.gen.rxbilling.client.RxBilling
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import timber.log.Timber

interface PurchasesGoogleStore {

    fun getPurchases(): Single<List<PurchaseEntity>>

    fun consumeProducts(): Completable

    fun fetchHistory(): Completable

    fun acknowledge(): Completable

}

class PurchasesGoogleStoreImpl(
        private val rxBilling: RxBilling,
        private val mapper: PurchasesMapper
) : PurchasesGoogleStore {

    override fun getPurchases(): Single<List<PurchaseEntity>> {
        return rxBilling.getPurchases(BillingClient.SkuType.SUBS)
                .map { mapper.mapFromBillingPurchases(it, TYPE_SUBSCRIPTION) }
                .flatMap { purchases ->
                    //we don't use zip() here because it crashes in case of errors in both sources
                    return@flatMap rxBilling.getPurchases(BillingClient.SkuType.INAPP)
                            .map { mapper.mapFromBillingPurchases(it, TYPE_PRODUCT) }
                            .map { subscriptions -> return@map subscriptions + purchases }
                }
                .doOnSuccess { Timber.d("getPurchases $it") }
                .doOnError { Timber.e(it) }
                //by default billing client pushes result to UI thread, so we need to switch it to IO
                .observeOn(Schedulers.io())
    }

    override fun consumeProducts(): Completable {
        return rxBilling.getPurchases(BillingClient.SkuType.INAPP)
                .flatMapPublisher { Flowable.fromIterable(it) }
                .concatMapCompletable {
                    rxBilling.consumeProduct(ConsumeParams.newBuilder()
                            .setPurchaseToken(it.purchaseToken)
                            .build())
                }
                .observeOn(Schedulers.io())
    }

    override fun fetchHistory(): Completable {
        return rxBilling.getPurchaseHistory(BillingClient.SkuType.SUBS)
                .ignoreElement()
                .andThen(rxBilling.getPurchaseHistory(BillingClient.SkuType.INAPP))
                .ignoreElement()
                .observeOn(Schedulers.io())
    }

    override fun acknowledge(): Completable =
            rxBilling.getPurchases(BillingClient.SkuType.SUBS)
                    .flatMap { subscriptions ->
                        rxBilling.getPurchases(BillingClient.SkuType.INAPP)
                                .map { products ->
                                    subscriptions + products
                                }
                    }.map { it.filter { !it.isAcknowledged } }
                    .flatMapPublisher { Flowable.fromIterable(it) }
                    .flatMapCompletable {
                        rxBilling.acknowledge(AcknowledgePurchaseParams
                                .newBuilder()
                                .setPurchaseToken(it.purchaseToken)
                                .build())
                    }
                    .observeOn(Schedulers.io())

}
