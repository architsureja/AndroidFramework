package com.archit.androidframework.base

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import java.lang.reflect.ParameterizedType

/**
 * Created on 7/16/20.
 */

abstract class BaseFragment<T, Q : BaseViewModel<T>>(
    @LayoutRes contentLayoutId: Int
) : Fragment(contentLayoutId),
    BaseActionListener<T> {

    protected lateinit var viewModel: Q

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListener()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)
            .get((javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[1] as Class<Q>)
        viewModel.actionPerformer = this
    }

    open fun setOnClickListener() = Unit
}