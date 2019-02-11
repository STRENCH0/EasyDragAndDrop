package ru.checka.easydnd

import android.view.View

public abstract class BaseConfig<S : DragAssignment, R : DragAssignment> {

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

}

/**
 * Default configuration class
 */
public class DragAndDropDefaultConfig<S : DragAssignment, R : DragAssignment> : BaseConfig<S, R>() {

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
     * Override default [ShadowBuilder]
     */
    var shadowBuilder: (View, DragAssignment) -> View.DragShadowBuilder = { view, _ -> DefaultShadowBuilder(view) }

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
public class DragAndDropLocalConfig<S : DragAssignment, R : DragAssignment> : BaseConfig<S, R>()


/**
 * User actions
 */
public enum class UserAction {
    /**
     * Drag must be performed on user's touch action
     */
    TOUCH,

    /**
     * Drag must be performed on user's long click action
     */
    LONG_CLICK
}

