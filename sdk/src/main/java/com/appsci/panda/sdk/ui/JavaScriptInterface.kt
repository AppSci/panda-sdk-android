package com.appsci.panda.sdk.ui

import android.webkit.JavascriptInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface JavaScriptBridgeInterface {
    fun onPurchase(json: String)
    fun onRedirect(json: String)
    fun onScreenChanged(json: String)
    fun onTerms()
    fun onPolicy()
    fun onDismiss()
    fun onRestore()
    fun onCustomEventSent(json: String)
    fun onAction(json: String)
    fun loadPricing(request: String)
}

class JavaScriptInterface(
        private val jsBridge: JavaScriptBridgeInterface,
) {
    private val scope = CoroutineScope(Dispatchers.Main)

    @JavascriptInterface
    fun onPurchase(json: String) {
        scope.launch {
            jsBridge.onPurchase(json)
        }
    }

    @JavascriptInterface
    fun onRedirect(json: String) {
        scope.launch {
            jsBridge.onRedirect(json)
        }
    }

    @JavascriptInterface
    fun onTerms() {
        scope.launch {
            jsBridge.onTerms()
        }
    }

    @JavascriptInterface
    fun onPolicy() {
        scope.launch {
            jsBridge.onPolicy()
        }
    }

    @JavascriptInterface
    fun onDismiss() {
        scope.launch {
            jsBridge.onDismiss()
        }
    }

    @JavascriptInterface
    fun onRestore() {
        scope.launch {
            jsBridge.onRestore()
        }
    }

    @JavascriptInterface
    fun onScreenChanged(json: String) {
        scope.launch {
            jsBridge.onScreenChanged(json)
        }
    }

    @JavascriptInterface
    fun onCustomEventSent(json: String) {
        scope.launch {
            jsBridge.onCustomEventSent(json)
        }
    }

    @JavascriptInterface
    fun onAction(json: String) {
        scope.launch {
            jsBridge.onAction(json)
        }
    }

}
