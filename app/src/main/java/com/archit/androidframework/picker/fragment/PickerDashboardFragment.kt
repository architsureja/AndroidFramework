package com.archit.androidframework.picker.fragment

import com.archit.androidframework.R
import com.archit.androidframework.base.BaseFragment
import com.archit.androidframework.picker.viewmodel.PickerDashboardViewModel

class PickerDashboardFragment :
    BaseFragment<PickerDashboardViewModel.Action, PickerDashboardViewModel>(
        R.layout.picker_dashboard_fragment
    ) {
    override fun performAction(action: PickerDashboardViewModel.Action, payload: Any?) = Unit
}