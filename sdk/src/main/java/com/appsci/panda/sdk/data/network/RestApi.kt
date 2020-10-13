package com.appsci.panda.sdk.data.network

import com.appsci.panda.sdk.data.device.DeviceRequest
import com.appsci.panda.sdk.data.device.DeviceResponse
import com.appsci.panda.sdk.data.subscriptions.rest.ProductRequest
import com.appsci.panda.sdk.data.subscriptions.rest.ScreenResponse
import com.appsci.panda.sdk.data.subscriptions.rest.SubscriptionRequest
import com.appsci.panda.sdk.data.subscriptions.rest.SubscriptionResponse
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.*

interface RestApi {

    companion object {
        const val PATTERN_SERVER_TIME = "yyyy-MM-dd HH:mm:ss"
    }

    @POST("/v1/users")
    fun registerDevice(@Body deviceRequest: DeviceRequest): Single<DeviceResponse>

    @PUT("/v1/users/{user_id}")
    fun updateDevice(
            @Body deviceRequest: DeviceRequest,
            @Path("user_id") userId: String
    ): Single<DeviceResponse>

    @DELETE("/v1/devices")
    fun deleteDevice(): Completable

    @POST("/v1/android/subscriptions")
    fun sendSubscription(@Body request: SubscriptionRequest): Completable

    @GET("/v1/subscription-status/{user_id}")
    fun getSubscriptionStatus(
            @Path("user_id") userId: String
    ): Single<SubscriptionResponse>

    @GET("/v1/screen")
    fun getSubscriptionScreen(
            @Query("user_id") userId: String,
            @Query("type") type: String?,
            @Query("id") id: String?,
    ): Single<ScreenResponse>

    @POST("/v1/android/products")
    fun sendProduct(@Body request: ProductRequest): Completable

}
