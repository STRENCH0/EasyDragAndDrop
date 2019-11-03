package ru.checka.easydnd

import android.view.View
import java.lang.Exception

@ConfigMarker
abstract class BaseConfig<S, R> internal constructor() {

    /**
     * Calls when dropping is performed. Sender and Receiver objects of type [DragAndDropObject] will be passed as params
     */
    open var onDropped: ((sender: S, receiver: R) -> Unit)? = null


    /**
     * Calls when drag object covers receiver's area. Receiver [View] will be passed as param
     */
    open var onDragEntered: ((View) -> Unit)? = null

    /**
     * Calls when drag object stop covers receiver's area. Receiver [View] will be passed as param
     */
    open var onDragExited: ((View) -> Unit)? = null

    open var onDragLocation: ((View, Float, Float) -> Unit)? = null

    //function setters

    /**
     * DSL-like method for variable [onDropped]
     */
    fun onDropped(action: (sender: S, receiver: R) -> Unit) {
        this.onDropped = action
    }

    /**
     * DSL-like method for variable [onDragEntered]
     */
    fun onDragEntered(action: (View) -> Unit) {
        this.onDragEntered = action
    }

    /**
     * DSL-like method for variable [onDragExited]
     */
    fun onDragExited(action: (View) -> Unit) {
        this.onDragExited = action
    }

    fun onDragLocation(action: (View, Float, Float) -> Unit) {
        this.onDragLocation = action
    }

}

/**
 * Default configuration class
 */
open class DragAndDropDefaultConfig<S, R> : BaseConfig<S, R>() {

    /**
     * Calls when start dragging sender. Sender [DragAndDropObject] will be passed as param
     */
    var onSenderDragStart: ((DragAndDropObject<S>) -> Unit)? = null

    /**
     * Calls when stop dragging sender. Sender [DragAndDropObject] will be passed as param
     */
    var onSenderDragStop: ((DragAndDropObject<S>) -> Unit)? = null

    /**
     * Dropping on self
     */
    var selfDrop: Boolean = false

    /**
     * Action on which Drag and Drop action starts
     */
    var userAction: UserAction = UserAction.TOUCH

    /**
     * Override default [View.DragShadowBuilder]
     */
    var shadowBuilder: (View, S) -> View.DragShadowBuilder =
        { view, _ -> DefaultShadowBuilder(view) }

    /**
     * Drag flags of [View]. Use flags starting with prefix DRAG_FLAG_
     */
    var dragFlags: Int = 0

    //function setters

    /**
     * DSL-like method for variable [onSenderDragStart]
     */
    fun onSenderDragStart(action: (DragAndDropObject<S>) -> Unit) {
        this.onSenderDragStart = action
    }

    /**
     * DSL-like method for variable [onSenderDragStop]
     */
    fun onSenderDragStop(action: (DragAndDropObject<S>) -> Unit) {
        this.onSenderDragStop = action
    }

}

/**
 * Additional configuration which can override default behavior of [DragAndDropDefaultConfig]
 */
class DragAndDropLocalConfig<S, R> : BaseConfig<S, R>() {

    fun callSuper(view: View, x: Float = 0f, y: Float = 0f) {
        when (Thread.currentThread().stackTrace[5].methodName) {
            "handleActionDragEnter" -> default?.onDragEntered?.invoke(view)
            "handleActionDragExit" -> default?.onDragExited?.invoke(view)
            "handleActionDragLocation" -> default?.onDragLocation?.invoke(view, x, y)
            else -> throw WrongArgumentsException()
        }
    }

    fun callSuper(sender: S, receiver: R) {
        if (Thread.currentThread().stackTrace[5].methodName == "handleActionDrop") {
            default?.onDropped?.invoke(sender, receiver)
        } else {
            throw WrongArgumentsException()
        }
    }

    internal fun provideDefaultConfigForUpCall(default: DragAndDropDefaultConfig<S, R>) {
        this.default = default
    }

    private var default: DragAndDropDefaultConfig<S, R>? = null
}

internal class DragAndDropLocalConfigInternal<S, R>(
    private val local: DragAndDropLocalConfig<S, R>?,
    private val default: DragAndDropDefaultConfig<S, R>,
    val sender: S,
    val receiver: R
) : BaseConfig<S, R>() {

    init {
        local?.provideDefaultConfigForUpCall(default)
    }

    override var onDragEntered: ((View) -> Unit)?
        get() = local?.onDragEntered ?: default.onDragEntered
        set(value) {
            super.onDragEntered = value
        }

    override var onDragExited: ((View) -> Unit)?
        get() = local?.onDragExited ?: default.onDragExited
        set(value) {
            super.onDragExited = value
        }

    override var onDropped: ((sender: S, receiver: R) -> Unit)?
        get() = local?.onDropped ?: default.onDropped
        set(value) {
            super.onDropped = value
        }

    override var onDragLocation: ((View, Float, Float) -> Unit)?
        get() = local?.onDragLocation ?: default.onDragLocation
        set(value) {
            super.onDragLocation = value
        }

}

class WrongArgumentsException(): Exception("Unable to cast arguments")

/**
 * User actions
 */
enum class UserAction {
    /**
     * Drag must be performed on user's touch action
     */
    TOUCH,

    /**
     * Drag must be performed on user's long click action
     */
    LONG_CLICK
}

@DslMarker
annotation class ConfigMarker
