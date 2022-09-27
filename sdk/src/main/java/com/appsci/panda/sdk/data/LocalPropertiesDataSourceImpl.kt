package com.appsci.panda.sdk.data

import android.content.SharedPreferences
import androidx.core.content.edit
import com.appsci.panda.sdk.domain.utils.LocalPropertiesDataSource

class LocalPropertiesDataSourceImpl(
        private val preferences: SharedPreferences,
) : LocalPropertiesDataSource {

    override fun putProperty(key: String, value: String) {
        preferences.edit { putString(key, value) }
    }

    override fun getAll(): Map<String, String> {
        return preferences.all.map {
            it.key to it.value.toString()
        }.toMap()
    }

    override fun clear() {
        preferences.edit { this.clear() }
    }
}
