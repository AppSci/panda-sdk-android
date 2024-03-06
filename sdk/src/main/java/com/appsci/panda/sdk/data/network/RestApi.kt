package com.appsci.panda.sdk.data.network

import com.appsci.panda.sdk.data.device.DeviceRequest
import com.appsci.panda.sdk.data.device.DeviceResponse
import com.appsci.panda.sdk.data.feedback.FeedbackRequest
import com.appsci.panda.sdk.data.subscriptions.rest.*
import com.google.gson.annotations.SerializedName
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.*

interface RestApi {

    @POST("/v1/users")
    fun registerDevice(@Body deviceRequest: DeviceRequest): Single<DeviceResponse>

    @PUT("/v1/users/{user_id}")
    fun updateDevice(
        @Body deviceRequest: DeviceRequest,
        @Path("user_id") userId: String,
    ): Single<DeviceResponse>

    @DELETE("/v1/devices")
    fun deleteDevice(): Completable

    @GET("/v1/subscription-status/{user_id}")
    fun getSubscriptionStatus(
        @Path("user_id") userId: String,
    ): Single<SubscriptionStateResponse>

    @GET("/v1/screen")
    fun getSubscriptionScreen(
        @Query("user_id") userId: String,
        @Query("type") type: String?,
        @Query("id") id: String?,
    ): Single<ScreenResponse>

    @POST("/v1/android/products/{user_id}")
    fun sendProduct(
        @Body request: ProductRequest,
        @Path("user_id") userId: String,
    ): Single<SendSubscriptionResponse>

    @POST("/v1/android/subscriptions/{user_id}")
    fun sendSubscription(
        @Body request: SubscriptionRequest,
        @Path("user_id") userId: String,
    ): Single<SendSubscriptionResponse>

    @POST("/v1/feedback/answers")
    suspend fun sendFeedback(
        @Body request: FeedbackRequest,
    )

    @POST("/v1/solid/google/pay")
    suspend fun sendGooglePayment(
        @Body request: GooglePaymentRequest,
    ): GooglePayResponse
}

/**
 * GooglePayRequest struct {
 *     Name        string `json:"name"`
 *     PurchaseUrl string `json:"purchase_url"`
 *     ProductID   string `json:"product_id" validate:"required,min=10" minLength:"10"`
 *     Signature   string `json:"signature" validate:"required,min=10" minLength:"10"`
 *     //EncryptedMessage     string `json:"encrypted_message" validate:"required,min=1" minLength:"1"`
 *     ProtocolVersion  string `json:"protocol_version" validate:"required,min=1" minLength:"1"`
 *     SignedMessage    string `json:"signed_msg" validate:"required,min=10" minLength:"10"`
 *     UserID           string `json:"user_id" validate:"required,min=10" minLength:"10"`
 *     Sandbox          bool   `json:"sandbox,omitempty"`
 *     Wallet           string `json:"wallet,omitempty"`
 *     OrderDescription string `json:"order_description,omitempty"`
 * }
 */
data class GooglePaymentRequest(
    @SerializedName("name")
    val name: String,
    @SerializedName("purchase_url")
    val purchaseUrl: String,
    @SerializedName("product_id")
    val productId: String,
    @SerializedName("signature")
    val signature: String,
    @SerializedName("protocol_version")
    val protocolVersion: String,
    @SerializedName("signed_msg")
    val signedMessage: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("sandbox")
    val sandbox: Boolean,
    @SerializedName("wallet")
    val wallet: String,
    @SerializedName("order_description")
    val orderDescription: String,
)

data class GooglePayResponse(
    @SerializedName("order_id")
    val orderId: String,
    @SerializedName("state")
    val state: String,
)
