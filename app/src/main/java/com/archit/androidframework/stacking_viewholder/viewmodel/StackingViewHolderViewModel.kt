package com.archit.androidframework.stacking_viewholder.viewmodel

import com.archit.androidframework.base.BaseViewModel
import com.archit.androidframework.util.CoroutineHelper
import kotlinx.coroutines.delay
import kotlin.random.Random

class StackingViewHolderViewModel(private val coroutineHelper: CoroutineHelper = CoroutineHelper()) :
    BaseViewModel<StackingViewHolderViewModel.Action>() {

    fun getNewData() {
        coroutineHelper.ioThenMain({
            delay(3000L)
            if(Random.nextBoolean()) {
                listOf("asd1", "asd2", "asd3", "asd4", "asd5", "asd6")
            }else{
                throw Exception()
            }
        }, { data: List<String>? ->
            actionPerformer?.performAction(Action.LOAD_DATA, data)
        }, {
            actionPerformer?.performAction(Action.LOAD_DATA_FAIL, null)
        })

    }

    enum class Action {
        LOAD_DATA,
        LOAD_DATA_FAIL,
    }
}