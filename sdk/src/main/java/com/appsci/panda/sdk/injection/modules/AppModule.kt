package com.appsci.panda.sdk.injection.modules

import android.content.Context
import android.content.res.Resources
import com.appsci.panda.sdk.IPanda
import com.appsci.panda.sdk.PandaImpl
import com.appsci.panda.sdk.data.DeviceManagerImpl
import com.appsci.panda.sdk.data.PreferencesImpl
import com.appsci.panda.sdk.data.LocalPropertiesDataSourceImpl
import com.appsci.panda.sdk.data.StopNetwork
import com.appsci.panda.sdk.domain.device.DeviceRepository
import com.appsci.panda.sdk.domain.subscriptions.SubscriptionsRepository
import com.appsci.panda.sdk.domain.utils.DeviceManager
import com.appsci.panda.sdk.domain.utils.Preferences
import com.appsci.panda.sdk.domain.utils.LocalPropertiesDataSource
import dagger.Module
import dagger.Provides
import org.threeten.bp.Clock
import javax.inject.Singleton

@Module
class AppModule(private val context: Context) {

    @Provides
    fun providePanda(
            deviceRepository: DeviceRepository,
            subscriptionsRepository: SubscriptionsRepository,
            preferences: Preferences,
            deviceManager: DeviceManager,
            stopNetwork: StopNetwork,
            localPropertiesDataSource: LocalPropertiesDataSource,
    ): IPanda = PandaImpl(
            deviceRepository = deviceRepository,
            subscriptionsRepository = subscriptionsRepository,
            preferences = preferences,
            deviceManager = deviceManager,
            stopNetworkInternal = stopNetwork,
            propertiesDataSource = localPropertiesDataSource,
    )

    @Provides
    fun provideAppContext(): Context = context

    @Provides
    fun provideResources(context: Context): Resources = context.resources

    @Provides
    fun provideDeviceManager(
            appContext: Context,
            preferences: Preferences,
    ): DeviceManager {
        return DeviceManagerImpl(appContext, preferences)
    }

    @Provides
    @Singleton
    fun providePreferences(context: Context): Preferences {
        return PreferencesImpl(context)
    }

    @Provides
    @Singleton
    fun providePropertiesDataSource(context: Context): LocalPropertiesDataSource {
        return LocalPropertiesDataSourceImpl(
                context.getSharedPreferences("PropertiesPreferences", Context.MODE_PRIVATE)
        )
    }

    @Provides
    @Singleton
    fun provideClock(): Clock =
            Clock.systemDefaultZone()

}
