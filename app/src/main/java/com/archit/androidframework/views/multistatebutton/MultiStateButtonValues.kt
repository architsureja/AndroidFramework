package com.archit.androidframework.views.multistatebutton

class MultiStateButtonValues private constructor(builder: Builder) {
    val initialState: MultiStateButton.State
    var idleText: String
    var successText: String
    val idleTextColor: Int
    val idleBackgroundColor: Int
    val progressBackgroundColor: Int
    val successTextColor: Int
    val successBackgroundColor: Int
    val fadeAnimationDuration: Long
    val morphingAnimationDuration: Long

    init {
        this.initialState = builder.initialState
        this.idleText = builder.idleText
        this.successText = builder.successText
        this.idleTextColor = builder.idleTextColor
        this.idleBackgroundColor = builder.idleBackgroundColor
        this.progressBackgroundColor = builder.progressBackgroundColor
        this.successTextColor = builder.successTextColor
        this.successBackgroundColor = builder.successBackgroundColor
        this.fadeAnimationDuration = builder.fadeAnimationDuration
        this.morphingAnimationDuration = builder.morphingAnimationDuration
    }

    class Builder {
        var initialState: MultiStateButton.State = MultiStateButton.State.IDLE
        var idleText: String = ""
        var successText: String = ""
        var idleTextColor: Int = 0
        var idleBackgroundColor: Int = 0
        var progressBackgroundColor: Int = 0
        var successTextColor: Int = 0
        var successBackgroundColor: Int = 0
        var fadeAnimationDuration: Long = 0
        var morphingAnimationDuration: Long = 0

        fun build() = MultiStateButtonValues(this)
    }
}