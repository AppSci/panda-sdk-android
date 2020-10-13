package com.appsci.tenwords.domain.utils

import io.reactivex.observers.DisposableCompletableObserver
import timber.log.Timber

open class DefaultCompletableObserver : DisposableCompletableObserver() {
    override fun onComplete() {

    }

    override fun onError(e: Throwable) {
        Timber.e(e)
    }

}
