package com.appsci.panda.sdk.data

import io.reactivex.Completable
import okhttp3.OkHttpClient
import javax.inject.Inject

class StopNetwork @Inject constructor(
        private val okHttpClient: OkHttpClient
) : () -> Completable {

    override operator fun invoke(): Completable =
            Completable.fromAction {
                okHttpClient.dispatcher.cancelAll()
                okHttpClient.cache?.evictAll()
            }
}
