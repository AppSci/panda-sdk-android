package com.appsci.panda.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.appsci.panda.sdk.Panda
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Panda.setCustomUserId("super-unique-custom-id")

        Panda.getSubscriptionState()
                .subscribe({
                    Timber.d("getSubscriptionState $it")
                }, {
                    Timber.e(it)
                })

        Panda.prefetchSubscriptionScreen()
                .subscribe({

                }, {
                    Timber.e(it)
                })

        Panda.syncSubscriptions()
                .subscribe({
                    Timber.d("syncSubscriptions")
                }, {
                    Timber.e(it)
                })
    }
}
