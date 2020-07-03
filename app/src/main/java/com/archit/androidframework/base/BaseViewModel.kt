package com.archit.androidframework.base

import androidx.lifecycle.ViewModel

/**
 * Created on 7/16/20.
 */

abstract class BaseViewModel<T> : ViewModel() {
    var actionPerformer: BaseActionListener<T>? = null
}