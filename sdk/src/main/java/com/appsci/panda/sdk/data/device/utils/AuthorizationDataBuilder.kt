package com.appsci.panda.sdk.data.device.utils

import com.appsci.panda.sdk.domain.utils.DeviceManager
import com.appsci.panda.sdk.domain.utils.Preferences
import com.appsci.panda.sdk.domain.utils.PropertiesDataSource
import javax.inject.Inject

interface AuthorizationDataBuilder {
    fun createAuthData(): AuthorizationData
}

class AuthorizationDataBuilderImpl @Inject constructor(
        private val device: DeviceManager,
        private val preferences: Preferences,
        private val propertiesDataSource: PropertiesDataSource,
) : AuthorizationDataBuilder {

    override fun createAuthData(): AuthorizationData {
        return AuthorizationData(
                pushToken = device.getFcmToken(),
                timeZone = device.getTimeZoneId(),
                idfa = device.getAdvertisingId(),
                idfv = null,
                appVersion = device.getAppVersionName(),
                osVersion = device.getOsVersionName(),
                country = device.getLocale().country,
                language = device.getLocale().language,
                deviceFamily = device.getDeviceFamily(),
                deviceModel = device.getDeviceModel(),
                locale = device.getLocale().toString(),
                startAppVersion = device.startVersion,
                customUserId = preferences.customUserId,
                appsflyerId = preferences.appsflyerId,
                fbc = preferences.fbc,
                fbp = preferences.fbp,
                email = preferences.email,
                facebookLoginId = preferences.facebookLoginId,
                firstName = preferences.firstName,
                lastName = preferences.lastName,
                fullName = preferences.fullName,
                gender = preferences.gender,
                phone = preferences.phone,
                properties = propertiesDataSource.getAll(),
        )
    }
}
