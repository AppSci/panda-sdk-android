package com.appsci.panda.sdk.data

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.Keep
import androidx.core.content.edit
import com.appsci.panda.sdk.domain.utils.Preferences

@Keep
class PreferencesImpl(context: Context) : Preferences {

    companion object {
        private const val KEY_START_VERSION: String = "startVersion"
        private const val KEY_CUSTOM_USER_ID: String = "customUserId"
        private const val KEY_PANDA_USER_ID: String = "pandaUserId"
        private const val KEY_APPSFLYER_ID: String = "appsflyerId"
    }

    private val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("PandaPreferences", Context.MODE_PRIVATE)

    override var startVersion: String?
        get() = sharedPreferences.getString(KEY_START_VERSION, null)
        set(value) = sharedPreferences.edit { putString(KEY_START_VERSION, value) }
    override var customUserId: String?
        get() = sharedPreferences.getString(KEY_CUSTOM_USER_ID, null)
        set(value) = sharedPreferences.edit { putString(KEY_CUSTOM_USER_ID, value) }
    override var appsflyerId: String?
        get() = sharedPreferences.getString(KEY_APPSFLYER_ID, null)
        set(value) = sharedPreferences.edit { putString(KEY_APPSFLYER_ID, value) }
    override var pandaUserId: String?
        get() = sharedPreferences.getString(KEY_PANDA_USER_ID, null)
        set(value) = sharedPreferences.edit { putString(KEY_PANDA_USER_ID, value) }
}
