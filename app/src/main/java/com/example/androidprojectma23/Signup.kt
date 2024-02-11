package com.example.androidprojectma23

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore


class Signup : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var newUsernameEditText: EditText
    private lateinit var newPasswordEditText: EditText
    private lateinit var signUpButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = Firebase.auth
        newUsernameEditText = findViewById(R.id.usernameEditText)
        newPasswordEditText = findViewById(R.id.passwordEditText)
        signUpButton = findViewById(R.id.signUpButton)

        signUpButton.setOnClickListener {
            signUp()
        }
    }

    private fun signUp() {
        val username = newUsernameEditText.text.toString()
        val password = newPasswordEditText.text.toString()

        if (username.isEmpty() || password.isEmpty()) {
            return
        }
        auth.createUserWithEmailAndPassword(username,password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    saveUserData()
                }
            }
    }

    private fun saveUserData() {
        val user = hashMapOf(
            "Username" to newUsernameEditText.text.toString()
        )

        val db = Firebase.firestore
        val currentUser = auth.currentUser

        currentUser?.let {
            db.collection("users").document(it.uid).set(user)
                .addOnSuccessListener {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
        }
    }
}
