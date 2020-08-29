package com.archit.androidframework.stacking_viewholder.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.archit.androidframework.R
import com.archit.androidframework.base.BaseFragment
import com.archit.androidframework.stacking_viewholder.viewmodel.StackingViewHolderViewModel
import com.archit.androidframework.stacking_viewholder.viewmodel.StackingViewHolderViewModel.Action.LOAD_DATA
import com.archit.androidframework.stacking_viewholder.viewmodel.StackingViewHolderViewModel.Action.LOAD_DATA_FAIL
import com.archit.androidframework.stacking_viewholder.views.StackItemDecoration
import com.archit.androidframework.stacking_viewholder.views.StackingAdapter
import com.archit.androidframework.util.setDebounceOnClickListener
import com.archit.androidframework.views.multistatebutton.MultiStateButton
import com.archit.androidframework.views.multistatebutton.MultiStateButton.StateListener
import kotlinx.android.synthetic.main.stacking_dashboard_fragment.*
import kotlinx.android.synthetic.main.stacking_item_holder.view.*

class StackingViewHolderFragment :
    BaseFragment<StackingViewHolderViewModel.Action, StackingViewHolderViewModel>(
        R.layout.stacking_dashboard_fragment
    ) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    override fun setOnClickListener() {
        super.setOnClickListener()
        stateButton.setDebounceOnClickListener {
            stateButton.setButtonState(MultiStateButton.State.LOADING)
            viewModel.getNewData()
        }
    }

    private fun initRecyclerView() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(StackItemDecoration())
        }
    }

    private fun loadData(data: List<String>) {
        stateButton.setButtonState(MultiStateButton.State.COMPLETE)
        stateButton.setStateListener(object : StateListener {
            override fun onSuccessState() {
                stateButton.setButtonState(MultiStateButton.State.HIDDEN)
                recyclerView.adapter = ListAdapter(data)
            }
        })

    }

    override fun performAction(action: StackingViewHolderViewModel.Action, payload: Any?) {
        when (action) {
            LOAD_DATA -> loadData((payload as? List<String>) ?: emptyList())
            LOAD_DATA_FAIL -> stateButton.setButtonState(MultiStateButton.State.IDLE)
        }
    }

    class ListAdapter(private val data: List<String>) :
        StackingAdapter<ListAdapter.ItemViewHolder>(true) {

        class ItemViewHolder(itemView: View, clickListener: (View) -> Unit) :
            RecyclerView.ViewHolder(itemView) {
            init {
                itemView.setOnClickListener(clickListener)
            }

            fun bind(image: String) = with(itemView) {
                itemImage.setImageResource(
                    resources.getIdentifier(
                        image,
                        "drawable",
                        context.packageName
                    )
                )
            }
        }

        private val clickListener = { _: View ->
            isStacking = !isStacking
            notifyItemRangeChanged(0, data.size)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            return ItemViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.stacking_item_holder, parent, false),
                clickListener
            )
        }

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            holder.bind(data[position])
        }

        override fun getItemCount(): Int = data.size
    }
}