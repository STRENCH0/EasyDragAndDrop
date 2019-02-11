package ru.checka.easydnd

import android.view.View

public open class BaseConfig<S : DragAssignment, R : DragAssignment> {

    var onDropped: ((sender: S, receiver: R) -> Unit)? = null
    var onDragEntered: ((View) -> Unit)? = null
    var onDragExited: ((View) -> Unit)? = null

    //function setters
    fun onDropped(action: (sender: S, receiver: R) -> Unit) {
        this.onDropped = action
    }

    fun onDragEntered(action: (View) -> Unit) {
        this.onDragEntered = action
    }

    fun onDragExited(action: (View) -> Unit) {
        this.onDragExited = action
    }

}

public class DragAndDropDefaultConfig<S : DragAssignment, R : DragAssignment> : BaseConfig<S, R>() {
    var onSenderDragStart: ((DragAndDropObject<S>) -> Unit)? = null
    var onSenderDragStop: ((DragAndDropObject<S>) -> Unit)? = null
    var selfDrop: Boolean = false
    var userAction: UserAction = UserAction.TOUCH
    var shadowBuilder: (View, DragAssignment) -> View.DragShadowBuilder = { view, _ -> DefaultShadowBuilder(view) }
    var dragFlags: Int = 0

   //function setters

    fun onSenderDragStart(action: (DragAndDropObject<S>) -> Unit) {
        this.onSenderDragStart = action
    }

    fun onSenderDragStop(action: (DragAndDropObject<S>) -> Unit) {
        this.onSenderDragStop = action
    }

}

public class DragAndDropLocalConfig<S : DragAssignment, R : DragAssignment> : BaseConfig<S, R>()


public enum class UserAction {
    TOUCH, LONG_CLICK
}

