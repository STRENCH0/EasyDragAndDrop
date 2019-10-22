package ru.checka.easydnd

import android.view.View
import java.util.*

/**
 * Basic interface for DragAndDrop
 */
data class DragAndDropObject<T>(
    /**
     * View which will be dragged
     */
    val view: View,
    /**
     * Associated object with additional information
     */
    val assignedObject: T
) {
    val tag = UUID.randomUUID().toString()
}

