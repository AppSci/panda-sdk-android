package com.appsci.panda.sdk.ui

import android.webkit.JavascriptInterface

interface JavaScriptBridgeInterface {
    fun onPurchase(json: String)
    fun onRedirect(json: String)
}

class JavaScriptInterface(private val jsBridge: JavaScriptBridgeInterface) {

    @JavascriptInterface
    fun onPurchase(json: String) {
        jsBridge.onPurchase(json)
    }

    @JavascriptInterface
    fun onRedirect(json: String) {
        jsBridge.onRedirect(json)
    }
}
