package android.example.com.assignment3

import android.graphics.Canvas
import android.view.MotionEvent
import android.view.View

class NewState : GameState {
    override fun onDraw(canvas: Canvas) {
    }

    override fun handleTouch(view: View, event: MotionEvent): Boolean {
        return false
    }

}