package com.archit.androidframework.util

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import androidx.annotation.AnimRes

object ButtonUtils {

    fun animate(
        viewToAnimate: View,
        duration: Long,
        @AnimRes animResource: Int,
        listener: Animation.AnimationListener? = null
    ) {
        AnimationUtils.loadAnimation(viewToAnimate.context, animResource).apply {
            this.duration = duration
            setAnimationListener(listener)
            viewToAnimate.startAnimation(this)
        }
    }

    fun getValueAnimator(
        fromWidth: Int, toWidth: Int, updateListener: ValueAnimator.AnimatorUpdateListener
    ): ValueAnimator {
        return ValueAnimator.ofInt(fromWidth, toWidth).apply {
            interpolator = DecelerateInterpolator()
            addUpdateListener(updateListener)
        }
    }

    fun colorAnimator(
        startColor: Int, endColor: Int, updateListener: ValueAnimator.AnimatorUpdateListener
    ): ValueAnimator {
        return ValueAnimator.ofObject(ArgbEvaluator(), startColor, endColor).apply {
            addUpdateListener(updateListener)
        }
    }

}