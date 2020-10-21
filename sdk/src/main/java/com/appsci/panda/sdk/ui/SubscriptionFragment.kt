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
import com.appsci.panda.sdk.domain.utils.rx.DefaultSingleObserver
import com.appsci.panda.sdk.domain.utils.rx.Schedulers
import com.gen.rxbilling.client.RxBilling
import com.gen.rxbilling.flow.BuyItemRequest
import com.gen.rxbilling.flow.RxBillingFlow
import com.gen.rxbilling.flow.delegate.FragmentFlowDelegate
import com.gen.rxbilling.lifecycle.BillingConnectionManager
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.panda_fragment_subscription.*
import timber.log.Timber
import javax.inject.Inject

class SubscriptionFragment : Fragment(R.layout.panda_fragment_subscription) {

    @Inject
    lateinit var billingFlow: RxBillingFlow

    @Inject
    lateinit var billing: RxBilling

    private val disposeOnDestroyView = CompositeDisposable()

    private val screenExtra: ScreenExtra by lazy {
        requireArguments().getParcelable(EXTRA_SCREEN)!!
    }

    private val rcToType = mapOf(
            RC_SUBSCRIPTION to BillingClient.SkuType.SUBS,
            RC_PRODUCT to BillingClient.SkuType.INAPP
    )

    companion object {
        const val RC_SUBSCRIPTION = 101
        const val RC_PRODUCT = 102
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
        Panda.pandaComponent.inject(this)
        lifecycle.addObserver(BillingConnectionManager(billingFlow))
        lifecycle.addObserver(BillingConnectionManager(billing))
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        disposeOnDestroyView.add(
                billing.observeUpdates()
                        .observeOn(Schedulers.mainThread())
                        .subscribe({
                            Timber.d("observeUpdates ${it.purchases}")
                        }, {
                            Timber.e(it)
                        })
        )
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
        Panda.screenShowed(screenExtra)
    }

    override fun onDestroyView() {
        disposeOnDestroyView.clear()
        super.onDestroyView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode in listOf(RC_SUBSCRIPTION, RC_PRODUCT)) {
            val type = rcToType.getValue(requestCode)
            disposeOnDestroyView.add(
                    billingFlow.handleActivityResult(data)
                            .flatMap {
                                Timber.d("handleActivityResult $it")
                                loading.visibility = View.VISIBLE
                                Panda.onPurchase(screenExtra, it, type)
                                        .doAfterTerminate {
                                            loading.visibility = View.GONE
                                        }
                            }.subscribe({
                                Timber.d("onPurchase success=$it")
                            }, {
                                Panda.onError(it)
                                Timber.e(it)
                            })
            )
        }
    }

    private fun handleRedirect(url: String): Boolean {
        return when {
            url.contains("/subscription?type=restore") -> {
                Timber.d("restore click")
                restore()
                true
            }
            url.contains("/subscription?type=terms") -> {
                Timber.d("terms click")
                Panda.onTermsClick()
                openExternalUrl(getString(R.string.panda_terms_url))
                true
            }
            url.contains("/subscription?type=policy") -> {
                Timber.d("policy click")
                Panda.onPolicyClick()
                openExternalUrl(getString(R.string.panda_policy_url))
                true
            }
            url.contains("/subscription?type=purchase") -> {
                purchaseClick(url)
                true
            }
            url.contains("/dismiss?type=dismiss") -> {
                Timber.d("dismiss click")
                Panda.onDismiss(screenExtra)
                true
            }
            else -> false
        }
    }

    private fun restore() {
        loading.visibility = View.VISIBLE
        Panda.restore(screenExtra)
                .doOnSuccess {
                    Timber.d("restore $it")
                }
                .doAfterTerminate {
                    loading.visibility = View.GONE
                }
                .subscribe(DefaultSingleObserver())
    }

    private fun purchaseClick(url: String) {
        val subscriptions = resources.getStringArray(R.array.panda_subscriptions)
        val products = resources.getStringArray(R.array.panda_products)
        val id = url.toUri().getQueryParameter("product_id")!!
        Panda.subscriptionSelect(screenExtra, id)
        Timber.d("purchase click $id")

        val type = when {
            products.contains(id) -> {
                BillingClient.SkuType.INAPP
            }
            else -> BillingClient.SkuType.SUBS
        }
        val rc = rcToType.entries.first {
            it.value == type
        }.key
        billingFlow.buyItem(BuyItemRequest(
                type = type,
                id = id,
                requestCode = rc
        ), FragmentFlowDelegate(this))
                .doOnError {
                    Panda.onError(it)
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
