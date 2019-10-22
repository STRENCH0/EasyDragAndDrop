package ru.checka.easydnd

import android.view.View

/**
 * Creates [DragAndDropManager] and configure it
 * @param [S] type of sender's associated object
 * @param [R] type of receiver's associated object
 * @param init [DragAndDropManager] DSL-configuration
 */
fun <S, R> enableDragAndDrop(
    init: DragAndDropDslFacade<S, R>.() -> Unit
): DragAndDropController<S, R> {
    val manager = DragAndDropManager<S, R>()
    val facade = DragAndDropDslFacade(manager)
    facade.init()
    manager.applyDragAndDrop()
    return DragAndDropControllerImpl(manager)
}

/**
 * Simple creation of [DragAndDropObject]
 * @param assigned assigned object
 */
infix fun <T> View.assign(assigned: T): DragAndDropObject<T> {
    return DragAndDropObject<T>(this, assigned)
}