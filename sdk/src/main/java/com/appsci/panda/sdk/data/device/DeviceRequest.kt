package com.appsci.panda.sdk.data.device

import com.google.gson.annotations.SerializedName

data class DeviceRequest(
        @SerializedName("country")
        val country: String,
        @SerializedName("device_model")
        val deviceModel: String,
        @SerializedName("app_version")
        val appVersion: String,
        @SerializedName("start_app_version")
        val startAppVersion: String,
        @SerializedName("timezone")
        val timeZone: String,
        @SerializedName("os_version")
        val osVersion: String,
        @SerializedName("idfa")
        val idfa: String? = null,
        @SerializedName("device_family")
        val deviceFamily: String,
        @SerializedName("language")
        val language: String,
        @SerializedName("locale")
        val locale: String,
        @SerializedName("platform")
        val platform: String,
        @SerializedName("push_notifications_token")
        val pushToken: String? = null,
        @SerializedName("custom_user_id")
        val customUserId: String? = null,
        @SerializedName("appsflyer_id")
        val appsflyerId: String? = null,
        @SerializedName("time_zone")
        val idfv: String? = null,
        @SerializedName("fbc")
        val fbc: String? = null,
        @SerializedName("fbp")
        val fbp: String? = null,
        @SerializedName("email")
        val email: String? = null,
        @SerializedName("facebook_login_id")
        val facebookLoginId: String? = null,
        @SerializedName("first_name")
        val firstName: String? = null,
        @SerializedName("last_name")
        val lastName: String? = null,
        @SerializedName("full_name")
        val fullName: String? = null,
        @SerializedName("gender")
        val gender: Int? = null,
        @SerializedName("phone")
        val phone: String? = null,
        @SerializedName("properties")
        val properties: Map<String, String>,
)
