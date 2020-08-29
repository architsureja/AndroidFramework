/*
 * Copyright (c) Mobiquityinc, 2020.
 * All rights reserved.
 */
package com.archit.androidframework.stacking_viewholder.views

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.archit.androidframework.R

/**
 * Created on 8/29/20.
 */

abstract class StackingAdapter<T: RecyclerView.ViewHolder>(var isStacking: Boolean): RecyclerView.Adapter<T>()

class StackItemDecoration : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if (view.height == 0) fixLayoutSize(view, parent)

        val offset = view.context.resources.getDimensionPixelOffset(R.dimen.list_invite_offset)
        val elevation = view.context.resources.getDimensionPixelOffset(R.dimen.list_item_elevation)
        val viewHolder = parent.findContainingViewHolder(view)

        val isStackingAdapter = (viewHolder?.bindingAdapter as? StackingAdapter)?.isStacking ?: false
        if (isStackingAdapter && viewHolder != null) {
            when (viewHolder.bindingAdapterPosition) {
                0 -> {
                    outRect.set(offset, 0, -offset, 0)
                    ViewCompat.setElevation(view, elevation + offset * 3f)
                }
                1 -> {
                    outRect.set(0, -view.height + offset, 0, 0)
                    ViewCompat.setElevation(view, elevation + offset * 2f)
                }
                2 -> {outRect.set(-offset, -view.height + offset, offset, 0)
                    ViewCompat.setElevation(view, elevation.toFloat() + offset)
                }
                else -> {outRect.set(-offset, -view.height, offset, 0)
                    ViewCompat.setElevation(view, elevation.toFloat())
                }
            }
        } else {
            super.getItemOffsets(outRect, view, parent, state)
            ViewCompat.setElevation(view, elevation.toFloat())
        }
    }

    private fun fixLayoutSize(view: View, parent: ViewGroup) {
        if (view.layoutParams == null) {
            view.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.UNSPECIFIED)
        val childWidth = ViewGroup.getChildMeasureSpec(
            widthSpec, parent.paddingLeft + parent.paddingRight, view.layoutParams.width
        )
        val childHeight = ViewGroup.getChildMeasureSpec(
            heightSpec, parent.paddingTop + parent.paddingBottom, view.layoutParams.height
        )
        view.measure(childWidth, childHeight)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    }
}