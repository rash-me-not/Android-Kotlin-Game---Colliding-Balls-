package android.example.com.assignment3

import java.util.concurrent.TimeUnit

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import kotlin.math.sqrt

class PlayState(
    private val view: View,
    var width: Float,
    var height: Float,
    var circles: ArrayList<DrawState.Circle>,
    val updateViewListener: UpdateViewListener,
    var pauseState: Boolean
) : GameState {


    var xCord = width / 2
    var yCord = height / 2
    val radius = 50f
    var ballSpeed = 5f
    val ballMaxSpeed = 35f
    val scheduler = Executors.newScheduledThreadPool(1)
    var task: ScheduledFuture<*>? = null
    var paint = Paint()
    var score = 0
    var lives = 3
    var rangeY = 0f
    var ballsCrossedBottomCount = 0


    //    Keeping track of the Max and Min values of the Y Coordinates of the Centres of all white circles on canvas.
    //    The difference of the height between Max-Min value of centers, will help us to keep track that all the white circles have passed the bottom of screen.
    init {
        var maxY = Float.MIN_VALUE
        var minY = Float.MAX_VALUE
        for (circle in circles) {
            if (circle.centerY > maxY) {
                maxY = circle.centerY
            }
            if (circle.centerY < minY) {
                minY = circle.centerY
            }
            rangeY = maxY - minY
            Log.i("rangeY", rangeY.toString())
        }
    }

    //    Direction to move the black ball in Left or Right Direction
    enum class Direction {
        LEFT, RIGHT
    }

    //    Whenever the user touches the screen, the black ball moves in left/right direction until the user lifts the finger up
    override fun handleTouch(view: View, event: MotionEvent): Boolean {
        if (!pauseState) {
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_DOWN -> {
                    startSchedule(event.x)
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    stopSchedule()
                    return true
                }
                MotionEvent.ACTION_POINTER_DOWN -> {
                    if (isOppDirection(event.getX(0), event.getX(1))) {
                        stopSchedule()
                    }
                }
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {

//        Drawing black circle for the player with the updated values of X coordinates based on the direction user touches screen
        paint.color = Color.parseColor("#000000")
        paint.style = Paint.Style.FILL
        canvas.drawCircle(xCord, yCord, radius, paint)

//        Redrawing white circles based on the Ball speed
        for (circle in circles) {
            paint.color = Color.parseColor("#000000")
            paint.strokeWidth = 5f
            paint.style = Paint.Style.STROKE
            Log.i("rangeY", rangeY.toString())
            circle.centerY = circle.centerY + ballSpeed
            canvas.drawCircle(circle.centerX, circle.centerY, circle.radius, paint)

            if (!pauseState) {

//              Detecting collision of every circle and maintaining the score, if the collision or score for that particular circle is not tracked yet.
                if (!circle.isCollided && !circle.isScored) {
                    detectCollision(circle, xCord, yCord, radius)
                }

//              If the circle crosses the bottom of the screen, move the circle to the top of the screen at the distance 'rangeY' where
//              rangeY equals the relative distance between the circles of Max and Min height
                if (circle.centerY > height) {
                    circle.isCollided = false
                    circle.centerY = -rangeY
                    circle.isScored = false
                    ballsCrossedBottomCount += 1

//              If all the circles have crossed the bottom of screen, increase the speed of white circles
                    if (ballSpeed <= ballMaxSpeed && ballsCrossedBottomCount == circles.size) {
                        ballSpeed = ballSpeed * 1.25f
                        ballsCrossedBottomCount = 0
                    }
                    Log.i("ball speed", ballSpeed.toString())
                }
            }
        }
        if (!pauseState) {
            view.invalidate()
        }
    }

    private fun detectCollision(circle: DrawState.Circle, xBlack: Float, yBlack: Float, radBlack: Float) {

//        If the black and white circles collide, reduce the lives of the player, and update on the view
        if (sqrt((circle.centerX - xBlack) * (circle.centerX - xBlack) + (circle.centerY - yBlack) * (circle.centerY - yBlack)) < (radBlack + circle.radius)) {
            Log.i("collide", "Balls are colliding!!");
            lives = lives - 1
            updateViewListener.updateLives(lives)
            circle.isCollided = true

//            If lives is equal to 0, End the Game
            if (lives == 0) {
                Toast.makeText(view.context, "Game Over", Toast.LENGTH_SHORT).show()
                updateViewListener.endGame()

            }
        } else if (circle.centerY >= height) {

//            If the circle crosses the screen bottom, increase the score and update the view
            score = score + 1
            circle.isScored = true
            updateViewListener.updateScore(score)
        }
    }

    //    Scheduler to update the coordinates of the black circle based on touch
    fun startSchedule(xTouch: Float) {
        task = scheduler.scheduleAtFixedRate(
            { getNewCoord(xTouch) },
            0, 10, TimeUnit.MILLISECONDS
        )
    }

    fun stopSchedule() {
        Log.i("s", "stopping schedule")
        task?.cancel(true)
    }

    //    If the user presses right half of screen, move right by 10, else move left by 10
    fun getNewCoord(xTouch: Float) {
        if (xTouch > width / 2 && (xCord + radius) < width) {
            Log.i("r", "Right")
            xCord = xCord + 10
        }
        if (xTouch < width / 2 && (xCord - radius) > 0) {
            Log.i("r", "Left")
            xCord = xCord - 10
        }
        view.invalidate()
    }

    fun getDirection(x: Float): Direction {
        if (x > width / 2) {
            return Direction.RIGHT
        } else return Direction.LEFT
    }

    //    Check if two finger touches on the screen are in opposite or same direction. This will guide movement of the black ball.
    fun isOppDirection(x1: Float, x2: Float): Boolean {
        if (getDirection(x1) == getDirection(x2)) {
            return false
        }
        return true
    }

}