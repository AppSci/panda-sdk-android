package com.appsci.panda.sdk

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.appsci.panda.sdk.domain.subscriptions.ScreenType
import com.appsci.panda.sdk.domain.subscriptions.SubscriptionState
import com.appsci.panda.sdk.domain.utils.rx.DefaultCompletableObserver
import com.appsci.panda.sdk.domain.utils.rx.DefaultSchedulerProvider
import com.appsci.panda.sdk.domain.utils.rx.Schedulers
import com.appsci.panda.sdk.injection.components.DaggerPandaComponent
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

object Panda {

    private lateinit var panda: IPanda
    private lateinit var context: Context

    private val dismissListeners: MutableList<() -> Unit> = mutableListOf()

    @kotlin.jvm.JvmStatic
    fun configure(context: Context, apiKey: String, debug: Boolean = BuildConfig.DEBUG) {
        this.context = context
        Schedulers.setInstance(DefaultSchedulerProvider())
        AndroidThreeTen.init(context)
        val wrapper = PandaDependencies()
        val pandaComponent = DaggerPandaComponent
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
        panda.authorize()
                .subscribeOn(Schedulers.io())
                .doOnComplete {
                    Timber.d("authorize success")
                }
                .andThen(panda.syncSubscriptions())
                .observeOn(Schedulers.mainThread())
                .subscribe(DefaultCompletableObserver())
    }

    @kotlin.jvm.JvmStatic
    fun setCustomUserId(id: String) {
        panda.setCustomUserId(id)
                .subscribeOn(Schedulers.io())
                .subscribe(DefaultCompletableObserver())
    }

    @kotlin.jvm.JvmStatic
    fun syncSubscriptions() {
        panda.syncSubscriptions()
                .subscribeOn(Schedulers.io())
                .subscribe(DefaultCompletableObserver())
    }

    @kotlin.jvm.JvmStatic
    fun getSubscriptionState(): Single<SubscriptionState> =
            panda.getSubscriptionState()
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.mainThread())

    @kotlin.jvm.JvmStatic
    fun prefetchSubscriptionScreen(type: ScreenType = ScreenType.Sales, id: String? = null) =
            panda.prefetchSubscriptionScreen()
                    .subscribeOn(Schedulers.io())
                    .subscribe(DefaultCompletableObserver())

    @kotlin.jvm.JvmStatic
    fun getSubscriptionScreen(type: ScreenType? = null, id: String? = null): Single<Fragment> =
            panda.getSubscriptionScreen(type, id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.mainThread())
                    .map { SubscriptionFragment.create(ScreenExtra.create(it)) }

    @kotlin.jvm.JvmStatic
    fun showSubscriptionScreen(
            type: ScreenType? = null,
            id: String? = null,
            activity: Activity? = null,
            theme: Int? = null
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

    internal fun onDismiss() {
        dismissListeners.forEach { it() }
    }

}

class PandaDependencies {
    @Inject
    lateinit var panda: IPanda
}

