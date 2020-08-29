package com.archit.androidframework.views.multistatebutton

import android.content.Context
import android.content.res.TypedArray
import android.os.Handler
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.VisibleForTesting
import androidx.core.content.ContextCompat
import com.archit.androidframework.R
import kotlinx.android.synthetic.main.multi_state_button.view.*


class MultiStateButton : FrameLayout {

    private lateinit var animationHandler: MorphingAnimationHandler
    private var fadeAnimationDuration: Long =
        lazy { context.resources.getInteger(android.R.integer.config_shortAnimTime).toLong() }.value
    private var morphingAnimationDuration: Long =
        lazy {
            context.resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
        }.value

    enum class State {
        HIDDEN, IDLE, LOADING, COMPLETE
    }

    lateinit var multiStateButtonValues: MultiStateButtonValues

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    private fun init(context: Context, attributeSet: AttributeSet? = null) {
        inflate(context, R.layout.multi_state_button, this)

        val attr = getTypedArray(context, attributeSet, R.styleable.MultiStateButton)
            ?: return

        val white = ContextCompat.getColor(context, android.R.color.white)
        val greyBlue = ContextCompat.getColor(context, R.color.dark_grey_blue)
        val green = ContextCompat.getColor(context, R.color.my_green)
        val ilaBlue = ContextCompat.getColor(context, R.color.my_blue)
        var fitWidth: Boolean

        try {
            val valueBuilder = MultiStateButtonValues.Builder()
            valueBuilder.initialState =
                State.values()[attr.getInt(R.styleable.MultiStateButton_initialState, 1)]
            valueBuilder.idleText =
                getDefaultString(
                    attr.getString(R.styleable.MultiStateButton_idleText),
                    context.getString(R.string.next)
                )
            valueBuilder.idleTextColor =
                attr.getColor(R.styleable.MultiStateButton_idleTextColor, white)
            valueBuilder.idleBackgroundColor =
                attr.getColor(R.styleable.MultiStateButton_idleBackgroundColor, greyBlue)
            valueBuilder.progressBackgroundColor =
                attr.getColor(R.styleable.MultiStateButton_loadingBackgroundColor, greyBlue)
            valueBuilder.successTextColor =
                attr.getColor(R.styleable.MultiStateButton_successTextColor, ilaBlue)
            valueBuilder.successBackgroundColor =
                attr.getColor(R.styleable.MultiStateButton_successBackgroundColor, green)
            valueBuilder.successText = getDefaultString(
                attr.getString(R.styleable.MultiStateButton_successText),
                context.getString(R.string.done)
            )
            fitWidth = attr.getBoolean(R.styleable.MultiStateButton_fitWidth, false)

            valueBuilder.fadeAnimationDuration = fadeAnimationDuration
            valueBuilder.morphingAnimationDuration = morphingAnimationDuration
            multiStateButtonValues = valueBuilder.build()

        } finally {
            attr.recycle()
        }

        animationHandler = MorphingAnimationHandler(
            multiStateButtonValues,
            buttonRoot,
            linearLayout,
            buttonText,
            animationView,
            null,
            fitWidth
        )
        setInitialState(multiStateButtonValues.initialState)
    }

    fun setStateListener(stateListener: StateListener) {
        animationHandler.setStateListener(stateListener)
    }

    fun getCurrentState(): State {
        return animationHandler.getCurrentState()
    }

    private fun setInitialState(state: State) {
        if (state == State.HIDDEN) {
            setButtonState(state)
        } else if (state == State.IDLE) {
            isClickable = true
            animationHandler.showIdleText(false)
        }
    }

    fun setButtonState(state: State) {
        if (animationHandler.isMorphingInProgress) {
            Handler().postDelayed({
                setButtonState(state)
            }, morphingAnimationDuration)
            return
        }
        when (state) {
            State.HIDDEN -> {
                isClickable = false
                animationHandler.hideLayout()
            }
            State.IDLE -> {
                isClickable = true
                animationHandler.showIdleText(true)
            }
            State.LOADING -> {
                isClickable = false
                animationHandler.startLoading()
            }
            State.COMPLETE -> {
                isClickable = false
                animationHandler.showSuccess()
            }
        }
    }

    fun setIdleText(text: String) {
        animationHandler.setIdleText(text)
    }

    fun setSuccessText(text: String) {
        animationHandler.setSuccessText(text)
    }

    private fun getDefaultString(string: String?, defaultString: String) =
        string ?: defaultString


    private fun getTypedArray(
        context: Context,
        attributeSet: AttributeSet?,
        attr: IntArray
    ): TypedArray? {
        return context.obtainStyledAttributes(attributeSet, attr, 0, 0)
    }

    @VisibleForTesting
    fun setAnimationHandler(animationHandler: MorphingAnimationHandler) {
        this.animationHandler = animationHandler
    }

    interface StateListener {
        fun onSuccessState()
    }
}