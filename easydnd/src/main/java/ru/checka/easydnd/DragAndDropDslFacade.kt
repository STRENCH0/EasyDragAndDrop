package ru.checka.easydnd

@ConfigMarker
class DragAndDropDslFacade<S, R>(
    private val dragAndDropManager: DragAndDropManager<S, R>
) {

    fun mapSets(
        newSenders: Set<DragAndDropObject<S>>,
        newReceivers: Set<DragAndDropObject<R>>,
        init: (DragAndDropLocalConfig<S, R>.() -> Unit)? = null
    ) {
        if (init != null) {
            val localConfig = DragAndDropLocalConfig<S, R>()
            localConfig.init()
            dragAndDropManager.mapSets(newSenders, newReceivers, localConfig)
        }
    }

    fun default(init: (DragAndDropDefaultConfig<S, R>.() -> Unit)) {
        dragAndDropManager.defaultConfig.init()
    }



}