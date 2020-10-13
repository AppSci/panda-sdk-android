package com.appsci.panda.sdk

import android.content.Context
import com.appsci.panda.sdk.domain.subscriptions.SubscriptionState
import com.appsci.panda.sdk.domain.utils.rx.AppSchedulers
import com.appsci.panda.sdk.injection.components.DaggerPandaComponent
import com.appsci.panda.sdk.injection.modules.AppModule
import com.appsci.panda.sdk.injection.modules.BillingModule
import com.appsci.panda.sdk.injection.modules.NetworkModule
import com.appsci.tenwords.domain.utils.DefaultCompletableObserver
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject

object Panda {

    private lateinit var panda: IPanda

    private val disposable = CompositeDisposable()

    @kotlin.jvm.JvmStatic
    fun configure(context: Context, apiKey: String, debug: Boolean = BuildConfig.DEBUG) {
        val wrapper = PandaDependencies()
        DaggerPandaComponent
                .builder()
                .appModule(AppModule(context.applicationContext))
                .billingModule(BillingModule(context))
                .networkModule(NetworkModule(
                        debug = debug,
                        apiKey = apiKey
                ))
                .build()
                .inject(wrapper)
        panda = wrapper.panda
        panda.start()
        disposable.add(
                panda.authorize()
                        .subscribeOn(AppSchedulers.io())
                        .doOnComplete {
                            Timber.d("authorize success")
                        }
                        .andThen(panda.syncSubscriptions())
                        .observeOn(AppSchedulers.mainThread())
                        .subscribeWith(DefaultCompletableObserver())
        )
    }

    @kotlin.jvm.JvmStatic
    fun setCustomUserId(id: String) {
        disposable.add(
                panda.setCustomUserId(id)
                        .subscribeOn(AppSchedulers.io())
                        .observeOn(AppSchedulers.mainThread())
                        .subscribeWith(DefaultCompletableObserver())
        )
    }

    @kotlin.jvm.JvmStatic
    fun syncSubscriptions(): Completable =
            panda.syncSubscriptions()
                    .subscribeOn(AppSchedulers.io())
                    .observeOn(AppSchedulers.mainThread())

    @kotlin.jvm.JvmStatic
    fun getSubscriptionState(): Single<SubscriptionState> =
            panda.getSubscriptionState()
                    .subscribeOn(AppSchedulers.io())
                    .observeOn(AppSchedulers.mainThread())

    @kotlin.jvm.JvmStatic
    fun prefetchSubscriptionScreen(): Completable =
            panda.prefetchSubscriptionScreen()
                    .subscribeOn(AppSchedulers.io())
                    .observeOn(AppSchedulers.mainThread())
}

class PandaDependencies() {
    @Inject
    lateinit var panda: IPanda
}

