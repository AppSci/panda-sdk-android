package com.appsci.panda.sdk.injection.components

import com.appsci.panda.sdk.PandaDependencies
import com.appsci.panda.sdk.injection.modules.*
import com.appsci.panda.sdk.ui.SubscriptionFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AppModule::class,
    RepositoryModule::class,
    DatabaseModule::class,
    NetworkModule::class,
    BillingModule::class
])

interface PandaComponent {

    fun inject(app: PandaDependencies)
    fun inject(fragment: SubscriptionFragment)

}
