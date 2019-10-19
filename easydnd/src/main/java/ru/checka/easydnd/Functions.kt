package ru.checka.easydnd

import android.view.View

/**
 * Creates [DragAndDropManager] and configure it
 * @param [S] type of sender's associated object
 * @param [R] type of receiver's associated object
 * @param init [DragAndDropManager] DSL-configuration
 */
public fun <S : DragAssignment, R : DragAssignment> enableDragAndDrop(
    init: DragAndDropManager<S, R>.() -> Unit
): DragAndDropController<S, R> {
    val config = DragAndDropManager<S, R>().apply(init)
    config.applyDragAndDrop()
    return DragAndDropControllerImpl(config)
}

/**
 * Simple creation of [DragAndDropObject]
 * @param assigned assigned object
 */
public infix fun <T : DragAssignment> View.assign(assigned: T): DragAndDropObject<T> {
    return object : DragAndDropObject<T> {
        override val view = this@assign
        override val assignedObject = assigned
    }
}