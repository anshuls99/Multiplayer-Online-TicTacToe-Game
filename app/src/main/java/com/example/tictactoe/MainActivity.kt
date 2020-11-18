package com.example.tictactoe

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var activePlayer = 1

    var player1 = ArrayList<Int>()
    var player2 = ArrayList<Int>()

    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.reference

    private var myEmail: String? = null

    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        myEmail = intent.extras!!.getString("email")
        resetGame()
        incomingCalls()
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

        myRef.child("PlayerOnline").child(sessionId!!).child(cellId.toString()).setValue(myEmail)
    }


    private fun playGame(cellId: Int, buSelected: Button) {

        if (activePlayer == 1) {
            buSelected.text = "X"
            buSelected.setBackgroundResource(R.color.green)
            player1.add(cellId)
        } else {
            buSelected.text = "O"
            buSelected.setBackgroundResource(R.color.red)
            player2.add(cellId)
        }

        buSelected.isEnabled = false

        checkWinner()
    }

    fun autoPlay(cellId: Int) {

        val buSelected: Button? = when (cellId) {
            1 -> bu1
            2 -> bu2
            3 -> bu3
            4 -> bu4
            5 -> bu5
            6 -> bu6
            7 -> bu7
            8 -> bu8
            else -> bu9
        }

        playGame(cellId, buSelected!!)
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

        myRef.child("PlayerOnline").removeValue()

    }


    fun buRequestEvent(view: View) {
        val userEmail = etEmail.text.toString()
        myRef.child("Users").child(userEmail.split("@")[0]).child("Request").push()
            .setValue(myEmail)

        playerOnline(myEmail!!.split("@")[0] + userEmail.split("@")[0])
        playerSymbol = "X"
        Toast.makeText(this, "Request Sent", Toast.LENGTH_LONG).show()
    }

    fun buAcceptEvent(view: View) {
        val userEmail = etEmail.text.toString()
        myRef.child("Users").child(userEmail.split("@")[0]).child("Request").push()
            .setValue(myEmail)

        playerOnline(userEmail.split("@")[0] + myEmail!!.split("@")[0])
        playerSymbol = "O"
        Toast.makeText(this, "Game Started", Toast.LENGTH_LONG).show()
    }

    var sessionId: String? = null
    var playerSymbol: String? = null

    private fun playerOnline(sessionId: String) {
        this.sessionId = sessionId
        myRef.child("PlayerOnline").removeValue()
        myRef.child("PlayerOnline").child(sessionId)
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val td = snapshot.value as HashMap<String, Any>
                        for (key in td.keys) {
                            val value = td[key] as String
                            activePlayer = if (value != myEmail) {
                                if (playerSymbol === "X") 1 else 2
                            } else {
                                if (playerSymbol === "X") 2 else 1
                            }

                            autoPlay(key.toInt())
                        }
                    } catch (ex: Exception) {
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    var number = 0

    private fun incomingCalls() {
        myRef.child("Users").child(myEmail!!.toString().split("@")[0]).child("Request")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val td = snapshot.value as HashMap<*, *>
                        for (key in td.keys) {
                            val value = td[key] as String
                            etEmail.setText(value)

                            val notify = Notification()
                            notify.notify(
                                applicationContext,
                                value + "wants to play with you",
                                number
                            )
                            number++
                            myRef.child("Users").child(myEmail!!.toString().split("@")[0])
                                .child("Request").setValue(true)
                            break
                        }
                    } catch (ex: Exception) {
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

}