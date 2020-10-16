package com.appsci.panda.example

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.appsci.panda.sdk.Panda
import com.appsci.panda.sdk.domain.utils.rx.DefaultSingleObserver

class GetScreenActivity : AppCompatActivity() {

    companion object {
        fun createIntent(activity: Activity) =
                Intent(activity, GetScreenActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_screen)
        Panda.getSubscriptionScreenRx()
                .doOnSuccess {
                    supportFragmentManager.beginTransaction()
                            .replace(R.id.container, it)
                            .commitNow()
                }
                .subscribe(DefaultSingleObserver())
    }
}
