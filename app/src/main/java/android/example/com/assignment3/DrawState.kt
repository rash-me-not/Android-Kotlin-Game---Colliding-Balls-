package android.example.com.assignment3

import android.graphics.Canvas;
import android.graphics.Paint
import android.util.Log
import android.view.MotionEvent
import android.view.View;
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import android.graphics.Color

class DrawState(private val view: View) : GameState {

    var paint = Paint()
    val scheduler = Executors.newScheduledThreadPool(1)
    private val circleList = ArrayList<Circle>()
    var task: ScheduledFuture<*>? = null

    //    Keeping track of the attributes of every White Circle
    class Circle constructor(
        var centerX: Float,
        var centerY: Float,
        var radius: Float,
        var isCollided: Boolean,
        var isScored: Boolean
    )

    fun getCircles(): ArrayList<Circle> {
        return circleList
    }

    //    Drawing the White Circle on Canvas
    override fun onDraw(canvas: Canvas) {
        for (circle in circleList) {
            paint.color = Color.parseColor("#000000")
            paint.strokeWidth = 10f
            paint.style = Paint.Style.STROKE
            canvas.drawCircle(circle.centerX, circle.centerY, circle.radius, paint)
        }
    }

    //    Schedule to increase the radius of the white circle on pressing the screen
    fun startSchedule(circlePoint: Circle) {
        task = scheduler.scheduleAtFixedRate(
            { increaseRadius(circlePoint) },
            0, 10, TimeUnit.MILLISECONDS
        )
    }

    fun stopSchedule() {
        Log.i("s", "stopping schedule")
        task?.cancel(true)
    }

    fun increaseRadius(circlePoint: Circle) {
        circlePoint.radius = circlePoint.radius + 5f
        Log.i("i", "invalidating")
        view.invalidate()
    }

    //      A circle is created everytime a user touches a point on the screen. The radius of each circle keeps growing till the user lifts the finger
    override fun handleTouch(view: View, event: MotionEvent): Boolean {
        Log.i("c", event.toString())
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                Log.i("down", "Action down")
                var p = Circle(event.getX(), event.getY(), 50f, false, false)
                circleList.add(p)
                startSchedule(p)
                return true;
            }
            MotionEvent.ACTION_UP -> {
                Log.i("up", "Action Up")
                stopSchedule()
                return true
            }
        }
        return false
    }
}
