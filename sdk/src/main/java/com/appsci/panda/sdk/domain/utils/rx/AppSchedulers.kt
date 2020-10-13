package com.appsci.panda.sdk.domain.utils.rx

import io.reactivex.Scheduler

@Suppress("unused")
object AppSchedulers {
    private lateinit var instance: SchedulerProvider

    fun setInstance(instance: SchedulerProvider) {
        AppSchedulers.instance = instance
    }

    @JvmStatic
    fun io(): Scheduler = instance.io()

    @JvmStatic
    fun mainThread(): Scheduler = instance.mainThread()

    fun computation(): Scheduler = instance.computation()

    fun newThread(): Scheduler = instance.newThread()

    fun trampoline(): Scheduler = instance.trampoline()
}
