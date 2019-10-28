package ru.checka.easydnd

import android.view.View

internal class SenderToReceiverActions<K, V, T> {

    private val map = mutableMapOf<K, MutableMap<V, T>>()

    operator fun get(receiverTag: K, senderTag: V): T? {
        val sendersAction = map[receiverTag]
        if (sendersAction != null) {
            return sendersAction[senderTag]
        }
        return null
    }

    operator fun set(receiverTag: K, senderTag: V, config: T) {
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

