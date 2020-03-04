package android.example.com.assignment3
import android.graphics.Canvas;
import android.view.MotionEvent
import android.view.View

interface GameState {

    fun onDraw(canvas: Canvas);

    fun handleTouch(view: View, event: MotionEvent) : Boolean
}