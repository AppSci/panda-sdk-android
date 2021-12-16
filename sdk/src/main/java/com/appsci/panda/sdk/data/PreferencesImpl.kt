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
        private const val KEY_FBC: String = "fbc"
        private const val KEY_FBP: String = "fbp"
        private const val KEY_EMAIL: String = "email"
        private const val KEY_FACEBOOK_ID: String = "facebook_id"
        private const val KEY_FIRST_NAME: String = "first_name"
        private const val KEY_LAST_NAME: String = "last_name"
        private const val KEY_FULL_NAME: String = "full_name"
        private const val KEY_GENDER: String = "gender"
        private const val KEY_PHONE: String = "phone"
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
    override var fbc: String?
        get() = sharedPreferences.getString(KEY_FBC, null)
        set(value) = sharedPreferences.edit { putString(KEY_FBC, value) }
    override var fbp: String?
        get() = sharedPreferences.getString(KEY_FBP, null)
        set(value) = sharedPreferences.edit { putString(KEY_FBP, value) }
    override var email: String?
        get() = sharedPreferences.getString(KEY_EMAIL, null)
        set(value) = sharedPreferences.edit { putString(KEY_EMAIL, value) }
    override var facebookLoginId: String?
        get() = sharedPreferences.getString(KEY_FACEBOOK_ID, null)
        set(value) = sharedPreferences.edit { putString(KEY_FACEBOOK_ID, value) }
    override var firstName: String?
        get() = sharedPreferences.getString(KEY_FIRST_NAME, null)
        set(value) = sharedPreferences.edit { putString(KEY_FIRST_NAME, value) }
    override var lastName: String?
        get() = sharedPreferences.getString(KEY_LAST_NAME, null)
        set(value) = sharedPreferences.edit { putString(KEY_LAST_NAME, value) }
    override var fullName: String?
        get() = sharedPreferences.getString(KEY_FULL_NAME, null)
        set(value) = sharedPreferences.edit { putString(KEY_FULL_NAME, value) }
    override var gender: Int?
        get() {
            return if (sharedPreferences.contains(KEY_GENDER)) {
                sharedPreferences.getInt(KEY_GENDER, -1)
            } else null
        }
        set(value) {
            value?.let {
                sharedPreferences.edit { putInt(KEY_GENDER, value) }
            }
        }
    override var phone: String?
        get() = sharedPreferences.getString(KEY_PHONE, null)
        set(value) = sharedPreferences.edit { putString(KEY_PHONE, value) }

    override fun clear() {
        sharedPreferences.edit { clear() }
    }
}
