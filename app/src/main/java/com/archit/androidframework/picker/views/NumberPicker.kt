/*
 * Copyright (c) Mobiquityinc, 2020.
 * All rights reserved.
 */
package com.archit.androidframework.picker.views

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.archit.androidframework.R
import com.archit.androidframework.util.convertDpToPixel
import kotlinx.android.synthetic.main.number_picker.view.*
import kotlin.math.abs
import kotlin.math.pow


/**
 * Created on 2020-02-17.
 */

class NumberPicker : FrameLayout {
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(context)
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private fun init(context: Context) {
        inflate(context, R.layout.number_picker, this)
    }

    fun setMaxValue(value: Int) = run { numberPickerRecyclerView.maxValue = value }
    fun setMinValue(value: Int) = run { numberPickerRecyclerView.minValue = value }
    fun setValue(value: Int) = numberPickerRecyclerView.setValue(value)
    fun getValue() = numberPickerRecyclerView.getValue()
}

class NumberPickerRecyclerView(context: Context, val attrs: AttributeSet?, defStyle: Int) :
    RecyclerView(context, attrs, defStyle) {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private var currentValue = 0
    var maxValue = 100
        set(value) {
            field = value
            if (currentValue > value) {
                currentValue = value
            }
            this.adapter?.notifyDataSetChanged()
        }
    var minValue = 0
        set(value) {
            field = value
            if (currentValue < value) {
                currentValue = value
            }
            this.adapter?.notifyDataSetChanged()
        }

    init {
        val manager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(this)
        layoutManager = manager
        onFlingListener = snapHelper
        addItemDecoration(paddingDecoration())
        adapter = PickerAdapter(context).apply {
            registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onChanged() {
                    val position = currentValue - minValue
                    scrollToPosition(if (position > 0) position else 0)
                    smoothScrollBy(0, 10)
                }

            })
        }

        addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == SCROLL_STATE_IDLE) {
                    val centerView = snapHelper.findSnapView(manager)
                    centerView?.let {
                        val pos = manager.getPosition(it)
                        currentValue = minValue + pos
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                onScrollChanged()
            }
        })
    }

    fun getValue() = currentValue

    fun setValue(value: Int) {
        when (value) {
            in minValue..maxValue -> {
                currentValue = value
                this.scrollToPosition(value - minValue)
                smoothScrollBy(0, 10)
            }
            else -> {
                currentValue = minValue
                this.scrollToPosition(0)
                smoothScrollBy(0, 10)
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setPadding(0, (height / 2) - 10, 0, (height / 2) - 10)
        val position = currentValue - minValue
        scrollToPosition(if (position > 0) position else 0)
        smoothScrollBy(0, 10)
    }

    private fun onScrollChanged() {
        post {
            (0 until childCount).forEach { position ->
                val child: AppCompatTextView = getChildAt(position) as AppCompatTextView
                val childCenterY = (child.top + child.bottom) / 2
                val scaleValue = getGaussianScale(childCenterY, 1f, 1f, 150.toDouble()) - 1
                child.setTextSize(
                    TypedValue.COMPLEX_UNIT_SP,
                    32f * if (scaleValue > 0.5) scaleValue else 0.5f
                )
                child.alpha = getLinearScale(childCenterY, child.height)
            }
        }
    }

    private fun getLinearScale(
        childCenterY: Int,
        childHeight: Int
    ): Float {
        val recyclerCenterY = height / 2
        val scale = abs(recyclerCenterY - childCenterY) / (childHeight * 7f)
        return abs(if (scale > 1) 0f else scale - 1)
    }

    private fun getGaussianScale(
        childCenterY: Int,
        minScaleOffest: Float,
        scaleFactor: Float,
        spreadFactor: Double
    ): Float {
        val recyclerCenterY = height / 2
        return (Math.E.pow(
            -(childCenterY - recyclerCenterY.toDouble()).pow(2.toDouble()) / (2 * spreadFactor.pow(
                2.toDouble()
            ))
        ) * scaleFactor + minScaleOffest).toFloat()
    }

    internal inner class PickerAdapter(private val context: Context) :
        Adapter<PickerAdapter.PickerViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickerViewHolder {
            val view = AppCompatTextView(context, attrs)
            view.setTextAppearance(context, R.style.H1Header)
            val params = android.widget.LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT,
                1f
            )
            view.layoutParams = params
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32f)
            view.gravity = Gravity.CENTER
            return PickerViewHolder(view)
        }

        override fun onBindViewHolder(holder: PickerViewHolder, position: Int) {
            holder.numberView.text = (minValue + position).toString()
        }

        override fun getItemCount(): Int {
            return maxValue - minValue + 1
        }

        internal inner class PickerViewHolder(val numberView: AppCompatTextView) :
            ViewHolder(numberView)
    }

    internal inner class paddingDecoration : ItemDecoration() {

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: State) {
            super.getItemOffsets(outRect, view, parent, state)
            val verticalMargin = 8.convertDpToPixel(context)
            outRect.top = verticalMargin
            outRect.bottom = verticalMargin
        }
    }
}

