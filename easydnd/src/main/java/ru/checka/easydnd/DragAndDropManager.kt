package ru.checka.easydnd

import android.content.ClipData
import android.content.ClipDescription
import android.view.DragEvent
import android.view.View


/**
 * Main class of DragAndDrop
 */
class DragAndDropManager<S, R> {

    private var senders: MutableSet<DragAndDropObject<S>> = mutableSetOf()
    private var receivers: MutableSet<DragAndDropObject<R>> = mutableSetOf()
    private val receiverSenderMap = mutableMapOf<String, MutableSet<String>>()
    private val configs = SenderToReceiverActions<DragAndDropLocalConfig<S, R>>()

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
        for (receiver in newReceivers) {
            for (sender in newSenders) {
                val receiverTag = receiver.tag
                val senderTag = sender.tag
                receiverSenderMap
                    .getOrPut(receiverTag, ::mutableSetOf)
                    .add(senderTag)
            }
        }
        //save custom config
        if (localConfig != null) {
            for (sender in newSenders) {
                val senderTag = sender.tag
                for (receiver in newReceivers) {
                    val receiverTag = receiver.tag
                    configs[senderTag, receiverTag] = localConfig
                }
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
            val item = ClipData.Item("DragAndDrop" as CharSequence)
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
            val action = configs[lastTag!!, view.tag as String]?.onDragExited
                ?: defaultConfig.onDragExited
            action?.invoke(view)
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
            val sender =
                senders.find { event.clipDescription.label == it.tag }
            val receiver = receivers.find { view.tag == it.tag }
            if (checkSenderReceiverMapping(sender, receiver)) {
                val localConfig = configs[sender!!.tag, receiver!!.tag]
                localConfig?.onDragExited?.invoke(view)
                    ?: defaultConfig.onDragExited?.invoke(view)
            }
        }

        private fun handleActionDragEnter(event: DragEvent, view: View) {
            val sender =
                senders.find { event.clipDescription.label == it.tag }
            val receiver = receivers.find { view.tag == it.tag }
            if (checkSenderReceiverMapping(sender, receiver)) {
                val localConfig = configs[sender!!.tag, receiver!!.tag]
                localConfig?.onDragEntered?.invoke(view)
                    ?: defaultConfig.onDragEntered?.invoke(view)
            }
        }

        private fun handleActionDrop(event: DragEvent, view: View) {
            val sender =
                senders.find { event.clipDescription.label == it.tag }
            val receiver = receivers.find { view.tag == it.tag }
            if (checkSenderReceiverMapping(sender, receiver)) {
                val localConfig = configs[sender!!.tag, receiver!!.tag]
                localConfig?.onDropped?.invoke(
                    sender.assignedObject,
                    receiver.assignedObject
                ) ?: defaultConfig
                    .onDropped
                    ?.invoke(sender.assignedObject, receiver.assignedObject)
            }
        }

        /**
         * Checks defaultConfig, sender and receiver on null and (sender -> receiver) mapping existing
         */
        private fun checkSenderReceiverMapping(
            sender: DragAndDropObject<S>?,
            receiver: DragAndDropObject<R>?
        ): Boolean {
            if (sender == null || receiver == null)
                return false
            //if object drops not on self or it's enabled
            if (defaultConfig.selfDrop || sender.tag != receiver.tag) {
                return receiverSenderMap[receiver.tag]
                    ?.contains(sender.tag)
                    ?: false
            }
            return false
        }

    }

}

