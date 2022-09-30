package com.appsci.panda.sdk.ui

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.SkuDetailsParams
import com.appsci.panda.sdk.Panda
import com.appsci.panda.sdk.R
import com.appsci.panda.sdk.databinding.PandaFragmentSubscriptionBinding
import com.appsci.panda.sdk.domain.subscriptions.SubscriptionScreen
import com.appsci.panda.sdk.domain.subscriptions.SubscriptionsRepository
import com.appsci.panda.sdk.domain.utils.getStringOrNull
import com.appsci.panda.sdk.domain.utils.rx.DefaultCompletableObserver
import com.appsci.panda.sdk.domain.utils.rx.DefaultSingleObserver
import com.appsci.panda.sdk.domain.utils.rx.Schedulers
import com.gen.rxbilling.client.PurchasesUpdate
import com.gen.rxbilling.client.RxBilling
import com.gen.rxbilling.lifecycle.BillingConnectionManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject

class SubscriptionFragment : Fragment() {

    @Inject
    lateinit var billing: RxBilling

    @Inject
    lateinit var subscriptionsRepository: SubscriptionsRepository

    private val disposeOnDestroyView = CompositeDisposable()

    private var _binding: PandaFragmentSubscriptionBinding? = null
    private val binding: PandaFragmentSubscriptionBinding
        get() = _binding!!

    private var onSuccessfulPurchase: (() -> Unit)? = null
    private val onPurchaseListener: (String) -> Unit = {
        onSuccessfulPurchase?.invoke()
    }

    private val screenExtra: ScreenExtra by lazy {
        requireArguments().getParcelable(EXTRA_SCREEN)!!
    }

    private val screenPayload: JSONObject? by lazy {
        requireArguments().getString(EXTRA_PAYLOAD)?.let {
            JSONObject(it)
        }
    }

