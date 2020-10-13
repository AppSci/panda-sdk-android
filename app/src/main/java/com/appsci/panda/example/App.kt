package com.appsci.panda.example

import android.app.Application
import com.appsci.panda.sdk.Panda
import com.appsci.panda.sdk.domain.utils.rx.AppSchedulers
import com.appsci.panda.sdk.domain.utils.rx.DefaultSchedulerProvider
import com.facebook.stetho.Stetho
import com.jakewharton.threetenabp.AndroidThreeTen
import timber.log.Timber

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        AppSchedulers.setInstance(DefaultSchedulerProvider())
        AndroidThreeTen.init(this)
        Stetho.initializeWithDefaults(this)
        Panda.configure(this, BuildConfig.PANDA_API_KEY, true)
    }
}
