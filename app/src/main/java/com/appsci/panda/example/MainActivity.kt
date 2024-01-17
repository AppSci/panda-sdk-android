package com.appsci.panda.example

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.appsci.panda.example.databinding.ActivityMainBinding
import com.appsci.panda.sdk.Panda
import com.appsci.panda.sdk.domain.utils.rx.DefaultCompletableObserver
import com.appsci.panda.sdk.domain.utils.rx.DefaultSingleObserver
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding!!

    private val onError: (e: Throwable) -> Unit = {
        val msg = "panda error ${it.message}"
        Timber.d(msg)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(R.layout.activity_main)

        with(binding) {
            btnShowScreen.setOnClickListener {
                Panda.showSubscriptionScreenRx(activity = this@MainActivity, theme = R.style.PandaTheme)
                        .subscribe(DefaultCompletableObserver())
            }
            btnGetScreen.setOnClickListener {
                startActivity(GetScreenActivity.createIntent(this@MainActivity))
            }
        }
        Panda.syncUser().subscribe(DefaultSingleObserver())

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
