package ru.checka.easydnd

import android.content.ClipData
import android.content.ClipDescription
import android.view.DragEvent
import android.view.View


/**
 * Main class of DragAndDrop
 */
class DragAndDropManager<S : DragAssignment, R : DragAssignment> {

    private var senders: MutableSet<DragAndDropObject<S>> = mutableSetOf()
    private var receivers: MutableSet<DragAndDropObject<R>> = mutableSetOf()
    private val receiverSenderMap = mutableMapOf<String, MutableSet<String>>()


    private val configs = SenderToReceiverActions<DragAndDropLocalConfig<S, R>>()
    private var defaultConfig: DragAndDropDefaultConfig<S, R> = DragAndDropDefaultConfig()

    /**
     * Add default settings
     */
    fun default(init: (DragAndDropDefaultConfig<S, R>.() -> Unit)) {
        defaultConfig.init()
    }

    /**
     * Maps set of sender objects to receivers
     * @param newSenders senders
     * @param newReceivers receivers
     * @param init overriding some default behavior for those pairs
     */
    fun mapSets(
        newSenders: Set<DragAndDropObject<S>>,
        newReceivers: Set<DragAndDropObject<R>>,
        init: (DragAndDropLocalConfig<S, R>.() -> Unit)? = null
    ) {

        //save all mappings
        senders.addAll(newSenders)
        receivers.addAll(newReceivers)
        for (receiver in newReceivers) {
            for (sender in newSenders) {
                val receiverTag = receiver.assignedObject.tag
                val senderTag = sender.assignedObject.tag
                receiverSenderMap
                    .getOrPut(receiverTag, ::mutableSetOf)
                    .add(senderTag)
            }
        }
        //save custom config
        if (init != null) {
            val config = DragAndDropLocalConfig<S, R>().apply { init() }
            for (sender in newSenders) {
                val senderTag = sender.assignedObject.tag
                for (receiver in newReceivers) {
                    val receiverTag = receiver.assignedObject.tag
                    configs[senderTag, receiverTag] = config
                }
            }
        }

    }

    internal fun applyDragAndDrop() {
        prepareSenders()
        prepareReceivers()
    }

    /**
     * Checks defaultConfig, sender and receiver on null and (sender -> receiver) mapping existing
     */
    private fun checkSenderReceiver(sender: DragAndDropObject<S>?, receiver: DragAndDropObject<R>?): Boolean {
        if (sender == null || receiver == null)
            return false
        if (defaultConfig.selfDrop || sender.assignedObject.tag != receiver.assignedObject.tag) {
            return receiverSenderMap[receiver.assignedObject.tag]
                ?.contains(sender.assignedObject.tag)
                ?: false
        }
        return false
    }

    private fun prepareReceivers() {
        var lastTag: String? = null
        val onDragListener: (View, DragEvent) -> Boolean = { view, event ->

            when (event.action) {
                DragEvent.ACTION_DROP -> {
                    val sender = senders.find { event.clipDescription.label == it.assignedObject.tag }
                    val receiver = receivers.find { view.tag == it.assignedObject.tag }
                    if (checkSenderReceiver(sender, receiver)) {
                        val localConfig = configs[sender!!.assignedObject.tag, receiver!!.assignedObject.tag]
                        localConfig?.onDropped?.invoke(sender.assignedObject, receiver.assignedObject)
                            ?: defaultConfig.onDropped?.invoke(sender.assignedObject, receiver.assignedObject)
                    }

                }

                DragEvent.ACTION_DRAG_ENTERED -> {
                    val sender = senders.find { event.clipDescription.label == it.assignedObject.tag }
                    val receiver = receivers.find { view.tag == it.assignedObject.tag }
                    if (checkSenderReceiver(sender, receiver)) {
                        val localConfig = configs[sender!!.assignedObject.tag, receiver!!.assignedObject.tag]
                        localConfig?.onDragEntered?.invoke(view)
                            ?: defaultConfig.onDragEntered?.invoke(view)
                    }

                }

                DragEvent.ACTION_DRAG_EXITED -> {
                    val sender = senders.find { event.clipDescription.label == it.assignedObject.tag }
                    val receiver = receivers.find { view.tag == it.assignedObject.tag }
                    if (checkSenderReceiver(sender, receiver)) {
                        val localConfig = configs[sender!!.assignedObject.tag, receiver!!.assignedObject.tag]
                        localConfig?.onDragExited?.invoke(view)
                            ?: defaultConfig.onDragExited?.invoke(view)
                    }
                }

                DragEvent.ACTION_DRAG_STARTED -> {
                    lastTag = event.clipDescription.label.toString()
                    senders
                        .find { it.assignedObject.tag == lastTag }
                        ?.let {
                            defaultConfig.onSenderDragStart?.invoke(it)
                        }
                }

                DragEvent.ACTION_DRAG_ENDED -> {
                    val action = configs[lastTag!!, view.tag as String]?.onDragExited ?: defaultConfig.onDragExited
                    action?.invoke(view)
                    senders
                        .find { it.assignedObject.tag == lastTag }
                        ?.let {
                            defaultConfig.onSenderDragStop?.invoke(it)
                        }
                }

            }
            true
        }
        receivers.forEach {
            it.view.tag = it.assignedObject.tag
            it.view.setOnDragListener(onDragListener)
        }
    }

    private fun prepareSenders() {
        val dragFlags = defaultConfig.dragFlags
        val sbBuilder = defaultConfig.shadowBuilder

        val action: (View, S) -> Boolean = { view, obj ->
            val item = ClipData.Item("DragAndDrop" as CharSequence)
            val dragData = ClipData(obj.tag, arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN), item)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                view.startDragAndDrop(dragData, sbBuilder(view, obj), null, dragFlags)
            } else {
                view.startDrag(dragData, sbBuilder(view, obj), null, dragFlags)
            }
            true

        }

        when (defaultConfig.userAction) {
            UserAction.TOUCH -> senders.forEach { it.setOnTouchListener(action) }

            UserAction.LONG_CLICK -> senders.forEach { it.setOnLongClickListener(action) }
        }


    }

}

