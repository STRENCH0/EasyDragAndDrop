package ru.checka.easydnd

import android.view.View

@ConfigMarker
class DragAndDropDslFacade<S, R>(
    private val dragAndDropManager: DragAndDropManager<S, R>
) {

    fun mapSets(
        newSenders: Set<DragAndDropObject<S>>,
        newReceivers: Set<DragAndDropObject<R>>,
        init: (DragAndDropLocalConfig<S, R>.() -> Unit)? = null
    ) {
        var localConfig: DragAndDropLocalConfig<S, R>? = null
        if (init != null) {
            localConfig = DragAndDropLocalConfig<S, R>()
            localConfig.init()
        }
        dragAndDropManager.mapSets(newSenders, newReceivers, localConfig)
    }

    fun mapSets(init: (DslSetConfig<S, R>.() -> Unit)) {
        val dslSetConfig = DslSetConfig<S, R>()
        dslSetConfig.init()
        mapSets(dslSetConfig.senders, dslSetConfig.receivers, dslSetConfig.localConfigInit)
    }

    fun default(init: (DragAndDropDefaultConfig<S, R>.() -> Unit)) {
        dragAndDropManager.defaultConfig.init()
    }



}

@ConfigMarker
class DslSetConfig<S, R> {

    internal var localConfigInit: (DragAndDropLocalConfig<S, R>.() -> Unit)? = null

    internal var senders: MutableSet<DragAndDropObject<S>> = mutableSetOf()

    internal var receivers: MutableSet<DragAndDropObject<R>> = mutableSetOf()

    fun config(init: (DragAndDropLocalConfig<S, R>.() -> Unit)?) {
        localConfigInit = init
    }

    infix fun View.assignSender(assigned: S) {
        senders.add(DragAndDropObject(this, assigned))
    }

    infix fun View.assignReceiver(assigned: R) {
        receivers.add(DragAndDropObject(this, assigned))
    }

}

