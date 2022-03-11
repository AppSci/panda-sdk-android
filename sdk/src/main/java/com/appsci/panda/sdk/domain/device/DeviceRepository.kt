package com.appsci.panda.sdk.domain.device

import io.reactivex.Completable
import io.reactivex.Single

interface DeviceRepository {

    val pandaUserId: String?

    fun authorize(): Single<Device>

    fun clearAdvId(): Completable

    fun ensureAuthorized(): Completable

    fun getAuthState(): Single<AuthState>

    fun deleteDevice(): Completable

    fun clearLocalData(): Completable
}
