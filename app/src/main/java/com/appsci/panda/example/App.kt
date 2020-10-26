package com.appsci.panda.example

import android.app.Application
import com.appsci.panda.sdk.Panda
import com.appsci.panda.sdk.domain.utils.rx.DefaultSingleObserver
import com.facebook.stetho.Stetho
import timber.log.Timber

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        Stetho.initializeWithDefaults(this)
        Panda.configureRx(this, BuildConfig.PANDA_API_KEY, true)
                .subscribe(DefaultSingleObserver())
    }
}
