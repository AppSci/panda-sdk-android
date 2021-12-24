package com.appsci.panda.example

import android.app.Application
import com.appsci.panda.sdk.Panda
import com.facebook.stetho.Stetho
import timber.log.Timber

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        Stetho.initializeWithDefaults(this)
        Panda.initialize(this, BuildConfig.PANDA_API_KEY, false)
    }
}
