package au.edu.swin.sdmd.w03_calculations

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private var score = 0
    private var currentHold = 0
    private var holdColor = R.color.black  // Default color for no holds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Apply insets for edge-to-edge experience
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val welcomeMessage = getString(R.string.welcome_message)

        val scoreTextView = findViewById<TextView>(R.id.scoreTextView)
        val climbButton = findViewById<Button>(R.id.climbButton)
        val fallButton = findViewById<Button>(R.id.fallButton)
        val resetButton = findViewById<Button>(R.id.resetButton)

        // Restore state if available
        if (savedInstanceState != null) {
            score = savedInstanceState.getInt("score")
            currentHold = savedInstanceState.getInt("currentHold")
            holdColor = savedInstanceState.getInt("holdColor", R.color.black)
            updateScoreDisplay(scoreTextView)
        }

        climbButton.setOnClickListener {
            if (currentHold < 12) {  // Allow climbing only up to hold 12
                currentHold++
                score += getPointsForHold(currentHold)
                score = minOf(score, 18) // Ensure score does not exceed 18
                holdColor = getColorForHold(currentHold) // Update color for current hold
                updateScoreDisplay(scoreTextView)
                Log.d("ClimbApp", "Climber advanced to hold $currentHold with score $score")
            }
        }

        fallButton.setOnClickListener {
            if (currentHold in 1..12) {
                if (score < 9) {
                    if (currentHold <= 9) {
                        score = maxOf(0, score - 3) // Deduct 3 points if falling from hold 1-8
                    }
                    holdColor = getColorForHold(currentHold) // Change color to the current hold's color
                    updateScoreDisplay(scoreTextView)
                    Log.d("ClimbApp", "Climber fell at hold $currentHold with score $score")
                } else {
                    Log.d("ClimbApp", "Fall action not allowed as score is $score")
                }
            }
        }

        resetButton.setOnClickListener {
            score = 0
            currentHold = 0
            holdColor = R.color.black // Reset color to default
            updateScoreDisplay(scoreTextView)
            Log.d("ClimbApp", "Score reset")
        }
    }

    private fun getPointsForHold(hold: Int): Int {
        return when (hold) {
            in 1..3 -> 1 // Blue zone
            in 4..6 -> 2 // Green zone
            in 7..9 -> 3 // Red zone
            else -> 0
        }
    }
    private fun getColorForHold(hold: Int): Int {
        return when (hold) {
            in 1..3 -> R.color.blue  // Blue zone
            in 4..6 -> R.color.green // Green zone
            in 7..18 -> R.color.red   // Red zone
            else -> R.color.black    // Default color (if below hold 1)
        }
    }


    private fun updateScoreDisplay(scoreTextView: TextView) {
        // Update the score text
        scoreTextView.text = "Score: $score"

        // Determine the color based on the current score
        holdColor = getColorForScore(score)

        // Print the color for debugging
        val color = ContextCompat.getColor(this, holdColor)
        Log.d("ClimbApp", "Updating score display with color: $color")

        // Set the text color based on the stored holdColor
        scoreTextView.setTextColor(color)
    }
    private fun getColorForScore(score: Int): Int {
        return when (score) {
            in 1..3 -> R.color.blue  // Blue zone
            in 4..6 -> R.color.green // Green zone
            in 7..18 -> R.color.red   // Red zone
            else -> R.color.black    // Default color (if below hold 1)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("score", score)
        outState.putInt("currentHold", currentHold)
        outState.putInt("holdColor", holdColor)
    }

}