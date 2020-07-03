/*
 * Copyright (c) Mobiquityinc, 2019.
 * All rights reserved.
 */
package com.archit.androidframework.util

import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.modules.junit4.PowerMockRunner
import java.lang.reflect.Field
import java.lang.reflect.Modifier


/**
 * Created on 2019-05-16.
 */

@RunWith(PowerMockRunner::class)
abstract class MockitoKotlinTestHelper {
    fun <T> capture(capture: ArgumentCaptor<T>) = capture.capture() ?: null as T
    fun <T> any() = Mockito.any<T>() ?: null as T
    fun anyString() = Mockito.anyString() ?: null as String
    fun <T> eq(capture: T) = Mockito.eq(capture) ?: null as T
    inline fun <reified T : Any> mock() = Mockito.mock(T::class.java)

    @Before
    fun initMockitoTestHelper(){
//        initRxJava() /*** for RxJava ***/
    }

    // mock Kotlin Singleton class
    fun <T> Class<T>.mockKotlinSingletonClass(mockedInstance: T) {

        if (!this.declaredFields.any {
                    it.name == "INSTANCE" && it.type == this && Modifier.isStatic(it.modifiers)
                }) {
            throw InstantiationException("clazz ${this.canonicalName} does not have a static  " +
                    "INSTANCE field, is it really a Kotlin \"object\"?")
        }

        val instanceField = this.getDeclaredField("INSTANCE")
        val modifiersField = Field::class.java.getDeclaredField("modifiers")
        modifiersField.isAccessible = true
        modifiersField.setInt(instanceField, instanceField.modifiers and Modifier.FINAL.inv())

        instanceField.isAccessible = true
        val originalInstance = instanceField.get(null) as T
        instanceField.set(null, mockedInstance)
    }

    fun Class<*>.mockCompanionObject(mockedCompanionObject: Any){
        PowerMockito.mockStatic(this)
        PowerMockito.field(this, "Companion").set(mockedCompanionObject,
            mockedCompanionObject)
    }

    fun Any.replaceThisRefOfObject(reference: Any) {
        val instanceField = this.javaClass.getDeclaredField("this$0")
        val modifiersField = Field::class.java.getDeclaredField("modifiers")
        modifiersField.isAccessible = true
        modifiersField.setInt(instanceField, instanceField.modifiers and Modifier.FINAL.inv())
        instanceField.isAccessible = true
        instanceField.set(this, reference)
    }

    /*** for RxJava ***/
    /*fun initRxJava() {
        RxJavaPlugins.setInitIoSchedulerHandler { immediate }
        RxJavaPlugins.setInitComputationSchedulerHandler { immediate }
        RxJavaPlugins.setInitNewThreadSchedulerHandler { immediate }
        RxJavaPlugins.setInitSingleSchedulerHandler { immediate }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { immediate }
    }

    val immediate = object : Scheduler() {
        override fun scheduleDirect(run: Runnable,
                                    delay: Long, unit: TimeUnit): Disposable {
            return super.scheduleDirect(run, 0, unit)
        }

        override fun createWorker(): Scheduler.Worker {
            return ExecutorScheduler.ExecutorWorker(
                    Executor { it.run() }, true)
        }
    }*/
}