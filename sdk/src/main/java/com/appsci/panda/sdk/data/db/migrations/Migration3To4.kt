package com.appsci.panda.sdk.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.appsci.panda.sdk.data.device.DeviceEntity

class Migration3To4 : Migration(3, 4) {

    override fun migrate(database: SupportSQLiteDatabase) {
        migrateDevice(database)
    }

    private fun migrateDevice(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE `${DeviceEntity.TABLE_NAME}` ADD COLUMN email TEXT")
        database.execSQL("ALTER TABLE `${DeviceEntity.TABLE_NAME}` ADD COLUMN facebook_login_id TEXT")
        database.execSQL("ALTER TABLE `${DeviceEntity.TABLE_NAME}` ADD COLUMN first_name TEXT")
        database.execSQL("ALTER TABLE `${DeviceEntity.TABLE_NAME}` ADD COLUMN last_name TEXT")
        database.execSQL("ALTER TABLE `${DeviceEntity.TABLE_NAME}` ADD COLUMN full_name TEXT")
        database.execSQL("ALTER TABLE `${DeviceEntity.TABLE_NAME}` ADD COLUMN gender Integer")
        database.execSQL("ALTER TABLE `${DeviceEntity.TABLE_NAME}` ADD COLUMN phone TEXT")
    }
}
