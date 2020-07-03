package com.archit.androidframework.util

import kotlinx.coroutines.*

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