package android.example.com.assignment3

interface UpdateViewListener {

    fun updateScore(score: Int);

    fun updateLives(lives: Int);

    fun endGame();

    fun resetViewButtons();
}