package com.appsci.panda.sdk

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Intent
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.ProductDetails
import com.appsci.panda.sdk.data.network.GooglePayResponse
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Inject
import com.android.billingclient.api.Purchase as GooglePurchase

object Panda {

    @Volatile
    private var initialized: Boolean = false

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
     * test
     */
    suspend fun requestGooglePayment(
        name: String = "",
        purchaseUrl: String = "",
        productId: String = "",
        signature: String = "",
        protocolVersion: String = "",
        signedMessage: String = "",
        userId: String = "",
        sandbox: Boolean = false,
        wallet: String = "",
        orderDescription: String = "",
    ): GooglePayResponse {
        return panda.requestGooglePayment(
            name = name,
            purchaseUrl = purchaseUrl,
            productId = productId,
            signature = signature,
            protocolVersion = protocolVersion,
            signedMessage = signedMessage,
            userId = userId,
            sandbox = sandbox,
            wallet = wallet,
            orderDescription = orderDescription
        )
    }

    /**
     * Call this function on App start to configure Panda SDK
     */
    @kotlin.jvm.JvmStatic
    fun initialize(
        context: Application,
        apiKey: String,
        debug: Boolean = BuildConfig.DEBUG,
        networkLogLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.BASIC,
    ) {
        initializeInternal(context, apiKey, debug, networkLogLevel)
    }

    /**
     * @returns
     */
    val pandaUserId: String?
        get() = panda.pandaUserId

    @kotlin.jvm.JvmStatic
    fun setFbIds(
        fbc: String?,
        fbp: String?,
        onComplete: (() -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null,
    ) = setFbIdsRx(fbc = fbc, fbp = fbp)
        .doOnComplete { onComplete?.invoke() }
        .doOnError { onError?.invoke(it) }
        .subscribe(DefaultCompletableObserver())

    @kotlin.jvm.JvmStatic
    fun clearAdvId(): Completable =
        panda.clearAdvId()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.mainThread())

