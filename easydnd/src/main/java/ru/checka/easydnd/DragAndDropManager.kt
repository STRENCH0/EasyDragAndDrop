package ru.checka.easydnd

import android.content.ClipData
import android.content.ClipDescription
import android.view.DragEvent
import android.view.View


/**
 * Main class of DragAndDrop
 */
class DragAndDropManager<S, R> {

    companion object {
        private const val DRAG_AND_DROP_CLIP = "DragAndDrop"
    }

    private var senders: MutableSet<DragAndDropObject<S>> = mutableSetOf()
    private var receivers: MutableSet<DragAndDropObject<R>> = mutableSetOf()
    private val configs =
        SenderToReceiverActions<String, String, DragAndDropLocalConfigInternal<S, R>>()

    val defaultConfig: DragAndDropDefaultConfig<S, R> = DragAndDropDefaultConfig()
    /**
     * Maps set of sender objects to receivers
     * @param newSenders senders
     * @param newReceivers receivers
     * @param localConfig overriding some default behavior for those pairs
     */
    fun mapSets(
        newSenders: Set<DragAndDropObject<S>>,
        newReceivers: Set<DragAndDropObject<R>>,
        localConfig: DragAndDropLocalConfig<S, R>? = null
    ) {

        //save all mappings
        senders.addAll(newSenders)
        receivers.addAll(newReceivers)

        for (sender in newSenders) {
            val senderTag = sender.tag
            for (receiver in newReceivers) {
                val receiverTag = receiver.tag
                configs[receiverTag, senderTag] =
                    DragAndDropLocalConfigInternal(localConfig, defaultConfig)
            }
        }
    }

    /**
     * Start drag-and-drop
     */
    fun applyDragAndDrop() {
        prepareSenders()
        prepareReceivers()
    }

    /**
     * Disable drag-and-drop
     */
    fun disable() {
        val emptyAction: (View, S, String) -> Boolean = { _, _, _ -> true }
        for (sender in senders) {
            if (defaultConfig.userAction == UserAction.TOUCH) {
                sender.setOnTouchListener(emptyAction)
            }
            if (defaultConfig.userAction == UserAction.LONG_CLICK) {
                sender.setOnLongClickListener(emptyAction)
            }
        }
        for (receiver in receivers) {
            receiver.view.setOnDragListener { _, _ -> true }
        }
    }


    private fun prepareReceivers() {
        receivers.forEach {
            it.view.tag = it.tag
            it.view.setOnDragListener(DndListener())
        }
    }

    private fun prepareSenders() {
        val dragFlags = defaultConfig.dragFlags
        val sbBuilder = defaultConfig.shadowBuilder

        val action: (View, S, String) -> Boolean = { view, obj, tag ->
            val item = ClipData.Item(DRAG_AND_DROP_CLIP as CharSequence)
            val dragData = ClipData(tag, arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN), item)
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

    private inner class DndListener : View.OnDragListener {

        private var lastTag: String? = null

        override fun onDrag(view: View, event: DragEvent): Boolean {
            when (event.action) {
                DragEvent.ACTION_DROP -> handleActionDrop(event, view)
                DragEvent.ACTION_DRAG_ENTERED -> handleActionDragEnter(event, view)
                DragEvent.ACTION_DRAG_EXITED -> handleActionDragExit(event, view)
                DragEvent.ACTION_DRAG_STARTED -> handleActionDragStart(event)
                DragEvent.ACTION_DRAG_ENDED -> handleActionDragEnd(view)
            }
            return true
        }

        private fun handleActionDragEnd(view: View) {
            senders
                .find { it.tag == lastTag }
                ?.let {
                    defaultConfig.onSenderDragStop?.invoke(it)
                }
        }

        private fun handleActionDragStart(event: DragEvent) {
            lastTag = event.clipDescription.label.toString()
            senders
                .find { it.tag == lastTag }
                ?.let {
                    defaultConfig.onSenderDragStart?.invoke(it)
                }
        }

        private fun handleActionDragExit(event: DragEvent, view: View) {
            val receiverTag = view.tag
            if (receiverTag is String) {
                val config =
                    configs[receiverTag, event.clipDescription.label.toString()]
                config?.onDragExited?.invoke(view)
            }
        }

        private fun handleActionDragEnter(event: DragEvent, view: View) {
            val receiverTag = view.tag
            if (receiverTag is String) {
                val config =
                    configs[receiverTag, event.clipDescription.label.toString()]
                config?.onDragEntered?.invoke(view)
            }
        }

        private fun handleActionDrop(event: DragEvent, view: View) {
            val receiverTag = view.tag
            if (receiverTag is String) {
                val config = configs[receiverTag, event.clipDescription.label.toString()]
                config
                    ?.onDropped
                    ?.let { onDropped ->
                        val sender = senders.find { event.clipDescription.label == it.tag }
                        val receiver = receivers.find { receiverTag == it.tag }
                        if (sender != null && receiver != null) {
                            onDropped.invoke(sender.assignedObject, receiver.assignedObject)
                        }
                    }
            }
        }


    }

}

