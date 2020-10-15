package com.appsci.panda.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.appsci.panda.sdk.Panda
import com.appsci.panda.sdk.domain.utils.rx.DefaultCompletableObserver
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnShowScreen.setOnClickListener {
            Panda.showSubscriptionScreen(activity = this, theme = null)
                    .subscribe(DefaultCompletableObserver())
        }

        btnGetScreen.setOnClickListener {
            startActivity(GetScreenActivity.createIntent(this))
        }

        Panda.setCustomUserId("super-unique-custom-id")

        Panda.getSubscriptionState()
                .subscribe({
                    Timber.d("getSubscriptionState $it")
                }, {
                    Timber.e(it)
                })
        Panda.prefetchSubscriptionScreen()
        Panda.syncSubscriptions()
    }
}