    @kotlin.jvm.JvmStatic
    fun syncUser(): Single<String> =
        panda.authorize()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.mainThread())

    @kotlin.jvm.JvmStatic
    fun setFbIdsRx(
        fbc: String?,
        fbp: String?,
    ): Completable =
        panda.setFbIds(fbc = fbc, fbp = fbp)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.mainThread())

    @kotlin.jvm.JvmStatic
    suspend fun setProperty(
        key: String,
        value: String,
    ) = panda.setUserProperty(key = key, value = value)

    @kotlin.jvm.JvmStatic
    suspend fun setProperties(
        map: Map<String, String>,
    ) = panda.setUserProperties(map)

    @kotlin.jvm.JvmStatic
    suspend fun sendFeedback(
        screenId: String,
        answer: String,
    ) = withContext(Dispatchers.IO) {
        panda.sendFeedback(screenId = screenId, answer = answer)
    }

    /**
     * Set custom user id to current user
     * @param id - your custom userId,
     */
    @kotlin.jvm.JvmStatic
    fun saveCustomUserId(id: String?) {
        panda.saveCustomUserId(id)
    }

    @kotlin.jvm.JvmStatic
    fun saveLoginData(
        loginData: LoginData,
    ) = panda.saveLoginData(loginData)

    /**
     * Set appsflyer id to current user
     * @param id - your appsflyer Id,
     */
    @kotlin.jvm.JvmStatic
    fun saveAppsflyerId(id: String?) {
        id?.let {
            panda.saveAppsflyerId(id)
        }
    }

    /**
     * Gets subscriptions from google and sends to Panda server
     */
    @kotlin.jvm.JvmStatic
    fun syncSubscriptions(
        onComplete: (() -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null,
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
        onError: ((Throwable) -> Unit)? = null,
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
        onError: ((Throwable) -> Unit)? = null,
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
        onError: ((Throwable) -> Unit)? = null,
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

    @kotlin.jvm.JvmStatic
    fun getCachedSubscriptionScreen(
        type: ScreenType? = null,
        id: String? = null,
    ) = panda.getCachedSubscriptionScreen(type = type, id = id)

    @kotlin.jvm.JvmStatic
    fun getCachedOrDefaultSubscriptionScreen(
        type: ScreenType? = null,
        id: String? = null,
    ) = panda.getCachedOrDefaultSubscriptionScreen(type, id)
        .map { SubscriptionFragment.create(ScreenExtra.create(it)) }

    /**
     * Get Fragment with subscription UI that handles billing flow
     */
    @kotlin.jvm.JvmStatic
    fun getSubscriptionScreen(
        type: ScreenType? = null,
        id: String? = null,
        onSuccess: ((SubscriptionFragment) -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null,
    ) = getSubscriptionScreenRx(type, id)
        .doOnSuccess { onSuccess?.invoke(it) }
        .doOnError { onError?.invoke(it) }
        .subscribe(DefaultSingleObserver())

    /**
     * Get Fragment with subscription UI that handles billing flow
     */
    @kotlin.jvm.JvmStatic
    fun getSubscriptionScreenRx(
        type: ScreenType? = null,
        id: String? = null,
    ): Single<SubscriptionFragment> =
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
        onError: ((Throwable) -> Unit)? = null,
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
                val intent =
                    SubscriptionActivity.createIntent(launchContext, ScreenExtra.create(it), theme)
                if (activity == null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                launchContext.startActivity(intent)
            }
            .ignoreElement()

    @JvmStatic
    suspend fun getProductsDetails(requests: Map<String, List<String>>): List<ProductDetails> =
        withContext(Dispatchers.IO) {
            panda.getProductsDetails(requests)
        }

    @JvmStatic
    fun dropData() = panda.stopNetwork()
        .andThen(panda.clearLocalData())

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
            it(
                PandaEvent.DismissClick(
                    screenId = screen.id,
                    screenName = screen.name,
                )
            )
        }
    }

    internal fun onShowCloseConfirmation(screenExtra: ScreenExtra) {
        pandaListeners.forEach { it.onShowCloseConfirmation() }
        analyticsListeners.forEach {
            it(
                PandaEvent.ShowCloseConfirmation(
                    screenId = screenExtra.id,
                    screenName = screenExtra.name,
                )
            )
        }
    }

    internal fun onBackClick(screen: ScreenExtra) {
        pandaListeners.forEach { it.onBackClick() }
        analyticsListeners.forEach {
            it(
                PandaEvent.BackClick(
                    screenId = screen.id,
                    screenName = screen.name,
                )
            )
        }
    }

    internal fun onTermsClick() {
        analyticsListeners.forEach { it(PandaEvent.TermsClick) }
    }

    internal fun onPolicyClick() {
        analyticsListeners.forEach { it(PandaEvent.PolicyClick) }
    }

    internal fun onOpenExternal(screenId: String, url: String) {
        analyticsListeners.forEach { it(PandaEvent.OpenExternal(screenId, url)) }
    }

    internal fun onRedirect(screenId: String, url: String) {
        analyticsListeners.forEach { it(PandaEvent.Redirect(screenId, url)) }
    }

    internal fun onCustomEvent(screenId: String, name: String, params: Map<String, String>) {
        analyticsListeners.forEach {
            it(
                PandaEvent.CustomEvent(
                    name = name,
                    screenId = screenId,
                    params = params,
                )
            )
        }
    }

    fun onAction(name: String, json: String) {
        analyticsListeners.forEach {
            it(
                PandaEvent.Action(
                    name = name,
                    json = json,
                )
            )
        }
    }

    fun onScreenChanged(id: String, screenName: String) {
        analyticsListeners.forEach {
            it(
                PandaEvent.ScreenChanged(
                    screenId = id,
                    screenName = screenName,
                )
            )
        }
    }

    internal fun restore(screenExtra: ScreenExtra): Single<List<String>> =
        restore()
            .doOnSuccess { ids ->
                notifyRestore(ids)
                if (ids.isNotEmpty()) {
                    notifyPurchase(screenExtra, ids.first())
                }
            }
            .doOnError { e ->
                notifyError(e)
            }

    fun restore(): Single<List<String>> =
        panda.restore()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.mainThread())

    internal fun onPurchase(
        screenExtra: ScreenExtra,
        purchase: GooglePurchase,
        @BillingClient.SkuType type: String,
    ): Single<Boolean> {
        val purchaseType = when (type) {
            BillingClient.SkuType.SUBS -> SkuType.SUBSCRIPTION
            else -> SkuType.INAPP
        }
        return panda.validatePurchase(
            Purchase(
                id = purchase.skus.first(),
                type = purchaseType,
                orderId = purchase.orderId,
                token = purchase.purchaseToken
            )
        )
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.mainThread())
            .doOnError { t ->
                notifyError(t)
            }.doOnSuccess {
                notifyPurchase(screenExtra, purchase.skus.first())
            }
    }

    internal fun onError(throwable: Throwable) {
        notifyError(throwable)
    }

    internal fun screenShowed(screenExtra: ScreenExtra) {
        analyticsListeners.forEach {
            it(
                PandaEvent.ScreenShowed(
                    screenId = screenExtra.id,
                    screenName = screenExtra.name,
                )
            )
        }
    }

    internal fun subscriptionSelect(screenExtra: ScreenExtra, id: String) {
        analyticsListeners.forEach {
            it(
                PandaEvent.SubscriptionSelect(
                    productId = id,
                    screenId = screenExtra.id,
                    screenName = screenExtra.name,
                )
            )
        }
    }

    private fun notifyError(throwable: Throwable) {
        errorListeners.forEach { it(throwable) }
        pandaListeners.forEach { it.onError(throwable) }
    }

    private fun notifyPurchase(
        screenExtra: ScreenExtra,
        skuId: String,
    ) {
        pandaListeners.forEach { it.onPurchase(skuId) }
        analyticsListeners.forEach {
            it(
                PandaEvent.SuccessfulPurchase(
                    screenId = screenExtra.id,
                    screenName = screenExtra.name,
                    productId = skuId,
                )
            )
        }
        purchaseListeners.forEach { it(skuId) }
    }

    private fun notifyRestore(ids: List<String>) {
        restoreListeners.forEach { it(ids) }
        pandaListeners.forEach { it.onRestore(ids) }
    }

    private fun initializeInternal(
        context: Application,
        apiKey: String,
        debug: Boolean = BuildConfig.DEBUG,
        networkLogLevel: HttpLoggingInterceptor.Level,
    ) {
        if (initialized) return
        this.context = context
        Schedulers.setInstance(DefaultSchedulerProvider())
        AndroidThreeTen.init(context)
        val wrapper = PandaDependencies()
        pandaComponent = DaggerPandaComponent
            .builder()
            .appModule(AppModule(context.applicationContext))
            .billingModule(BillingModule(context))
            .networkModule(
                NetworkModule(
                    debug = debug,
                    apiKey = apiKey,
                    networkLogLevel = networkLogLevel,
                )
            )
            .build()
        pandaComponent.inject(wrapper)
        panda = wrapper.panda
        panda.onStart()
        initialized = true
    }

}

class PandaDependencies {
    @Inject
    lateinit var panda: IPanda
}

