package com.appsci.panda.sdk.ui

import android.webkit.JavascriptInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface JavaScriptBridgeInterface {
    fun onPurchase(json: String)
    fun onRedirect(json: String)
    fun onTerms()
    fun onPolicy()
    fun onDismiss()
    fun onRestore()
    fun onCustomEventSent(json: String)
}

class JavaScriptInterface(
        private val jsBridge: JavaScriptBridgeInterface,
) {
    private val scope = CoroutineScope(Dispatchers.Main)

    @JavascriptInterface
    fun onPurchase(json: String) {
        scope.launch(Dispatchers.Main) {
            jsBridge.onPurchase(json)
        }
    }

    @JavascriptInterface
    fun onRedirect(json: String) {
        scope.launch(Dispatchers.Main) {
            jsBridge.onRedirect(json)
        }
    }

    @JavascriptInterface
    fun onTerms() {
        scope.launch(Dispatchers.Main) {
            jsBridge.onTerms()
        }
    }

    @JavascriptInterface
    fun onPolicy() {
        scope.launch(Dispatchers.Main) {
            jsBridge.onPolicy()
        }
    }

    @JavascriptInterface
    fun onDismiss() {
        scope.launch(Dispatchers.Main) {
            jsBridge.onDismiss()
        }
    }

    @JavascriptInterface
    fun onRestore() {
        scope.launch(Dispatchers.Main) {
            jsBridge.onRestore()
        }
    }

    @JavascriptInterface
    fun onCustomEventSent(json: String) {
        scope.launch(Dispatchers.Main) {
            jsBridge.onCustomEventSent(json)
        }
    }

}
