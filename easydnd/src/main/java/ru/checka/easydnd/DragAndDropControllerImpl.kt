package ru.checka.easydnd

class DragAndDropControllerImpl<S : DragAssignment, R : DragAssignment>(
    private val manager: DragAndDropManager<S, R>
) : DragAndDropController<S, R> {

    override fun mapSets(
        newSenders: Set<DragAndDropObject<S>>,
        newReceivers: Set<DragAndDropObject<R>>,
        init: (DragAndDropLocalConfig<S, R>.() -> Unit)?
    ) {
        manager.mapSets(newSenders, newReceivers, init)
        manager.applyDragAndDrop()
    }

    override fun disable() = manager.disable()

    override fun enable() = manager.applyDragAndDrop()
}