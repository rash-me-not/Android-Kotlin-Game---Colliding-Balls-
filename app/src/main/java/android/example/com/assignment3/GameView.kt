package android.example.com.assignment3

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View;
import android.widget.Toast


class GameView : View, View.OnTouchListener {

    private var currLifecycleState: GameState? = null
    private var isPauseState = false
    private lateinit var updateViewListener: UpdateViewListener
    private var gameStateName: String = "START"
    private var pauseState: String = "PAUSE"

    constructor(context: Context) : super(context) {}
    constructor(context: Context, attributes: AttributeSet) : super(context, attributes) {
        setOnTouchListener(this)

//         Initializing current state to Draw State. Here user can draw white circles on canvas
        currLifecycleState = DrawState(this)
    }

//         Invoking OnDraw and OnTouch events of Current Lifecycle State
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        currLifecycleState?.onDraw(canvas)
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        Log.i("c", event.toString())
        if (currLifecycleState != null)
            return (currLifecycleState as GameState).handleTouch(view, event)
        return true
    }

    fun updateState() {

//        User can move to PlayState for playing the game, from the DrawState.
//        The button text changes to 'END' as user is allowed to end the game while playing
        if (currLifecycleState is DrawState) {
            (currLifecycleState as DrawState).scheduler.shutdown()
            val circles = (currLifecycleState as DrawState).getCircles()
            currLifecycleState =
                PlayState(this, width.toFloat(), height.toFloat(), circles, updateViewListener, isPauseState)
            gameStateName = "END"
            invalidate()

//        User can move to NewState to get a New screen, from the PlayState.
//        The button text changes is 'NEW' as user is allowed to go to the new screen to start drawing on canvas
        } else if (currLifecycleState is PlayState) {
            (currLifecycleState as PlayState).scheduler.shutdown()
            currLifecycleState = NewState()
            isPauseState = false
            gameStateName = "NEW"
            pauseState = "PAUSE"
            updateViewListener.resetViewButtons()

//        User can move to DrawState to draw on canvas, from the NewState.
//        The button text changes is 'START' as user is allowed to start the game after drawing on canvas
        } else if (currLifecycleState is NewState) {
            currLifecycleState = DrawState(this)
            gameStateName = "START"
        }
        invalidate()
    }

//    Setting a Listener to inform the Main Activity for updating the view
    fun setListener(updateViewListener: UpdateViewListener) {
        this.updateViewListener = updateViewListener
    }

//    Update the pause or resume state based on the current state. Send the Pause/Resume value to Play State so the user is allowed to pause while playing
    fun setPauseOrResume() {
        Log.i("pause?: ", isPauseState.toString())
        if (currLifecycleState is PlayState) {
            if (isPauseState) {
                isPauseState = false
                pauseState = "PAUSE"
                invalidate()
            } else {
                isPauseState = true
                pauseState = "RESUME"
                Toast.makeText(context, "Game Paused", Toast.LENGTH_SHORT).show()
            }
            (currLifecycleState as PlayState).pauseState = isPauseState
        }
    }

    fun getStateName(): String {
        return gameStateName
    }

    fun getPauseState(): String {
        return pauseState
    }
}