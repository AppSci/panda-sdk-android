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
                && device.fbc == authData.fbc
                && device.fbp == authData.fbp
                && device.facebookLoginId == authData.facebookLoginId
                && device.email == authData.email
                && device.fullName == authData.fullName
                && device.firstName == authData.firstName
                && device.lastName == authData.lastName
                && device.gender == authData.gender
                && device.phone == authData.phone
    }

}
