package com.archit.androidframework.util

import com.archit.androidframework.util.CoroutineHelper
import kotlinx.coroutines.test.TestCoroutineScope

class CoroutineTestHelper : CoroutineHelper() {

    val testScope = TestCoroutineScope()

    override fun uiCoroutineScope() = testScope
    override fun workerCoroutineScope() = testScope

}
