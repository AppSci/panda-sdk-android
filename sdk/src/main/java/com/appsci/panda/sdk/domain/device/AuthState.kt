package com.appsci.panda.sdk.domain.device

sealed class AuthState {
    data class Authorized(val device: Device) : AuthState()
    object NotAuthorized : AuthState()
}
