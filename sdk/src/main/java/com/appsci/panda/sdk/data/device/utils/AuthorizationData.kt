package com.appsci.panda.sdk.data.device.utils

data class AuthorizationData(
        val startAppVersion: String,
        val idfa: String? = null,
        val appVersion: String,
        val locale: String,
        val language: String,
        val idfv: String? = null,
        val deviceFamily: String,
        val osVersion: String,
        val timeZone: String,
        val country: String,
        val pushToken: String? = null,
        val deviceModel: String,
        val platform: String = "Android",
        val customUserId: String? = null,
        val appsflyerId: String? = null
)
