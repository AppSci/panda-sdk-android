package com.appsci.panda.sdk.data.device

import com.appsci.panda.sdk.data.device.utils.AuthorizationData
import com.appsci.panda.sdk.domain.device.Device
import javax.inject.Inject

interface DeviceMapper {

    fun mapToLocal(
        deviceResponse: DeviceResponse,
        request: DeviceRequest,
    ): DeviceEntity

    fun mapToDomain(deviceEntity: DeviceEntity): Device

    /**
     * panda can't register existing user (after reinstall) with properties,
     * so make request body without properties field
     */
    fun mapRegisterRequest(
        authData: AuthorizationData,
    ): DeviceRequest

    fun mapUpdateRequest(
        authData: AuthorizationData,
    ): DeviceRequest
}

class DeviceMapperImpl @Inject constructor() : DeviceMapper {

    override fun mapToLocal(
        deviceResponse: DeviceResponse,
        request: DeviceRequest,
    ): DeviceEntity {
        return DeviceEntity(
            id = deviceResponse.id,
            idfa = request.idfa,
            idfv = request.idfv,
            locale = request.locale,
            startAppVersion = request.startAppVersion,
            deviceModel = request.deviceModel,
            deviceFamily = request.deviceFamily,
            language = request.language,
            country = request.country,
            osVersion = request.osVersion,
            appVersion = request.appVersion,
            pushToken = request.pushToken,
            timeZone = request.timeZone,
            customUserId = request.customUserId,
            appsflyerId = request.appsflyerId,
            fbc = request.fbc,
            fbp = request.fbp,
            facebookLoginId = request.facebookLoginId,
            email = request.email,
            fullName = request.fullName,
            firstName = request.firstName,
            lastName = request.lastName,
            gender = request.gender,
            phone = request.phone,
            properties = request.properties,
        )
    }

    override fun mapToDomain(deviceEntity: DeviceEntity): Device {
        return with(deviceEntity) {
            Device(id = id)
        }
    }

    override fun mapRegisterRequest(authData: AuthorizationData): DeviceRequest =
        mapDeviceRequest(authData = authData, isUpdate = false)

    override fun mapUpdateRequest(
        authData: AuthorizationData,
    ): DeviceRequest = mapDeviceRequest(authData = authData, isUpdate = true)

    private fun mapDeviceRequest(
        authData: AuthorizationData,
        isUpdate: Boolean,
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
            properties = if (isUpdate) authData.properties else null,
        )
    }
}
