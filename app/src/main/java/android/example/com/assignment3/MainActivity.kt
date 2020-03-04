package android.example.com.assignment3

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), UpdateViewListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        gameView.setListener(this)
        gameStateButton.text = gameView.getStateName()

//        Controlling the Pause/Resume state of game, with help of the pauseButton click
        pauseButton.setOnClickListener {
            gameView.setPauseOrResume()
            pauseButton.text = gameView.getPauseState()
        }

//        Controlling different states of the Game (i.e. Drawing, Playing or New/Clear screen state) with help of gameStateButton click
        gameStateButton.setOnClickListener() {
            gameView.updateState()
            gameStateButton.text = gameView.getStateName()

//            When the player is ready to start a new game, the default score gets updated to 0, and lives get updated to 3
            if (gameView.getStateName() == "START") {
                scoreVal.text = getString(R.string.scoreVal)
                livesVal.text = getString(R.string.livesVal)
            }
        }
    }

//    Whenever the functions from the UpdateViewListener interface are invoked, the MainActivity performs the following tasks:
//          1. Update the Score Value Text Field
//          2. Update the Lives Value Text Field
//          3. Update the Game State when the player presses End Game
//          4. Reset the Score and Lives Text fields to default value in case of an End Game

    override fun updateScore(score: Int) {
        scoreVal.text = score.toString()
    }

    override fun updateLives(lives: Int) {
        livesVal.text = lives.toString()
    }

    override fun endGame() {
        gameView.updateState()
        resetViewButtons()
    }

    override fun resetViewButtons() {
        gameStateButton.text = gameView.getStateName()
        pauseButton.text = gameView.getPauseState()
    }
}