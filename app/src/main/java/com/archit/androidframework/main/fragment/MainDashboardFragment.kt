package com.archit.androidframework.main.fragment

import android.view.View
import androidx.navigation.findNavController
import com.archit.androidframework.R
import com.archit.androidframework.base.BaseFragment
import com.archit.androidframework.main.viewmodel.MainDashboardViewModel
import com.archit.androidframework.main.viewmodel.MainDashboardViewModel.Action
import com.archit.androidframework.util.setDebounceOnClickListener
import kotlinx.android.synthetic.main.main_dashboard_fragment.*

class MainDashboardFragment :
    BaseFragment<Action, MainDashboardViewModel>(
        R.layout.main_dashboard_fragment
    ) {

    override fun setOnClickListener() {
        setDebounceOnClickListener(listOf(btnPickerFeature, btnStackingFeature)) { view ->
            when(view){
                btnPickerFeature -> viewModel.pickerFeatureClicked()
                btnStackingFeature -> viewModel.stackingViewHolderFeatureClicked()
            }
        }
    }

    override fun performAction(action: Action) {
        when (action) {
            Action.START_PICKER_FLOW ->
                view?.findNavController()?.navigate(R.id.actionStartPickerActivity)
            Action.START_STACKING_VIEW_HOLDER_FLOW ->
                view?.findNavController()?.navigate(R.id.actionStartStackingViewHolderActivity)
        }
    }
}