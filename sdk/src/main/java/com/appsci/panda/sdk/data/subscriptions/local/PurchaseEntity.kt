package com.appsci.panda.sdk.data.subscriptions.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

const val TYPE_SUBSCRIPTION: Int = 1
const val TYPE_PRODUCT: Int = 2

@Entity(tableName = "Purchase")
data class PurchaseEntity(
        @PrimaryKey
        @ColumnInfo(name = "productId") val productId: String,
        @ColumnInfo(name = "orderId") val orderId: String,
        @ColumnInfo(name = "purchaseToken") val purchaseToken: String,
        @ColumnInfo(name = "purchaseType") val purchaseType: Int,
        @ColumnInfo(name = "synced") val synced: Boolean
)
