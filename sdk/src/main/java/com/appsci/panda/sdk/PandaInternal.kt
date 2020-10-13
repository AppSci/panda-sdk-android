package com.appsci.panda.sdk

import com.appsci.panda.sdk.domain.device.DeviceRepository
import com.appsci.panda.sdk.domain.subscriptions.SubscriptionState
import com.appsci.panda.sdk.domain.subscriptions.SubscriptionsRepository
import com.appsci.panda.sdk.domain.utils.DeviceManager
import com.appsci.panda.sdk.domain.utils.Preferences
import io.reactivex.Completable
import io.reactivex.Single

interface IPanda {
    fun start()
    fun authorize(): Completable
    fun setCustomUserId(id: String): Completable
    fun syncSubscriptions(): Completable
    fun getSubscriptionState(): Single<SubscriptionState>
    fun prefetchSubscriptionScreen(): Completable
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
                    .ignoreElement()
                    .andThen(deviceRepository.setCustomUserId(id))

    override fun syncSubscriptions(): Completable {
        return deviceRepository.ensureAuthorized()
                .flatMapCompletable {
                    subscriptionsRepository.sync()
                }
    }

    override fun getSubscriptionState(): Single<SubscriptionState> =
            deviceRepository.ensureAuthorized()
                    .flatMap {
                        subscriptionsRepository.getSubscriptionState(it.id)
                    }

    override fun prefetchSubscriptionScreen(): Completable =
            deviceRepository.ensureAuthorized()
                    .flatMapCompletable {
                        subscriptionsRepository.prefetchSubscriptionScreen(it.id, null, null)
                    }

}
