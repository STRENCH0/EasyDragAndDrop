package ru.checka.easydnd

class DragAndDropControllerImpl<S, R>(
    private val manager: DragAndDropManager<S, R>
) : DragAndDropController<S, R> {

    override fun mapSets(
        newSenders: Set<DragAndDropObject<S>>,
        newReceivers: Set<DragAndDropObject<R>>,
        init: (DragAndDropLocalConfig<S, R>.() -> Unit)?
    ) {
        var localConfig: DragAndDropLocalConfig<S, R>? = null
        if (init != null) {
            localConfig = DragAndDropLocalConfig<S, R>()
            localConfig.init()
        }
        manager.mapSets(newSenders, newReceivers, localConfig)
        manager.applyDragAndDrop()
    }

    override fun mapSets(init: (DslSetConfig<S, R>.() -> Unit)) {
        val dslSetConfig = DslSetConfig<S, R>()
        dslSetConfig.init()
        mapSets(dslSetConfig.senders, dslSetConfig.receivers, dslSetConfig.localConfigInit)
    }

    override fun disable() = manager.disable()

    override fun enable() = manager.applyDragAndDrop()
}