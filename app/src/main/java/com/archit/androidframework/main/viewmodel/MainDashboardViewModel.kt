package com.archit.androidframework.main.viewmodel

import com.archit.androidframework.base.BaseViewModel

class MainDashboardViewModel : BaseViewModel<MainDashboardViewModel.Action>() {

    fun pickerFeatureClicked() = actionPerformer?.performAction(Action.START_PICKER_FLOW)

    fun stackingViewHolderFeatureClicked() =
        actionPerformer?.performAction(Action.START_STACKING_VIEW_HOLDER_FLOW)

    enum class Action {
        START_PICKER_FLOW,
        START_STACKING_VIEW_HOLDER_FLOW
    }
}