package ru.checka.easydnd

import android.view.View

@ConfigMarker
interface ChildOnDropped<S, R> {
    fun callSuper(sender: S, receiver: R)
}

@ConfigMarker
interface ChildOnDrag {
    fun callSuper(view: View)
}

@ConfigMarker
interface ChildOnDragLocation {
    fun callSuper(view: View, x: Float, y: Float)
}

