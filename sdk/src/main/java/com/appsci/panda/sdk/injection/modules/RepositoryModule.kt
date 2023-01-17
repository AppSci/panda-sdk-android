package com.appsci.panda.sdk.injection.modules

import com.appsci.panda.sdk.data.device.DeviceMapper
import com.appsci.panda.sdk.data.device.DeviceMapperImpl
import com.appsci.panda.sdk.data.device.DeviceRepositoryImpl
import com.appsci.panda.sdk.data.device.utils.AuthDataValidator
import com.appsci.panda.sdk.data.device.utils.AuthDataValidatorImpl
import com.appsci.panda.sdk.data.device.utils.AuthorizationDataBuilder
import com.appsci.panda.sdk.data.device.utils.AuthorizationDataBuilderImpl
import com.appsci.panda.sdk.data.feedback.FeedbackRepositoryImpl
import com.appsci.panda.sdk.domain.device.DeviceRepository
import com.appsci.panda.sdk.domain.feedback.FeedbackRepository
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun provideDeviceRepository(impl: DeviceRepositoryImpl): DeviceRepository

    @Singleton
    @Binds
    abstract fun provideFeedbackRepository(impl: FeedbackRepositoryImpl): FeedbackRepository

    @Binds
    abstract fun provideAuthDataBuilder(impl: AuthorizationDataBuilderImpl): AuthorizationDataBuilder

    @Binds
    abstract fun provideAuthDataValidator(impl: AuthDataValidatorImpl): AuthDataValidator

    @Binds
    abstract fun provideDeviceMapper(impl: DeviceMapperImpl): DeviceMapper

}
