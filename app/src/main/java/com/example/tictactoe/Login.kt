package com.example.tictactoe

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*


class Login : AppCompatActivity() {

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth!!.currentUser
        if(currentUser!=null)
            loadMain(currentUser)
    }

    private var mAuth: FirebaseAuth? = null
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("message")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()

        // Write a message to the database

        // Write a message to the database


    }

    fun buLoginEvent() {
        loginToFirebase(etEmail.text.toString(), etPassword.text.toString())
    }

    private fun loginToFirebase(email: String, password: String) {
        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = mAuth!!.currentUser
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                    myRef.child("Users").child((user!!.email.toString().split("@"))[0]).setValue(user.email)
                    loadMain(user)
                } else {
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }

            }
    }

    private fun loadMain(currentUser: FirebaseUser) {

        myRef.child("PlayerOnline").removeValue()

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("email", currentUser.email)
        intent.putExtra("uid", currentUser.uid)
        startActivity(intent)
        finish()
    }


}