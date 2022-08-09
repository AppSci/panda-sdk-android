package com.appsci.panda.sdk.domain.utils

interface PropertiesDataSource {

    fun putProperty(key: String, value: String)

    fun getAll(): Map<String, String>

    fun clear()
}
