package com.archit.androidframework.views.multistatebutton

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.graphics.PorterDuff
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.archit.androidframework.R
import com.archit.androidframework.util.*


class MorphingAnimationHandler(
    private val multiStateButtonValues: MultiStateButtonValues,
    private val buttonRoot: RelativeLayout,
    private val linearLayout: LinearLayout,
    private val buttonText: TextView,
    private val animationView: LottieAnimationView,
    private var stateListener: MultiStateButton.StateListener?,
    private val fitWidth: Boolean = false
) {
    var isMorphingInProgress = false
    private var mCurrentState: MultiStateButton.State = MultiStateButton.State.IDLE

    private val widthUpdateListener = ValueAnimator.AnimatorUpdateListener {
        buttonRoot.updateWidth(it.animatedValue as Int)
    }

    private val heightUpdateListener = ValueAnimator.AnimatorUpdateListener {
        buttonRoot.updateHeight(it.animatedValue as Int)
    }

    private val colorUpdateListener = ValueAnimator.AnimatorUpdateListener {
        buttonRoot.background.setColorFilter(it.animatedValue as Int, PorterDuff.Mode.SRC)
    }

    fun hideLayout() {
        mCurrentState = MultiStateButton.State.HIDDEN
        buttonRoot.hide()
    }

    fun showIdleText(doAnimation: Boolean) {
        if (!doAnimation || mCurrentState == MultiStateButton.State.HIDDEN) {
            mCurrentState = MultiStateButton.State.IDLE
            hideLoading()
            bindText()
            return
        }

        if (mCurrentState != MultiStateButton.State.LOADING) {
            return
        }

        val expectedWidth = calculateExpectedWidth(
            multiStateButtonValues.idleText
        )
        val widthAnimation =
            ButtonUtils.getValueAnimator(buttonRoot.width, expectedWidth, widthUpdateListener)
        val colorAnimation = ButtonUtils.colorAnimator(
            multiStateButtonValues.progressBackgroundColor,
            multiStateButtonValues.idleBackgroundColor,
            colorUpdateListener
        )

        val mMorphingAnimatorSet = AnimatorSet()
        mMorphingAnimatorSet.playTogether(widthAnimation, colorAnimation)
        mMorphingAnimatorSet.duration = multiStateButtonValues.morphingAnimationDuration
        mMorphingAnimatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                setIdleLayout()
                isMorphingInProgress = false
            }

            override fun onAnimationStart(animation: Animator?) {
                mCurrentState = MultiStateButton.State.IDLE
                hideLoading()
                isMorphingInProgress = true
            }
        })
        mMorphingAnimatorSet.start()
    }

    fun setStateListener(stateListener: MultiStateButton.StateListener) {
        this.stateListener = stateListener
    }

    fun getCurrentState(): MultiStateButton.State {
        return mCurrentState
    }

    private fun setIdleLayout() {
        bindText()
        ButtonUtils.animate(
            linearLayout,
            multiStateButtonValues.fadeAnimationDuration,
            R.anim.slide_in_top
        )
    }

    private fun bindText() {
        buttonRoot.show()
        linearLayout.show()
        buttonText.text = multiStateButtonValues.idleText
        buttonText.setTextColor(multiStateButtonValues.idleTextColor)

        buttonRoot.background.setColorFilter(
            multiStateButtonValues.idleBackgroundColor,
            PorterDuff.Mode.SRC
        )
    }

    fun hideLoading() {
        if (animationView.isAnimating) {
            animationView.cancelAnimation()
        }
        animationView.hide()
    }

    private fun showLoader() {
        buttonRoot.show()
        animationView.show()
        animationView.playAnimation()
    }

    fun startLoading() {
        if (mCurrentState == MultiStateButton.State.HIDDEN) {
            mCurrentState = MultiStateButton.State.LOADING
            showLoader()
            return
        }
        if (mCurrentState != MultiStateButton.State.IDLE) {
            return
        }
        val widthAnimation =
            ButtonUtils.getValueAnimator(buttonRoot.width, buttonRoot.height, widthUpdateListener)
        val heightAnimation =
            ButtonUtils.getValueAnimator(buttonRoot.height, buttonRoot.height, heightUpdateListener)
        val colorAnimation = ButtonUtils.colorAnimator(
            multiStateButtonValues.idleBackgroundColor,
            multiStateButtonValues.progressBackgroundColor,
            colorUpdateListener
        )

        val mMorphingAnimatorSet = AnimatorSet()
        mMorphingAnimatorSet.duration = multiStateButtonValues.morphingAnimationDuration
        mMorphingAnimatorSet.playTogether(widthAnimation, heightAnimation, colorAnimation)
        mMorphingAnimatorSet.startDelay = multiStateButtonValues.fadeAnimationDuration
        mMorphingAnimatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                showLoader()
                isMorphingInProgress = false
            }

            override fun onAnimationStart(animation: Animator?) {
                mCurrentState = MultiStateButton.State.LOADING
                hideButtonText()
                isMorphingInProgress = true
            }
        })
        mMorphingAnimatorSet.start()
    }

    private fun hideButtonText() {
        ButtonUtils.animate(linearLayout,
            multiStateButtonValues.fadeAnimationDuration,
            R.anim.slide_out_down,
            object : AnimationListener {
                override fun onAnimationStart(animation: Animation?) = Unit

                override fun onAnimationEnd(animation: Animation?) {
                    linearLayout.hide()
                }

                override fun onAnimationRepeat(animation: Animation?) = Unit
            })
    }

    fun showSuccess() {
        if (mCurrentState != MultiStateButton.State.LOADING) {
            return
        }

        val expectedWidth = calculateExpectedWidth(
            multiStateButtonValues.successText
        )
        val widthAnimation =
            ButtonUtils.getValueAnimator(buttonRoot.width, expectedWidth, widthUpdateListener)
        val colorAnimation = ButtonUtils.colorAnimator(
            multiStateButtonValues.progressBackgroundColor,
            multiStateButtonValues.successBackgroundColor,
            colorUpdateListener
        )

        val mMorphingAnimatorSet = AnimatorSet()
        mMorphingAnimatorSet.playTogether(widthAnimation, colorAnimation)
        mMorphingAnimatorSet.duration = multiStateButtonValues.morphingAnimationDuration
        mMorphingAnimatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                setSuccessText()
                isMorphingInProgress = false
            }

            override fun onAnimationStart(animation: Animator?) {
                mCurrentState = MultiStateButton.State.COMPLETE
                hideLoading()
                isMorphingInProgress = true
            }
        })

        mMorphingAnimatorSet.start()
    }

    private fun calculateExpectedWidth(string: String): Int {
        if (fitWidth) {
            return (buttonRoot.parent as ViewGroup).width
        }
        buttonText.text = string
        linearLayout.invisible()
        val layoutWidth = View.MeasureSpec.UNSPECIFIED
        linearLayout.measure(
            View.MeasureSpec.makeMeasureSpec(
                buttonRoot.width,
                layoutWidth
            ), View.MeasureSpec.makeMeasureSpec(
                0,
                View.MeasureSpec.UNSPECIFIED
            )
        )
        return linearLayout.measuredWidth
    }

    private fun setSuccessText() {
        buttonText.setTextColor(multiStateButtonValues.successTextColor)
        buttonText.text = multiStateButtonValues.successText

        linearLayout.show()
        ButtonUtils.animate(linearLayout,
            multiStateButtonValues.fadeAnimationDuration,
            R.anim.slide_in_top,
            object : AnimationListener {
                override fun onAnimationStart(animation: Animation?) = Unit

                override fun onAnimationEnd(animation: Animation?) {
                    Handler().postDelayed({
                        stateListener?.onSuccessState()
                    }, multiStateButtonValues.fadeAnimationDuration)
                }

                override fun onAnimationRepeat(animation: Animation?) = Unit
            })

        buttonRoot.background.setColorFilter(
            multiStateButtonValues.successBackgroundColor,
            PorterDuff.Mode.SRC
        )
    }

    fun setIdleText(text: String) {
        multiStateButtonValues.idleText = text
        bindText()
    }

    fun setSuccessText(text: String) {
        multiStateButtonValues.successText = text
    }
}

