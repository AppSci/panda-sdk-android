package com.appsci.panda.sdk.injection.modules

import android.content.Context
import androidx.room.Room
import com.appsci.panda.sdk.data.db.PandaDatabase
import com.appsci.panda.sdk.data.db.migrations.Migration1To2
import com.appsci.panda.sdk.data.db.migrations.Migration2To3
import com.appsci.panda.sdk.data.db.migrations.Migration3To4
import com.appsci.panda.sdk.data.db.migrations.Migration4to5
import com.appsci.panda.sdk.data.device.DeviceDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule() {

    @Singleton
    @Provides
    fun provideDataBase(context: Context): PandaDatabase {
        return Room.databaseBuilder(context, PandaDatabase::class.java, "panda-sdk.db")
                .addMigrations(
                        Migration1To2(),
                        Migration2To3(),
                        Migration3To4(),
                        Migration4to5(),
                )
                .fallbackToDestructiveMigration()
                .build()
    }

    @Provides
    fun provideDeviceDao(appDatabase: PandaDatabase): DeviceDao =
            appDatabase.getDeviceDao()
}
