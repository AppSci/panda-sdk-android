package com.appsci.panda.sdk

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Intent
import androidx.fragment.app.Fragment
import com.android.billingclient.api.BillingClient
import com.appsci.panda.sdk.domain.subscriptions.*
import com.appsci.panda.sdk.domain.utils.rx.DefaultCompletableObserver
import com.appsci.panda.sdk.domain.utils.rx.DefaultSchedulerProvider
import com.appsci.panda.sdk.domain.utils.rx.DefaultSingleObserver
import com.appsci.panda.sdk.domain.utils.rx.Schedulers
import com.appsci.panda.sdk.injection.components.DaggerPandaComponent
import com.appsci.panda.sdk.injection.components.PandaComponent
import com.appsci.panda.sdk.injection.modules.AppModule
import com.appsci.panda.sdk.injection.modules.BillingModule
import com.appsci.panda.sdk.injection.modules.NetworkModule
import com.appsci.panda.sdk.ui.ScreenExtra
import com.appsci.panda.sdk.ui.SubscriptionActivity
import com.appsci.panda.sdk.ui.SubscriptionFragment
import com.jakewharton.threetenabp.AndroidThreeTen
import io.reactivex.Completable
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject
import com.android.billingclient.api.Purchase as GooglePurchase

object Panda {

    private lateinit var panda: IPanda
    private lateinit var context: Application
    internal lateinit var pandaComponent: PandaComponent

    private val dismissListeners: MutableList<() -> Unit> = mutableListOf()
    private val errorListeners: MutableList<(t: Throwable) -> Unit> = mutableListOf()
    private val purchaseListeners: MutableList<(id: String) -> Unit> = mutableListOf()
    private val restoreListeners: MutableList<(ids: List<String>) -> Unit> = mutableListOf()

    private val pandaListeners: MutableList<PandaListener> = mutableListOf()
    private val analyticsListeners: MutableList<PandaAnalyticsListener> = mutableListOf()

    /**
     * Call this function on App start to configure Panda SDK
     */
    @kotlin.jvm.JvmStatic
    fun configure(
            context: Application,
            apiKey: String,
            debug: Boolean = BuildConfig.DEBUG,
            appsflyerId: String? = null,
            onSuccess: ((String) -> Unit)? = null,
            onError: ((Throwable) -> Unit)? = null
    ) = configureRx(
            context = context,
            apiKey = apiKey,
            debug = debug,
            appsflyerId = appsflyerId
    )
            .doOnSuccess { onSuccess?.invoke(it) }
            .doOnError { onError?.invoke(it) }
            .subscribe(DefaultSingleObserver())

    /**
     * Call this function on App start to configure Panda SDK
     */
    @kotlin.jvm.JvmStatic
    fun configureRx(
            context: Application,
            apiKey: String,
            debug: Boolean = BuildConfig.DEBUG,
            appsflyerId: String? = null,
    ): Single<String> {
        this.context = context
        Schedulers.setInstance(DefaultSchedulerProvider())
        AndroidThreeTen.init(context)
        val wrapper = PandaDependencies()
        pandaComponent = DaggerPandaComponent
                .builder()
                .appModule(AppModule(context.applicationContext))
                .billingModule(BillingModule(context))
                .networkModule(NetworkModule(
                        debug = debug,
                        apiKey = apiKey
                ))
                .build()
        pandaComponent.inject(wrapper)
        panda = wrapper.panda
        panda.start()

        appsflyerId?.let {
            panda.saveAppsflyerId(appsflyerId)
        }

        return panda.authorize()
                .subscribeOn(Schedulers.io())
                .doOnSuccess {
                    Timber.d("authorize success")
                }
                .observeOn(Schedulers.mainThread())
    }

    /**
     * @returns
     */
    val pandaUserId: String?
        get() = panda.pandaUserId

    /**
     * Set custom user id to current user
     * @param id - your custom userId,
     */
    @kotlin.jvm.JvmStatic
    fun setCustomUserId(id: String,
                        onComplete: (() -> Unit)? = null,
                        onError: ((Throwable) -> Unit)? = null
    ) = setCustomUserIdRx(id)
            .doOnComplete { onComplete?.invoke() }
            .doOnError { onError?.invoke(it) }
            .subscribe(DefaultCompletableObserver())

    /**
     * Set appsflyer id to current user
     * @param id - your appsflyer Id,
     */
    @kotlin.jvm.JvmStatic
    fun setAppsflyerId(id: String,
                       onComplete: (() -> Unit)? = null,
                       onError: ((Throwable) -> Unit)? = null
    ) = setCustomUserIdRx(id)
            .doOnComplete { onComplete?.invoke() }
            .doOnError { onError?.invoke(it) }
            .subscribe(DefaultCompletableObserver())

    /**
     * Set custom user id to current user
     * @param id - your custom userId,
     */
    @kotlin.jvm.JvmStatic
    fun setCustomUserIdRx(id: String): Completable =
            panda.setCustomUserId(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.mainThread())

