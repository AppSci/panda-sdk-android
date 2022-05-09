package com.appsci.panda.sdk.domain.utils

import org.json.JSONException
import org.json.JSONObject

fun JSONObject.getStringOrNull(name: String): String? = try {
    getString(name)
} catch (e: JSONException) {
    null
}
