package com.appsci.panda.sdk.data.device

import com.appsci.panda.sdk.data.device.utils.AuthorizationData
import com.appsci.panda.sdk.domain.device.Device
import javax.inject.Inject

interface DeviceMapper {

    fun mapToLocal(
            deviceResponse: DeviceResponse,
            authData: AuthorizationData
    ): DeviceEntity

    fun mapToDomain(deviceEntity: DeviceEntity): Device
    fun mapToRequest(
            authData: AuthorizationData
    ): DeviceRequest
}

class DeviceMapperImpl @Inject constructor() : DeviceMapper {

    override fun mapToLocal(deviceResponse: DeviceResponse,
                            authData: AuthorizationData
    ): DeviceEntity {

        return DeviceEntity(
                id = deviceResponse.id,
                idfa = authData.idfa,
                idfv = authData.idfv,
                locale = authData.locale,
                startAppVersion = authData.startAppVersion,
                deviceModel = authData.deviceModel,
                deviceFamily = authData.deviceFamily,
                language = authData.language,
                country = authData.country,
                osVersion = authData.osVersion,
                appVersion = authData.appVersion,
                pushToken = authData.pushToken,
                timeZone = authData.timeZone,
                customUserId = authData.customUserId,
                appsflyerId = authData.appsflyerId,
                fbc = authData.fbc,
                fbp = authData.fbp,
                facebookLoginId = authData.facebookLoginId,
                email = authData.email,
                fullName = authData.fullName,
                firstName = authData.firstName,
                lastName = authData.lastName,
                gender = authData.gender,
                phone = authData.phone,
                properties = authData.properties,
        )
    }

    override fun mapToDomain(deviceEntity: DeviceEntity): Device {
        return with(deviceEntity) {
            Device(id = id)
        }
    }

    override fun mapToRequest(
            authData: AuthorizationData
    ): DeviceRequest {
        return DeviceRequest(
                idfa = authData.idfa,
                idfv = authData.idfv,
                locale = authData.locale,
                startAppVersion = authData.startAppVersion,
                deviceModel = authData.deviceModel,
                deviceFamily = authData.deviceFamily,
                language = authData.language,
                country = authData.country,
                osVersion = authData.osVersion,
                appVersion = authData.appVersion,
                platform = authData.platform,
                pushToken = authData.pushToken,
                timeZone = authData.timeZone,
                customUserId = authData.customUserId,
                appsflyerId = authData.appsflyerId,
                fbc = authData.fbc,
                fbp = authData.fbp,
                email = authData.email,
                facebookLoginId = authData.facebookLoginId,
                fullName = authData.fullName,
                firstName = authData.firstName,
                lastName = authData.lastName,
                gender = authData.gender,
                phone = authData.phone,
                properties = authData.properties,
        )
    }
}
