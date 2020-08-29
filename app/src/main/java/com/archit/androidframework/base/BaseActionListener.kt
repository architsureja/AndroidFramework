package com.archit.androidframework.base

/**
 * Created on 7/16/20.
 */

interface BaseActionListener<T> {
    fun performAction(action: T, payload: Any? = null)
}