    companion object {
        const val EXTRA_SCREEN = "screenExtra"
        const val EXTRA_PAYLOAD = "screenPayload"

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
        lifecycle.addObserver(BillingConnectionManager(billing))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return PandaFragmentSubscriptionBinding.inflate(inflater).apply {
            _binding = this
        }.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                binding.webView.evaluateJavascript("onBackPressed();") {
                    val handled = it.toBoolean()
                    Timber.d("onBackPressed result $it")
                    if (!handled) {
                        Panda.onDismiss(screenExtra)
                    }
                }
            }
        })
        binding.webView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.panda_screen_bg))
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.isHorizontalScrollBarEnabled = false
        binding.webView.isVerticalScrollBarEnabled = false

        // set on view created, remove on destroy view
        Panda.addPurchaseListener(onPurchaseListener)

        val jsBridge = object : JavaScriptBridgeInterface {
            override fun onPurchase(json: String) {
                Timber.d("onPurchase $json")
                onSuccessfulPurchase = null
                val obj = JSONObject(json)
                val productId = obj.getString("product_id")
                val type = obj.getStringOrNull("type")
                val url = obj.getStringOrNull("url")

                if (type == "external" && url != null) {
                    onSuccessfulPurchase = {
                        openExternalUrl(url)
                    }
                }
                if (type == "moveNext" && url != null) {
                    onSuccessfulPurchase = {
                        Timber.d("moveNext $url")
                        binding.webView.evaluateJavascript("moveNext();") {
                            Timber.d("moveNext result $it")
                        }
                    }
                }
                purchaseClick(productId)
            }

            override fun onScreenChanged(json: String) {
                Timber.d("onScreenChanged $json")
                val name = JSONObject(json).getString("screen_name")
                Panda.onScreenChanged(
                        id = screenExtra.id,
                        screenName = name,
                )
            }

            override fun onRedirect(json: String) {
                Timber.d("onRedirect $json")
                val url = JSONObject(json).getString("url")

                Panda.onRedirect(screenExtra.id, url)
                openExternalUrl(url)
            }

            override fun onCustomEventSent(json: String) {
                Timber.d("onCustomEventSent $json")
                val jsonObject = JSONObject(json)
                val params = runCatching {
                    jsonObject.getJSONObject("params")
                }.getOrNull()
                val paramsMap = mutableMapOf<String, String>()
                params?.keys()?.forEach { key ->
                    params.getStringOrNull(key)?.let { value ->
                        paramsMap[key] = value
                    }
                }
                Panda.onCustomEvent(
                        screenId = screenExtra.id,
                        name = jsonObject.getString("name"),
                        params = paramsMap,
                )
            }

            override fun loadPricing(request: String) {
                this@SubscriptionFragment.loadPricing(request)
            }

            override fun onAction(json: String) {
                Timber.d("onAction $json")
                val jsonObject = JSONObject(json)
                Panda.onAction(
                        name = jsonObject.getString("name"),
                        json = json,
                )
            }

            override fun onTerms() {
                Timber.d("onTerms")
                Panda.onTermsClick()
                openExternalUrl(getString(R.string.panda_terms_url))
            }

            override fun onPolicy() {
                Timber.d("onPolicy")
                Panda.onPolicyClick()
                openExternalUrl(getString(R.string.panda_policy_url))
            }

            override fun onDismiss() {
                Timber.d("onDismiss")
                Panda.onDismiss(screenExtra)
            }

            override fun onRestore() {
                Timber.d("onRestore")
                restore()
            }
        }

        binding.webView.addJavascriptInterface(
                JavaScriptInterface(jsBridge),
                "AndroidFunction",
        )

        binding.webView.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Timber.d("onPageFinished $url")
                _binding?.webView?.let { webView ->
                    webView.evaluateJavascript("setPayload($screenPayload);") {
                        Timber.d("setPayload result $it")
                    }
                }
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest): Boolean {
                Timber.d("shouldOverrideUrlLoading1 ${request.url}")
                return handleRedirect(request.url.toString())
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
                Timber.d("shouldOverrideUrlLoading2 $url")
                return handleRedirect(url)
            }

        }
        disposeOnDestroyView.addAll(
                billing.observeSuccess()
                        .observeOn(Schedulers.mainThread())
                        .flatMapSingle {
                            it.purchases.firstOrNull()
                            Timber.d("observeSuccess $it")
                            binding.loading.root.visibility = View.VISIBLE
                            val purchase = it.purchases.first()
                            val sku = purchase.products.first()
                            Panda.onPurchase(screenExtra, purchase, getType(sku))
                                    .doAfterTerminate {
                                        binding.loading.root.visibility = View.GONE
                                    }
                        }.subscribe({
                            Timber.d("onPurchase success=$it")
                        }, {
                            Panda.onError(it)
                            Timber.e(it)
                        }),
                billing.observeErrors()
                        .subscribe({
                            if (it is PurchasesUpdate.Failed) {
                                val throwable = RuntimeException("Billing update error: $it")
                                Timber.e(throwable)
                                Panda.onError(throwable)
                            }
                        }, {
                            Timber.e(it)
                            Panda.onError(it)
                        })
        )
        disposeOnDestroyView.add(
                subscriptionsRepository.getCachedOrDefaultScreen(screenExtra.id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.mainThread())
                        .doOnSuccess { screen ->
                            binding.webView.loadDataWithBaseURL("file:///android_asset/", screen.screenHtml, null, null, null)
                        }
                        .subscribeWith(DefaultSingleObserver()))
        Panda.screenShowed(screenExtra)
    }

    override fun onDestroyView() {
        _binding = null
        Panda.removePurchaseListener(onPurchaseListener)
        disposeOnDestroyView.clear()
        super.onDestroyView()
    }

    private fun loadPricing(requestString: String) {
        val gson = Gson()
        val requests: Map<String, List<String>> = gson.fromJson<List<ProductPricingRequest>>(
                requestString,
                object : TypeToken<List<ProductPricingRequest>>() {}.type,
        ).groupBy { it.type }
                .map { entry ->
                    entry.key to entry.value.map { it.id }
                }.toMap()

        lifecycleScope.launch {
            runCatching {
                Panda.getProductsDetails(requests)
            }.onSuccess {
                val json = gson.toJson(it.toModels())
                viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                    binding.webView.evaluateJavascript("pricingLoaded($json);") {

                    }
                }
            }.onFailure {
                Timber.e(it)
            }
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
                val id = url.toUri().getQueryParameter("product_id")
                        ?: error("product_id should be provided")

                purchaseClick(id)
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
        binding.loading.root.visibility = View.VISIBLE
        disposeOnDestroyView.add(
                Panda.restore(screenExtra)
                        .doOnSuccess {
                            Timber.d("restore $it")
                        }
                        .doAfterTerminate {
                            binding.loading.root.visibility = View.GONE
                        }
                        .subscribeWith(DefaultSingleObserver())
        )
    }

    private fun getType(id: String): String {
        val subscriptions = resources.getStringArray(R.array.panda_subscriptions)
        val products = resources.getStringArray(R.array.panda_products)
        return when {
            products.contains(id) -> {
                BillingClient.ProductType.INAPP
            }
            else -> BillingClient.ProductType.SUBS
        }
    }

    private fun purchaseClick(id: String) {
        Panda.subscriptionSelect(screenExtra, id)
        Timber.d("purchase click $id")
        val type = getType(id)
        billing.getSkuDetails(
                SkuDetailsParams.newBuilder()
                        .setType(type)
                        .setSkusList(listOf(id))
                        .build()
        ).observeOn(Schedulers.mainThread())
                .flatMapCompletable {
                    billing.launchFlow(
                            requireActivity(),
                            BillingFlowParams.newBuilder()
                                    .setSkuDetails(it.first())
                                    .build()
                    )
                }
                .doOnError {
                    Panda.onError(it)
                    Timber.e(it)
                }
                .subscribe(DefaultCompletableObserver())
    }

    private fun openExternalUrl(url: String) {
        Panda.onOpenExternal(screenExtra.id, url)
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
) : Parcelable {
    companion object {
        fun create(screen: SubscriptionScreen) =
                ScreenExtra(
                        id = screen.id,
                        name = screen.name
                )
    }
}

fun SubscriptionFragment.addPayload(json: JSONObject) {
    val args = this.arguments ?: Bundle()
    args.putString(SubscriptionFragment.EXTRA_PAYLOAD, json.toString())
}

fun RxBilling.observeSuccess() =
        this.observeUpdates()
                .filter { it is PurchasesUpdate.Success }
                .map { it as PurchasesUpdate.Success }

fun RxBilling.observeErrors() =
        this.observeUpdates()
                .filter { it !is PurchasesUpdate.Success }
