/*
 * Copyright (c) Mobiquityinc, 2019.
 * All rights reserved.
 */
package com.mobiquity.backbaseframework.util

import android.graphics.Bitmap
import android.widget.ImageView
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

open class CoroutineHelper {

    private val PLATFORM = "platform"
    private val PLATFORM2 = "{platform}"

    open fun uiCoroutineScope() = CoroutineScope(Dispatchers.Main)
    open fun workerCoroutineScope() = CoroutineScope(Dispatchers.IO)

    fun <T : Any> ioThenMain(
        work: suspend (() -> T?),
        onSuccess: ((T?) -> Unit)? = null,
        onError: ((Exception?) -> Unit)? = null,
        onComplete: (() -> Unit)? = null
    ): Job =
        uiCoroutineScope().launch {
            try {
                val data = workerCoroutineScope().async {
                    return@async work()
                }.await()
                if (this.isActive) {
                    onSuccess?.let {
                        it(data)
                    }
                }
            } catch (e: Exception) {
                if (this.isActive) {
                    onError?.let {
                        it(e)
                    }
                }
            } finally {
                if (this.isActive) {
                    onComplete?.let {
                        it()
                    }
                }
            }
        }
}