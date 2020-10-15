package com.appsci.panda.sdk.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.appsci.panda.sdk.Panda
import com.appsci.panda.sdk.R

class SubscriptionActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_THEME = "theme"
        fun createIntent(context: Context, screenExtra: ScreenExtra, theme: Int?) =
                Intent(context, SubscriptionActivity::class.java)
                        .putExtra(SubscriptionFragment.EXTRA_SCREEN, screenExtra).apply {
                            theme?.let { this.putExtra(EXTRA_THEME, theme) }
                        }
    }

    private val onDismiss = {
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        intent.getIntExtra(EXTRA_THEME, -1).let {
            if (it > 0) setTheme(it)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.panda_activity_subscription)
        supportFragmentManager.beginTransaction()
                .replace(
                        R.id.container,
                        SubscriptionFragment.create(intent.getParcelableExtra(SubscriptionFragment.EXTRA_SCREEN)!!)
                ).commitNow()
    }

    override fun onBackPressed() {
        Panda.onDismiss()
    }

    override fun onStart() {
        super.onStart()
        Panda.addDismissListener(onDismiss)
    }

    override fun onStop() {
        Panda.removeDismissListener(onDismiss)
        super.onStop()
    }
}
