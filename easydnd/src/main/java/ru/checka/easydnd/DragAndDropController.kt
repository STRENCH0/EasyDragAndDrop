package ru.checka.easydnd

interface DragAndDropController<S : DragAssignment, R : DragAssignment> {

    public fun mapSets(
        newSenders: Set<DragAndDropObject<S>>,
        newReceivers: Set<DragAndDropObject<R>>,
        init: (DragAndDropLocalConfig<S, R>.() -> Unit)? = null
    )

    public fun disable()

    public fun enable()

}