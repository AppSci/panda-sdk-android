package com.appsci.panda.sdk.domain.utils.rx

import io.reactivex.observers.DisposableCompletableObserver
import timber.log.Timber

open class DefaultCompletableObserver : DisposableCompletableObserver() {
    override fun onComplete() {

    }

    override fun onError(e: Throwable) {
        Timber.e(e)
    }

}
