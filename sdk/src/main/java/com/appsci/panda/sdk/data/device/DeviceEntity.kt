package com.appsci.panda.sdk.data.device

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = DeviceEntity.TABLE_NAME)
data class DeviceEntity(
        @PrimaryKey
        @ColumnInfo(name = "id")
        val id: String,
        @ColumnInfo(name = "startAppVersion")
        val startAppVersion: String,
        @ColumnInfo(name = "idfa")
        val idfa: String? = null,
        @ColumnInfo(name = "appVersion")
        val appVersion: String,
        @ColumnInfo(name = "locale")
        val locale: String,
        @ColumnInfo(name = "language")
        val language: String,
        @ColumnInfo(name = "idfv")
        val idfv: String? = null,
        @ColumnInfo(name = "deviceFamily")
        val deviceFamily: String,
        @ColumnInfo(name = "osVersion")
        val osVersion: String,
        @ColumnInfo(name = "timeZone")
        val timeZone: String,
        @ColumnInfo(name = "country")
        val country: String,
        @ColumnInfo(name = "pushToken")
        val pushToken: String? = null,
        @ColumnInfo(name = "deviceModel")
        val deviceModel: String,
        @ColumnInfo(name = "customUserId")
        val customUserId: String?,
        @ColumnInfo(name = "appsflyerId")
        val appsflyerId: String?,
        @ColumnInfo(name = "fbc")
        val fbc: String?,
        @ColumnInfo(name = "fbp")
        val fbp: String?,
        @ColumnInfo(name = "email")
        val email: String?,
        @ColumnInfo(name = "facebook_login_id")
        val facebookLoginId: String?,
        @ColumnInfo(name = "first_name")
        val firstName: String?,
        @ColumnInfo(name = "last_name")
        val lastName: String?,
        @ColumnInfo(name = "full_name")
        val fullName: String?,
        @ColumnInfo(name = "gender")
        val gender: Int?,
        @ColumnInfo(name = "phone")
        val phone: String?,
        @ColumnInfo(name = "properties")
        val properties: Map<String, String>?,
) {
    companion object {
        const val TABLE_NAME = "Device"
    }
}
