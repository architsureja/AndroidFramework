package com.archit.androidframework.util

import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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

internal fun Int.convertDpToPixel(context: Context): Int {
    val a: Number = 8
    val resources = context.resources
    val metrics = resources.displayMetrics
    return (this@convertDpToPixel * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
}

internal fun View.updateWidth(width: Int) {
    val layoutParams = this.layoutParams
    layoutParams.width = width
    this.layoutParams = layoutParams
}

internal fun View.updateHeight(height: Int) {
    val layoutParams = this.layoutParams
    layoutParams.height = height
    this.layoutParams = layoutParams
}

internal fun View.setOnClickListener() = callbackFlow {
    this@setOnClickListener.setOnClickListener {
        offer(it as View)
    }
    awaitClose { this@setOnClickListener.setOnClickListener(null) }
}

internal fun View.setDebounceOnClickListener(
    debounceTimeInMilli: Long = 300,
    action: (View) -> Unit
) = setOnClickListener().debounce(debounceTimeInMilli).onEach {
    action(it)
}.launchIn(CoroutineScope(Dispatchers.Main))

internal fun setOnClickListener(views: List<View>) =
    callbackFlow {
        val produceListener: (View) -> Unit = { offer(it) }
        views.forEach { view -> view.setOnClickListener(produceListener) }

        awaitClose {
            views.forEach { view -> view.setOnClickListener(null) }
        }

    }

internal fun setDebounceOnClickListener(
    views: List<View>,
    debounceTimeInMilli: Long = 300,
    action: (View) -> Unit
) = setOnClickListener(views).debounce(debounceTimeInMilli).onEach {
    action(it)
}.launchIn(CoroutineScope(Dispatchers.Main))

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