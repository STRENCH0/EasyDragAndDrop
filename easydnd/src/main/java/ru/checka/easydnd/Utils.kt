package ru.checka.easydnd

import android.view.View

internal class SenderToReceiverActions<A> {

    private val map = mutableMapOf<String, MutableMap<String, A>>()

    operator fun get(senderTag: String, receiverTag: String): A? {
        val sendersAction = map[receiverTag]
        if (sendersAction != null) {
            return sendersAction[senderTag]
        }
        return null
    }

    operator fun set(senderTag: String, receiverTag: String, config: A) {
        val sendersAction = map[receiverTag]
        if (sendersAction == null) {
            map[receiverTag] = mutableMapOf()
        }
        map[receiverTag]!![senderTag] = config
    }

    fun clear() = map.clear()

}

internal fun <T> DragAndDropObject<T>.setOnTouchListener(action: (View, T, String) -> Boolean) {
    this.view.setOnTouchListener { v, _ -> action(v, this.assignedObject, tag) }
}


internal fun <T> DragAndDropObject<T>.setOnLongClickListener(action: (View, T, String) -> Boolean) {
    this.view.setOnLongClickListener { v -> action(v, this.assignedObject, tag) }
}

