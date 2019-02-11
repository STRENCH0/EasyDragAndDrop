package ru.checka.easydnd

import android.view.View

/**
 * Basic interface for DragAndDrop
 */
interface DragAndDropObject<out T : DragAssignment> {
    /**
     * View which will be dragged
     */
    val view: View

    /**
     * Associated object with additional information
     */
    val assignedObject: T
}

/**
 * Every DragAndDrop receiver and sender must implement this interface. Property [tag] must be unique!
 */
interface DragAssignment {

    /**
     * Must be UNIQUE!
     */
    val tag: String
}