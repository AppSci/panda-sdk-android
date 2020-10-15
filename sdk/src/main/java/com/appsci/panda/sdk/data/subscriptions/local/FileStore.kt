package com.appsci.panda.sdk.data.subscriptions.local

import android.content.Context
import com.appsci.panda.sdk.domain.subscriptions.SubscriptionScreen
import io.reactivex.Single

interface FileStore {
    fun getSubscriptionScreen(): Single<SubscriptionScreen>
}

class FileStoreImpl(
        private val context: Context
) : FileStore {

    override fun getSubscriptionScreen(): Single<SubscriptionScreen> {
        return Single.fromCallable {
            context.assets.open("panda-index.html")
                    .bufferedReader()
                    .use { it.readText() }
        }.map {
            SubscriptionScreen(
                    id = "",
                    name = "",
                    screenHtml = it
            )
        }
    }
}
