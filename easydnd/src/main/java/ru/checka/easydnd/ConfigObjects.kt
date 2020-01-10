package ru.checka.easydnd

import android.view.View
import java.lang.Exception

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
@ConfigMarker
open class DragAndDropDefaultConfig<S, R> {

    /**
     * Calls when dropping is performed. Sender and Receiver objects of type [DragAndDropObject] will be passed as params
     */
    var onDropped: ((sender: S, receiver: R) -> Unit)? = null


    /**
     * Calls when drag object covers receiver's area. Receiver [View] will be passed as param
     */
    var onDragEntered: ((View) -> Unit)? = null

    /**
     * Calls when drag object stop covers receiver's area. Receiver [View] will be passed as param
     */
    var onDragExited: ((View) -> Unit)? = null

    var onDragLocation: ((View, Float, Float) -> Unit)? = null

    /**
     * Calls when start dragging sender. Sender [DragAndDropObject] will be passed as param
     */
    var onSenderDragStart: ((DragAndDropObject<S>) -> Unit)? = null

    /**
     * Calls when stop dragging sender. Sender [DragAndDropObject] will be passed as param
     */
    var onSenderDragStop: ((DragAndDropObject<S>) -> Unit)? = null

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
 * Additional configuration which can override default behavior of [DragAndDropDefaultConfig]
 */
@ConfigMarker
class DragAndDropLocalConfig<S, R> {

    /**
     * Calls when dropping is performed. Sender and Receiver objects of type [DragAndDropObject] will be passed as params
     */
    var onDropped: (ChildOnDropped<S, R>.(sender: S, receiver: R) -> Unit)? = null


    /**
     * Calls when drag object covers receiver's area. Receiver [View] will be passed as param
     */
    var onDragEntered: (ChildOnDrag.(View) -> Unit)? = null

    /**
     * Calls when drag object stop covers receiver's area. Receiver [View] will be passed as param
     */
    var onDragExited: (ChildOnDrag.(View) -> Unit)? = null

    var onDragLocation: (ChildOnDragLocation.(View, Float, Float) -> Unit)? = null

    //function setters

    /**
     * DSL-like method for variable [onDropped]
     */
    fun onDropped(action: ChildOnDropped<S, R>.(sender: S, receiver: R) -> Unit) {
        this.onDropped = action
    }

    /**
     * DSL-like method for variable [onDragEntered]
     */
    fun onDragEntered(action: ChildOnDrag.(View) -> Unit) {
        this.onDragEntered = action
    }

    /**
     * DSL-like method for variable [onDragExited]
     */
    fun onDragExited(action: ChildOnDrag.(View) -> Unit) {
        this.onDragExited = action
    }

    fun onDragLocation(action: ChildOnDragLocation.(View, Float, Float) -> Unit) {
        this.onDragLocation = action
    }

}


internal class DragAndDropInternalConfig<S, R>(
    private val local: DragAndDropLocalConfig<S, R>?,
    private val default: DragAndDropDefaultConfig<S, R>,
    val sender: S,
    val receiver: R
) {

    private val onDroppedChild = object : ChildOnDropped<S, R> {
        override fun callSuper(sender: S, receiver: R) {
            default.onDropped?.invoke(sender, receiver)
        }
    }

    private val onDragEnteredChild = object : ChildOnDrag {
        override fun callSuper(view: View) {
            default.onDragEntered?.invoke(view)
        }
    }

    private val onDragExitedChild = object : ChildOnDrag {
        override fun callSuper(view: View) {
            default.onDragExited?.invoke(view)
        }
    }

    private val onDragLocationChild = object : ChildOnDragLocation {
        override fun callSuper(view: View, x: Float, y: Float) {
            default.onDragLocation?.invoke(view, x, y)
        }
    }

    fun onDropped() {
        local
            ?.onDropped
            ?.invoke(onDroppedChild, sender, receiver)
            ?: default
                .onDropped
                ?.invoke(sender, receiver)
    }

    fun onDragEntered(view: View) {
        local
            ?.onDragEntered
            ?.invoke(onDragEnteredChild, view)
            ?: default
                .onDragEntered
                ?.invoke(view)
    }

    fun onDragExited(view: View) {
        local
            ?.onDragExited
            ?.invoke(onDragExitedChild, view)
            ?: default
                .onDragExited
                ?.invoke(view)
    }

    fun onDragLocation(view: View, x: Float, y: Float) {
        local
            ?.onDragLocation
            ?.invoke(onDragLocationChild, view, x, y)
            ?: default
                .onDragLocation
                ?.invoke(view, x, y)
    }

}

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
