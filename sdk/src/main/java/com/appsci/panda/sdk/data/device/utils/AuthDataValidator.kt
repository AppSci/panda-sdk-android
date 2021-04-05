package com.appsci.panda.sdk.data.device.utils

import com.appsci.panda.sdk.data.device.DeviceEntity
import javax.inject.Inject

interface AuthDataValidator {

    fun isDeviceValid(device: DeviceEntity, authData: AuthorizationData): Boolean
}

class AuthDataValidatorImpl @Inject constructor() : AuthDataValidator {

    override fun isDeviceValid(device: DeviceEntity, authData: AuthorizationData): Boolean {

        return device.customUserId == authData.customUserId
                && device.appsflyerId == authData.appsflyerId
                && device.pushToken == authData.pushToken
                && device.idfa == authData.idfa
                && device.timeZone == authData.timeZone
                && device.locale == authData.locale
                && device.appVersion == authData.appVersion
    }

}
