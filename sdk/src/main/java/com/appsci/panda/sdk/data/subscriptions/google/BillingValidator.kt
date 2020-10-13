package com.appsci.panda.sdk.data.subscriptions.google

import io.reactivex.Completable

interface BillingValidator {
    fun validateIntent(): Completable
}

