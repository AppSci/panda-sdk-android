package com.appsci.panda.sdk.domain.utils.rx

import io.reactivex.observers.DisposableSingleObserver
import timber.log.Timber

open class DefaultSingleObserver<T> : DisposableSingleObserver<T>() {

    override fun onSuccess(t: T) {
    }

    override fun onError(e: Throwable) {
        Timber.e(e)
    }
}
