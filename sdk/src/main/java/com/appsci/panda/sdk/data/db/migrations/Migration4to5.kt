package com.appsci.panda.sdk.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.appsci.panda.sdk.data.device.DeviceEntity

class Migration4to5 : Migration(4, 5) {

    override fun migrate(database: SupportSQLiteDatabase) {
        migrateDevice(database)
    }

    private fun migrateDevice(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE `${DeviceEntity.TABLE_NAME}` ADD COLUMN properties TEXT")
    }
}
