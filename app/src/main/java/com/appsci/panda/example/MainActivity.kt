package com.appsci.panda.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.appsci.panda.sdk.Panda
import com.appsci.panda.sdk.domain.utils.rx.DefaultCompletableObserver
import com.appsci.panda.sdk.domain.utils.rx.DefaultSingleObserver
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private val onError: (e: Throwable) -> Unit = {
        val msg = "panda error ${it.message}"
        Timber.d(msg)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnShowScreen.setOnClickListener {
            Panda.showSubscriptionScreenRx(activity = this, theme = R.style.PandaTheme)
                    .subscribe(DefaultCompletableObserver())
        }
        Panda.syncUser().subscribe()
        btnGetScreen.setOnClickListener {
            startActivity(GetScreenActivity.createIntent(this))
        }

        Panda.saveCustomUserId(id = "super-unique-custom-id")

        Panda.getSubscriptionStateRx()
                .doOnSuccess {
                    Timber.d("getSubscriptionState $it")
                }
                .doOnError { Timber.e(it) }
                .subscribe(DefaultSingleObserver())
        Panda.prefetchSubscriptionScreenRx()
                .subscribe(DefaultCompletableObserver())

        Panda.addErrorListener(onError)
    }

    override fun onDestroy() {
        Panda.removeErrorListener(onError)
        super.onDestroy()
    }
}
