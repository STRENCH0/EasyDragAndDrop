package ru.checka.easydnd

import android.view.View

interface DragAndDropObject<out T : DragAssignment> {
    val view: View

    val assignedObject: T
}

interface DragAssignment {
    val tag: String
}