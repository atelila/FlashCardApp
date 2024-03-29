package es.uam.eps.dadm.cards
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent

class BoardView(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        strokeWidth = 2f
        setStyle(Paint.Style.STROKE)
    }
    private var path = Path()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path, linePaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val pointX = event.x
        val pointY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN ->
                // Comienza una línea nueva
                path.moveTo(pointX, pointY)
            MotionEvent.ACTION_MOVE ->
                // Dibuja una línea entre el anterior punto y (pointX, pointY)
                path.lineTo(pointX, pointY)
            else -> return false
        }

        postInvalidate()
        return true
    }
}