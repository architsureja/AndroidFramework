/*
 * Copyright (c) Mobiquityinc, 2020.
 * All rights reserved.
 */
package com.mobiquity.backbaseframework.util

import android.view.View
import com.google.gson.Gson
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import java.lang.reflect.Type

/**
 * Created on 6/24/20.
 */

internal fun View.show() {
    visibility = View.VISIBLE
}

internal fun View.hide() {
    visibility = View.GONE
}

internal fun View.invisible() {
    visibility = View.INVISIBLE
}

internal fun <T: View> T.setOnClickListener() = callbackFlow {
    this@setOnClickListener.setOnClickListener {
        offer(it as T)
    }
    awaitClose { this@setOnClickListener.setOnClickListener(null) }
}

internal fun <T> Gson.fromJsonOrNull(json: String?, classOfT: Class<T>): T? {
    return try {
        if (json == "{}") return null
        fromJson(json, classOfT)
    } catch (e: Exception) {
        null
    }
}

internal fun <T> Gson.fromJsonOrNull(json: String?, typeOfT: Type): T? {
    return try {
        if (json == "{}") return null
        fromJson(json, typeOfT)
    } catch (e: Exception) {
        null
    }
}