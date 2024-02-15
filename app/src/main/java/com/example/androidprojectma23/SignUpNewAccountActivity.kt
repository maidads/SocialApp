package com.example.androidprojectma23

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore


class SignUpNewAccountActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    //private lateinit var newUsernameEditText: EditText
    //private lateinit var newPasswordEditText: EditText
    private lateinit var signUpButton: Button
    private lateinit var usernameTextInputLayout: TextInputLayout
    private lateinit var passwordTextInputLayout: TextInputLayout
    private lateinit var newUsernameEditText: TextInputEditText
    private lateinit var newPasswordEditText: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = Firebase.auth
        //newUsernameEditText = findViewById(R.id.usernameEditText)
        //newPasswordEditText = findViewById(R.id.passwordEditText)
        signUpButton = findViewById(R.id.signUpButton)

        usernameTextInputLayout = findViewById(R.id.outlinedTextField)
        passwordTextInputLayout = findViewById(R.id.outlinedTextField2)
        newUsernameEditText = usernameTextInputLayout.editText as TextInputEditText
        newPasswordEditText = passwordTextInputLayout.editText as TextInputEditText

        signUpButton.setOnClickListener {
            signUp()
        }
    }

    private fun signUp() {
        val username = newUsernameEditText.text.toString()
        val password = newPasswordEditText.text.toString()

        /*
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Snälla välj både användarnamn och lösenord.", Toast.LENGTH_SHORT).show()
            return
        }
         */

        var isFormValid = true

        if (username.isEmpty()) {
            usernameTextInputLayout.error = "Användarnamn krävs."
            isFormValid = false
        } else {
            usernameTextInputLayout.error = null
        }

        if (password.isEmpty()) {
            passwordTextInputLayout.error = "Lösenord krävs."
            isFormValid = false
        } else {
            passwordTextInputLayout.error = null
        }

        if (!isFormValid) return

        auth.createUserWithEmailAndPassword(username,password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    saveUserData()
                } else {
                    Toast.makeText(this, "Registrering misslyckades.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                when (exception) {
                    is FirebaseAuthWeakPasswordException -> Toast.makeText(this, "Lösenordet är för svagt.", Toast.LENGTH_SHORT).show()
                    is FirebaseAuthUserCollisionException -> Toast.makeText(this, "Ett konto med denna email finns redan.", Toast.LENGTH_SHORT).show()
                    else -> Toast.makeText(this, "Ett oväntat fel inträffade: ${exception.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserData() {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val userData = hashMapOf(
                "userID" to user.uid,
                "Username" to newUsernameEditText.text.toString()
            )

            val db = Firebase.firestore
            db.collection("users").document(user.uid).set(userData)
                .addOnSuccessListener {
                    navigateToProfileCreationStep1(user.uid)
                }
        }
    }

    private fun navigateToProfileCreationStep1(userId: String) {
        val fragment = ProfileCreationStep1Fragment.newInstance(userId)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
