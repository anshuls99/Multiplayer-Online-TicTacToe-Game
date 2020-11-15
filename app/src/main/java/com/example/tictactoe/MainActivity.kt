package com.example.tictactoe

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics

class MainActivity : AppCompatActivity() {

    var activePlayer = 1

    var player1 = ArrayList<Int>()
    var player2 = ArrayList<Int>()

    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    fun buClick(view: View) {

        val buSelected = view as Button

        val cellId = when (buSelected.id) {
            R.id.bu1 -> 1
            R.id.bu2 -> 2
            R.id.bu3 -> 3
            R.id.bu4 -> 4
            R.id.bu5 -> 5
            R.id.bu6 -> 6
            R.id.bu7 -> 7
            R.id.bu8 -> 8
            else -> 9
        }
        playGame(cellId, buSelected)
    }


    private fun playGame(cellId: Int, buSelected: Button) {

        if (activePlayer == 1) {
            buSelected.text = "X"
            buSelected.setBackgroundResource(R.color.green)
            player1.add(cellId)
            activePlayer = 2
        } else {
            buSelected.text = "0"
            buSelected.setBackgroundResource(R.color.red)
            player2.add(cellId)
            activePlayer = 1
        }

        buSelected.isEnabled = false

        checkWinner()
    }

    private fun checkWinner() {
        val winningPos = arrayOf(
                arrayOf(1, 2, 3),
                arrayOf(4, 5, 6),
                arrayOf(7, 8, 9),
                arrayOf(1, 4, 7),
                arrayOf(2, 5, 8),
                arrayOf(3, 6, 9),
                arrayOf(1, 5, 9),
                arrayOf(3, 5, 7)
        )

        for (i in winningPos.indices) {
            var p1 = 0
            var p2 = 0
            for (j in 0..2) {
                if (player1.contains(winningPos[i][j]))
                    p1++
                else if (player2.contains(winningPos[i][j]))
                    p2++
            }
            if (p1 == 3) {
                Toast.makeText(this, "Player 1 won", Toast.LENGTH_LONG).show()
                resetGame()
                break
            }
            if (p2 == 3) {
                Toast.makeText(this, "Player 2 won", Toast.LENGTH_LONG).show()
                resetGame()
                break
            }
        }

    }

    private fun resetGame() {
        activePlayer = 1
        player1.clear()
        player2.clear()

        for (i in 1..9) {
            val selected: Button? = when (i) {
                1 -> findViewById(R.id.bu1)
                2 -> findViewById(R.id.bu2)
                3 -> findViewById(R.id.bu3)
                4 -> findViewById(R.id.bu4)
                5 -> findViewById(R.id.bu5)
                6 -> findViewById(R.id.bu6)
                7 -> findViewById(R.id.bu7)
                8 -> findViewById(R.id.bu8)
                else -> findViewById(R.id.bu9)
            }

            selected!!.isEnabled = true
            selected.text = ""
            selected.setBackgroundResource(R.color.purple_200)
        }

    }

    fun buRequestEvent(view: View) {}
    fun buAcceptEvent(view: View) {}


}