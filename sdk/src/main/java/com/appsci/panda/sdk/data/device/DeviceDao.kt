package com.appsci.panda.sdk.data.device

import androidx.room.*
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
abstract class DeviceDao {

    @Query("SELECT * FROM Device LIMIT 1")
    abstract fun selectDevice(): Maybe<DeviceEntity>

    @Query("SELECT * FROM Device LIMIT 1")
    abstract fun getDevice(): DeviceEntity?

    @Query("SELECT id FROM Device LIMIT 1")
    abstract fun requireUserId(): Single<String>

    @Query("DELETE FROM Device")
    abstract fun deleteDevice()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertDevice(deviceEntity: DeviceEntity)

    @Transaction
    open fun putDevice(deviceEntity: DeviceEntity) {
        deleteDevice()
        insertDevice(deviceEntity)
    }
}
