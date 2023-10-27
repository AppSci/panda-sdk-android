package com.appsci.panda.sdk

interface PandaListener {
    fun onError(t: Throwable)
    fun onDismissClick()
    fun onBackClick()
    fun onPurchase(id: String)
    fun onRestore(ids: List<String>)
}

open class SimplePandaListener : PandaListener {

    override fun onError(t: Throwable) {
    }

    override fun onDismissClick() {
    }

    override fun onBackClick() {
    }

    override fun onPurchase(id: String) {
    }

    override fun onRestore(ids: List<String>) {
    }
}
