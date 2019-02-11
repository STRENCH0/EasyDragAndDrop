package ru.checka.easydnd

import android.view.View

public fun <S : DragAssignment, R : DragAssignment> enableDragAndDrop(init: DragAndDropManager<S, R>.() -> Unit) {
    val config = DragAndDropManager<S, R>().apply(init)
    config.applyDragAndDrop()

}

public infix fun <T : DragAssignment> View.assign(assigned: T): DragAndDropObject<T> {
    return object : DragAndDropObject<T> {
        override val view = this@assign
        override val assignedObject = assigned
    }
}