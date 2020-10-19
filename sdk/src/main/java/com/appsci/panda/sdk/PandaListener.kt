package com.appsci.panda.sdk

interface PandaListener {
    fun onError(t: Throwable)
    fun onDismiss()
    fun onPurchase(id: String)
    fun onRestore(ids: List<String>)
    fun onTermsClick()
    fun onPolicyClick()
}

open class SimplePandaListener : PandaListener {

    override fun onError(t: Throwable) {
    }

    override fun onDismiss() {
    }

    override fun onPurchase(id: String) {
    }

    override fun onRestore(ids: List<String>) {
    }

    override fun onTermsClick() {
    }

    override fun onPolicyClick() {
    }
}
