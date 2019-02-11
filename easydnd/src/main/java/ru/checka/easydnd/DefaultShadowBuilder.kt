package ru.checka.easydnd

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.view.View

internal class DefaultShadowBuilder(v: View) : View.DragShadowBuilder(v) {

    private var x: Float = 0f
    private var y: Float = 0f
    private var radius: Float = 0f

    override fun onProvideShadowMetrics(size: Point, touch: Point) {
        val width: Int = view.width

        val height: Int = view.height

        size.set(width, height)

        touch.set(width / 2, height / 2)

        x = width / 2f
        y = height / 2f
        radius = width / 2f
    }

    override fun onDrawShadow(canvas: Canvas) {
        val paint = Paint()
        paint.color = Color.GREEN
        canvas.drawCircle(x, y, radius, paint)
    }
}