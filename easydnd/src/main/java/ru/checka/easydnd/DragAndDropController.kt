package ru.checka.easydnd

/**
 * Controller to manage drag-and-drop after initial dsl setup
 */
interface DragAndDropController<S, R> {

    fun mapSets(
        newSenders: Set<DragAndDropObject<S>>,
        newReceivers: Set<DragAndDropObject<R>>,
        init: (DragAndDropLocalConfig<S, R>.() -> Unit)? = null
    )

    fun mapSets(init: DslSetConfig<S, R>.() -> Unit)

    fun disable()

    fun enable()
}