    /**
     * Set appsflyer id to current user
     * @param id - your appsflyer Id,
     */
    @kotlin.jvm.JvmStatic
    fun setAppsflyerIdRx(id: String): Completable =
            panda.setAppsflyerId(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.mainThread())

    /**
     * Gets subscriptions from google and sends to Panda server
     */
    @kotlin.jvm.JvmStatic
    fun syncSubscriptions(
            onComplete: (() -> Unit)? = null,
            onError: ((Throwable) -> Unit)? = null
    ) = syncSubscriptionsRx()
            .doOnComplete { onComplete?.invoke() }
            .doOnError { onError?.invoke(it) }
            .subscribe(DefaultCompletableObserver())

    /**
     * Gets subscriptions from google and sends to Panda server
     */
    @kotlin.jvm.JvmStatic
    fun syncSubscriptionsRx(): Completable =
            panda.syncSubscriptions()
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.mainThread())

    /**
     * Get user's subscription state from Panda server
     */
    @kotlin.jvm.JvmStatic
    fun getSubscriptionState(
            onSuccess: ((SubscriptionState) -> Unit)? = null,
            onError: ((Throwable) -> Unit)? = null
    ): Unit = getSubscriptionStateRx()
            .doOnSuccess { onSuccess?.invoke(it) }
            .doOnError { onError?.invoke(it) }
            .subscribe(DefaultSingleObserver())

    /**
     * Get user's subscription state from Panda server
     */
    @kotlin.jvm.JvmStatic
    fun getSubscriptionStateRx(): Single<SubscriptionState> =
            panda.getSubscriptionState()
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.mainThread())

    /**
     * Consume all products owned by user
     */
    @kotlin.jvm.JvmStatic
    fun consumeProductsRx(): Completable =
            panda.consumeProducts()
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.mainThread())

    /**
     * Consume all products owned by user
     */
    @kotlin.jvm.JvmStatic
    fun consumeProducts(
            onComplete: (() -> Unit)? = null,
            onError: ((Throwable) -> Unit)? = null
    ) = panda.consumeProducts()
            .doOnComplete(onComplete)
            .doOnError(onError)
            .subscribe(DefaultCompletableObserver())

    /**
     * Get subscription screen and save it to memory cache
     */
    @kotlin.jvm.JvmStatic
    fun prefetchSubscriptionScreen(
            type: ScreenType = ScreenType.Sales,
            id: String? = null,
            onComplete: (() -> Unit)? = null,
            onError: ((Throwable) -> Unit)? = null
    ) = prefetchSubscriptionScreenRx(type, id)
            .doOnComplete { onComplete?.invoke() }
            .doOnError { onError?.invoke(it) }
            .subscribe(DefaultCompletableObserver())

    /**
     * Get subscription screen and save it to memory cache
     */
    @SuppressLint("SetJavaScriptEnabled")
    @kotlin.jvm.JvmStatic
    fun prefetchSubscriptionScreenRx(type: ScreenType? = null, id: String? = null): Completable =
            panda.prefetchSubscriptionScreen(type, id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.mainThread())
                    .ignoreElement()

    /**
     * Get Fragment with subscription UI that handles billing flow
     */
    @kotlin.jvm.JvmStatic
    fun getSubscriptionScreen(
            type: ScreenType? = null,
            id: String? = null,
            onSuccess: ((Fragment) -> Unit)? = null,
            onError: ((Throwable) -> Unit)? = null
    ) = getSubscriptionScreenRx(type, id)
            .doOnSuccess { onSuccess?.invoke(it) }
            .doOnError { onError?.invoke(it) }
            .subscribe(DefaultSingleObserver())

    /**
     * Get Fragment with subscription UI that handles billing flow
     */
    @kotlin.jvm.JvmStatic
    fun getSubscriptionScreenRx(type: ScreenType? = null, id: String? = null): Single<Fragment> =
            panda.getSubscriptionScreen(type, id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.mainThread())
                    .map { SubscriptionFragment.create(ScreenExtra.create(it)) }

    /**
     * Show Activity with subscription screen
     */
    @kotlin.jvm.JvmStatic
    fun showSubscriptionScreen(
            type: ScreenType? = null,
            id: String? = null,
            activity: Activity? = null,
            theme: Int? = null,
            onComplete: (() -> Unit)? = null,
            onError: ((Throwable) -> Unit)? = null
    ) = showSubscriptionScreenRx(type, id, activity, theme)
            .doOnComplete { onComplete?.invoke() }
            .doOnError { onError?.invoke(it) }
            .subscribe(DefaultCompletableObserver())

    /**
     * Show Activity with subscription screen
     */
    @kotlin.jvm.JvmStatic
    fun showSubscriptionScreenRx(
            type: ScreenType? = null,
            id: String? = null,
            activity: Activity? = null,
            theme: Int? = null,
    ): Completable =
            panda.getSubscriptionScreen(type, id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.mainThread())
                    .doOnSuccess {
                        val launchContext = activity ?: this.context
                        val intent = SubscriptionActivity.createIntent(launchContext, ScreenExtra.create(it), theme)
                        if (activity == null) {
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        launchContext.startActivity(intent)
                    }
                    .ignoreElement()

    fun addDismissListener(onDismiss: () -> Unit) {
        dismissListeners.add(onDismiss)
    }

    fun removeDismissListener(onDismiss: () -> Unit) {
        dismissListeners.remove(onDismiss)
    }

    fun addErrorListener(onError: (e: Throwable) -> Unit) {
        errorListeners.add(onError)
    }

    fun removeErrorListener(onError: (e: Throwable) -> Unit) {
        errorListeners.remove(onError)
    }

    fun addPurchaseListener(onPurchase: (id: String) -> Unit) {
        purchaseListeners.add(onPurchase)
    }

    fun removePurchaseListener(onPurchase: (id: String) -> Unit) {
        purchaseListeners.remove(onPurchase)
    }

    fun addRestoreListener(onRestore: (ids: List<String>) -> Unit) {
        restoreListeners.add(onRestore)
    }

    fun removeRestoreListener(onRestore: (ids: List<String>) -> Unit) {
        restoreListeners.remove(onRestore)
    }

    fun addListener(listener: PandaListener) {
        pandaListeners.add(listener)
    }

    fun removeListener(listener: PandaListener) {
        pandaListeners.remove(listener)
    }

    fun addAnalyticsListener(listener: PandaAnalyticsListener) {
        analyticsListeners.add(listener)
    }

    fun removeAnalyticsListener(listener: PandaAnalyticsListener) {
        analyticsListeners.remove(listener)
    }

    internal fun onDismiss(screen: ScreenExtra) {
        dismissListeners.forEach { it() }
        pandaListeners.forEach { it.onDismissClick() }
        analyticsListeners.forEach {
            it(PandaEvent.DismissClick(
                    screenId = screen.id,
                    screenName = screen.name
            ))
        }
    }

    internal fun onTermsClick() {
        analyticsListeners.forEach { it(PandaEvent.TermsClick) }
    }

    internal fun onPolicyClick() {
        analyticsListeners.forEach { it(PandaEvent.PolicyClick) }
    }

    internal fun restore(screenExtra: ScreenExtra): Single<List<String>> =
            panda.restore()
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.mainThread())
                    .doOnSuccess { ids ->
                        notifyRestore(ids)
                        if (ids.isNotEmpty()) {
                            notifyPurchase(screenExtra, ids.first())
                        }
                    }
                    .doOnError { e ->
                        notifyError(e)
                    }

    internal fun onPurchase(
            screenExtra: ScreenExtra,
            purchase: GooglePurchase,
            @BillingClient.SkuType type: String
    ): Single<Boolean> {
        val purchaseType = when (type) {
            BillingClient.SkuType.SUBS -> SkuType.SUBSCRIPTION
            else -> SkuType.INAPP
        }
        return panda.validatePurchase(
                Purchase(
                        id = purchase.sku,
                        type = purchaseType,
                        orderId = purchase.orderId,
                        token = purchase.purchaseToken
                ))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.mainThread())
                .doOnError { t ->
                    notifyError(t)
                }.doOnSuccess {
                    notifyPurchase(screenExtra, purchase.sku)
                }
    }

    internal fun onError(throwable: Throwable) {
        notifyError(throwable)
    }

    internal fun screenShowed(screenExtra: ScreenExtra) {
        analyticsListeners.forEach {
            it(PandaEvent.ScreenShowed(
                    screenId = screenExtra.id,
                    screenName = screenExtra.name
            ))
        }
    }

    internal fun subscriptionSelect(screenExtra: ScreenExtra, id: String) {
        analyticsListeners.forEach {
            it(PandaEvent.SubscriptionSelect(
                    productId = id,
                    screenId = screenExtra.id,
                    screenName = screenExtra.name
            ))
        }
    }

    private fun notifyError(throwable: Throwable) {
        errorListeners.forEach { it(throwable) }
        pandaListeners.forEach { it.onError(throwable) }
    }

    private fun notifyPurchase(
            screenExtra: ScreenExtra,
            skuId: String
    ) {
        purchaseListeners.forEach { it(skuId) }
        pandaListeners.forEach { it.onPurchase(skuId) }
        analyticsListeners.forEach {
            it(PandaEvent.SuccessfulPurchase(
                    screenId = screenExtra.id,
                    screenName = screenExtra.name,
                    productId = skuId
            ))
        }
    }

    private fun notifyRestore(ids: List<String>) {
        restoreListeners.forEach { it(ids) }
        pandaListeners.forEach { it.onRestore(ids) }
    }

}

class PandaDependencies {
    @Inject
    lateinit var panda: IPanda
}

