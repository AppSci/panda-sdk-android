package com.appsci.panda.sdk.domain.utils

import java.util.*

/**
 * Wrapper upon class which provides basic information about the device on which the application
 * is running.
 */
interface DeviceManager {

    fun getAppName(): String

    fun getAppVersionName(): String

    fun getAppVersionCode(): Int

    fun getDeviceModel(): String

    fun getDeviceFamily(): String

    fun getOsDetails(): String

    fun getOsVersionName(): String

    fun getFcmToken(): String?

    fun getHardwareId(): String

    fun hardwareUUID(): String

    fun getAdvertisingId(): String?

    fun getTimeZoneId(): String

    fun getLocale(): Locale

    fun installTime(): Long

    fun updateTime(): Long

    val startVersion : String
}
