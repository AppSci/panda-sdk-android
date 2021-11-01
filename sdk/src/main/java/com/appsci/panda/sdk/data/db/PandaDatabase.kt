package com.appsci.panda.sdk.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.appsci.panda.sdk.data.device.DeviceDao
import com.appsci.panda.sdk.data.device.DeviceEntity
import com.appsci.panda.sdk.data.subscriptions.local.PurchaseDao
import com.appsci.panda.sdk.data.subscriptions.local.PurchaseEntity

@TypeConverters(Converters::class)
@Database(
        entities = [
            (DeviceEntity::class),
            (PurchaseEntity::class)
        ],
        version = 4
)

abstract class PandaDatabase : RoomDatabase() {

    abstract fun getDeviceDao(): DeviceDao

    abstract fun getPurchaseDao(): PurchaseDao

}
