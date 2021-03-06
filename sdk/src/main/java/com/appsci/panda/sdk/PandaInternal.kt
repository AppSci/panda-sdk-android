package com.appsci.panda.sdk

import com.appsci.panda.sdk.domain.device.DeviceRepository
import com.appsci.panda.sdk.domain.subscriptions.*
import com.appsci.panda.sdk.domain.utils.DeviceManager
import com.appsci.panda.sdk.domain.utils.Preferences
import com.appsci.panda.sdk.domain.utils.rx.Schedulers
import io.reactivex.Completable
import io.reactivex.Single
import java.util.concurrent.TimeUnit

interface IPanda {
    val pandaUserId: String?

    fun start()
    fun authorize(): Single<String>
    fun setCustomUserId(id: String?): Completable
    fun syncSubscriptions(): Completable
    fun validatePurchase(purchase: Purchase): Single<Boolean>
    fun restore(): Single<List<String>>
    fun getSubscriptionState(): Single<SubscriptionState>
    fun prefetchSubscriptionScreen(type: ScreenType? = null, id: String? = null): Single<SubscriptionScreen>
    fun getSubscriptionScreen(type: ScreenType? = null, id: String? = null, timeoutMs: Long = 5000L): Single<SubscriptionScreen>
    fun consumeProducts(): Completable
    fun setAppsflyerId(id: String): Completable
    fun setFbIds(fbc: String?, fbp: String?): Completable

    /**
     * save appsflyer id in local storage, will be used in next update request
     */
    fun saveAppsflyerId(id: String)
}

class PandaImpl(
        private val preferences: Preferences,
        private val deviceManager: DeviceManager,
        private val deviceRepository: DeviceRepository,
        private val subscriptionsRepository: SubscriptionsRepository
) : IPanda {

    override val pandaUserId: String?
        get() = deviceRepository.pandaUserId

    override fun start() {
        if (preferences.startVersion.isNullOrEmpty()) {
            preferences.startVersion = deviceManager.getAppVersionName()
        }
    }

    override fun authorize(): Single<String> =
            deviceRepository.authorize()
                    .map { it.id }

    override fun setCustomUserId(id: String?): Completable {
        if (preferences.customUserId == id) return Completable.complete()
        preferences.customUserId = id
        return Completable.defer {
            deviceRepository.authorize()
                    .ignoreElement()
        }
    }

    override fun setAppsflyerId(id: String): Completable {
        if (preferences.appsflyerId == id) return Completable.complete()
        preferences.appsflyerId = id
        return Completable.defer {
            deviceRepository.authorize()
                    .ignoreElement()
        }
    }

    override fun setFbIds(fbc: String?, fbp: String?): Completable {
        if (preferences.fbc == fbc && preferences.fbp == fbp) return Completable.complete()
        preferences.fbc = fbc
        preferences.fbp = fbp
        return Completable.defer {
            deviceRepository.ensureAuthorized()
                    .andThen(deviceRepository.authorize())
                    .ignoreElement()
        }
    }

    override fun saveAppsflyerId(id: String) {
        preferences.appsflyerId = id
    }

    override fun syncSubscriptions(): Completable {
        return deviceRepository.ensureAuthorized()
                .andThen(subscriptionsRepository.sync())
    }

    override fun validatePurchase(purchase: Purchase): Single<Boolean> {
        return deviceRepository.ensureAuthorized()
                .andThen(subscriptionsRepository.validatePurchase(purchase))
    }

    override fun restore(): Single<List<String>> =
            deviceRepository.ensureAuthorized()
                    .andThen(subscriptionsRepository.restore())

    override fun getSubscriptionState(): Single<SubscriptionState> =
            deviceRepository.ensureAuthorized()
                    .andThen(subscriptionsRepository.getSubscriptionState())

    override fun prefetchSubscriptionScreen(type: ScreenType?, id: String?): Single<SubscriptionScreen> =
            deviceRepository.ensureAuthorized()
                    .andThen(subscriptionsRepository.prefetchSubscriptionScreen(type, id))

    override fun getSubscriptionScreen(type: ScreenType?, id: String?, timeoutMs: Long): Single<SubscriptionScreen> =
            deviceRepository.ensureAuthorized()
                    .andThen(subscriptionsRepository.getSubscriptionScreen(type, id))
                    .timeout(timeoutMs, TimeUnit.MILLISECONDS, Schedulers.computation())
                    .onErrorResumeNext {
                        subscriptionsRepository.getFallbackScreen()
                    }

    override fun consumeProducts(): Completable =
            deviceRepository.ensureAuthorized()
                    .andThen(subscriptionsRepository.consumeProducts())

}
