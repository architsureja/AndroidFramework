package com.archit.androidframework.stacking_viewholder.fragment

import com.archit.androidframework.R
import com.archit.androidframework.base.BaseFragment
import com.archit.androidframework.stacking_viewholder.viewmodel.StackingDashboardViewModel

class StackingDashboardFragment :
    BaseFragment<StackingDashboardViewModel.Action, StackingDashboardViewModel>(
        R.layout.stacking_dashboard_fragment
    ) {
    override fun performAction(action: StackingDashboardViewModel.Action) = Unit
}