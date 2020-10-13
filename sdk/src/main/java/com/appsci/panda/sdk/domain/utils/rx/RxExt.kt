package com.appsci.panda.sdk.domain.utils.rx

import io.reactivex.Single

fun <T> Single<T>.shareSingle(): Single<T> {
    return this.compose {
        return@compose it.toFlowable()
                .replay(1)
                .refCount()
                .singleOrError()
    }
}
