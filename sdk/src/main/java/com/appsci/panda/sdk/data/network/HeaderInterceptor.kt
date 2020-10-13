package com.appsci.panda.sdk.data.network

import com.appsci.panda.sdk.domain.utils.DeviceManager
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class HeaderInterceptor(
        private val deviceManager: DeviceManager,
        private val apiKey: String
) : Interceptor {

    companion object {
        private const val AUTHORIZATION = "Authorization"
        private const val API_BEARER_AUTHORIZATION = "Bearer %s"
        private const val USER_AGENT = "User-agent"
        private const val USER_AGENT_FORMAT = "%s/%s Android/%s"
        private const val ACCEPT_LANGUAGE = "Accept-Language"
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val url = original.url
        var builder: Request.Builder = original.newBuilder()
        builder.url(url)

        builder = builder.build().newBuilder()
        builder.addHeader(USER_AGENT,
                String.format(
                        USER_AGENT_FORMAT,
                        deviceManager.getAppName(),
                        deviceManager.getAppVersionName(),
                        deviceManager.getOsVersionName()
                )
        ).addHeader(ACCEPT_LANGUAGE, deviceManager.getLocale().language)
        builder.addHeader(AUTHORIZATION, apiKey)
        return chain.proceed(builder.build())
    }
}
