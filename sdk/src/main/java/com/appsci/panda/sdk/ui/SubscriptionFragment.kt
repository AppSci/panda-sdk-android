package com.appsci.panda.sdk.ui

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.android.billingclient.api.BillingClient
import com.appsci.panda.sdk.Panda
import com.appsci.panda.sdk.R
import com.appsci.panda.sdk.domain.subscriptions.SubscriptionScreen
import com.appsci.panda.sdk.domain.utils.rx.DefaultCompletableObserver
import com.gen.rxbilling.connection.BillingServiceFactory
import com.gen.rxbilling.flow.BuyItemRequest
import com.gen.rxbilling.flow.RxBillingFlow
import com.gen.rxbilling.flow.delegate.FragmentFlowDelegate
import com.gen.rxbilling.lifecycle.BillingConnectionManager
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.panda_fragment_subscription.*
import timber.log.Timber

class SubscriptionFragment : Fragment(R.layout.panda_fragment_subscription) {

    private lateinit var billingFlow: RxBillingFlow

    private val screenExtra: ScreenExtra by lazy {
        requireArguments().getParcelable(EXTRA_SCREEN)!!
    }

    companion object {
        const val RC_SUBSCRIPTION = 101
        const val EXTRA_SCREEN = "screenExtra"
        fun create(screenExtra: ScreenExtra) =
                SubscriptionFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(EXTRA_SCREEN, screenExtra)
                    }
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        billingFlow = RxBillingFlow(
                context = requireActivity(),
                factory = BillingServiceFactory(requireActivity())
        )
        lifecycle.addObserver(BillingConnectionManager(billingFlow))
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.panda_screen_bg))
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest): Boolean {
                Timber.d("shouldOverrideUrlLoading1 ${request.url}")
                return handleRedirect(request.url.toString())
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
                Timber.d("shouldOverrideUrlLoading2 $url")
                return handleRedirect(url)
            }

        }
        webView.loadDataWithBaseURL("file:///android_asset/", screenExtra.html, null, null, null)
    }

    private fun handleRedirect(url: String): Boolean {
        return when {
            url.contains("/subscription?type=restore") -> {
                Timber.d("restore click")
                true
            }
            url.contains("/subscription?type=terms") -> {
                Timber.d("terms click")
                openExternalUrl(getString(R.string.panda_terms_url))
                true
            }
            url.contains("/subscription?type=policy") -> {
                Timber.d("policy click")
                openExternalUrl(getString(R.string.panda_policy_url))
                true
            }
            url.contains("/subscription?type=purchase") -> {
                purchaseClick(url)
                true
            }
            url.contains("/dismiss?type=dismiss") -> {
                Timber.d("dismiss click")
                Panda.onDismiss()
                true
            }
            else -> false
        }
    }

    private fun purchaseClick(url: String) {
        val subscriptions = resources.getStringArray(R.array.panda_subscriptions)
        val products = resources.getStringArray(R.array.panda_products)
        val id = url.toUri().getQueryParameter("product_id")!!
        Timber.d("purchase click $id")
        billingFlow.buyItem(BuyItemRequest(
                type = BillingClient.SkuType.SUBS,
                id = id,
                requestCode = RC_SUBSCRIPTION
        ), FragmentFlowDelegate(this))
                .doOnError {
                    Timber.e(it)
                }
                .subscribe(DefaultCompletableObserver())
    }

    private fun openExternalUrl(url: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
        } catch (e: ActivityNotFoundException) {
            Timber.e(e)
        }
    }

}

@Parcelize
data class ScreenExtra(
        val id: String,
        val name: String,
        val html: String
) : Parcelable {
    companion object {
        fun create(screen: SubscriptionScreen) =
                ScreenExtra(
                        id = screen.id,
                        name = screen.name,
                        html = screen.screenHtml
                )
    }
}
