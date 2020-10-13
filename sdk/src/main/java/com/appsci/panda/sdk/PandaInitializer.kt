package com.appsci.panda.sdk

import android.content.Context
import androidx.startup.Initializer

class PandaInitializer : Initializer<Boolean> {

    override fun create(context: Context): Boolean {
        return true
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        // No dependencies on other libraries.
        return emptyList()
    }
}
