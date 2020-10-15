package com.appsci.panda.sdk

import com.appsci.panda.sdk.domain.device.DeviceRepository
import com.appsci.panda.sdk.domain.subscriptions.SubscriptionScreen
import com.appsci.panda.sdk.domain.subscriptions.SubscriptionState
import com.appsci.panda.sdk.domain.subscriptions.SubscriptionsRepository
import com.appsci.panda.sdk.domain.utils.DeviceManager
import com.appsci.panda.sdk.domain.utils.Preferences
import com.appsci.panda.sdk.domain.utils.rx.Schedulers
import io.reactivex.Completable
import io.reactivex.Single
import java.util.concurrent.TimeUnit

interface IPanda {
    fun start()
    fun authorize(): Completable
    fun setCustomUserId(id: String): Completable
    fun syncSubscriptions(): Completable
    fun getSubscriptionState(): Single<SubscriptionState>
    fun prefetchSubscriptionScreen(): Completable
    fun getSubscriptionScreen(): Single<SubscriptionScreen>
}

class PandaImpl(
        private val preferences: Preferences,
        private val deviceManager: DeviceManager,
        private val deviceRepository: DeviceRepository,
        private val subscriptionsRepository: SubscriptionsRepository
) : IPanda {

    override fun start() {
        if (preferences.startVersion.isNullOrEmpty()) {
            preferences.startVersion = deviceManager.getAppVersionName()
        }
    }

    override fun authorize(): Completable {
        return deviceRepository.authorize()
                .ignoreElement()
    }

    override fun setCustomUserId(id: String): Completable =
            deviceRepository.ensureAuthorized()
                    .andThen(deviceRepository.setCustomUserId(id))

    override fun syncSubscriptions(): Completable {
        return deviceRepository.ensureAuthorized()
                .andThen(subscriptionsRepository.sync())
    }

    override fun getSubscriptionState(): Single<SubscriptionState> =
            deviceRepository.ensureAuthorized()
                    .andThen(subscriptionsRepository.getSubscriptionState())

    override fun prefetchSubscriptionScreen(): Completable =
            deviceRepository.ensureAuthorized()
                    .andThen(subscriptionsRepository.prefetchSubscriptionScreen(null, null))

    override fun getSubscriptionScreen(): Single<SubscriptionScreen> =
            deviceRepository.ensureAuthorized()
                    .andThen(subscriptionsRepository.getSubscriptionScreen(null, null))
                    .timeout(5, TimeUnit.SECONDS, Schedulers.computation())

}
