package com.example.androidprojectma23

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.ktx.firestore


class LogInToAppActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerTextView : TextView
    private lateinit var googleSignInButton : ImageView
    private lateinit var googleSignInClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = Firebase.auth

        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.signUpButton)
        registerTextView = findViewById(R.id.registerButton)


        loginButton.setOnClickListener {
            logIn()
        }

        registerTextView.setOnClickListener {
            val intent1 = Intent(this, SignUpNewAccountActivity::class.java)
            startActivity(intent1)
            finish()
        }
        googleSignInButton = findViewById(R.id.google_icon)

        googleSignInButton.setOnClickListener {
            signInWithGoogle()
        }


        /*
                if (auth.currentUser != null) {         // kommer ihåg användaren, slipper logga in igen
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }

         */
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun logIn() {
        val username = usernameEditText.text.toString()
        val password = passwordEditText.text.toString()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Snälla skriv både användarnamn och lösenord.", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, LandingPageActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    handleLoginFailure(task.exception)
                }
            }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, 1000)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1000) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            Toast.makeText(this, "Inloggningen misslyckades, försök igen.", Toast.LENGTH_SHORT).show()
        }
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val isNewUser = task.result?.additionalUserInfo?.isNewUser ?: false

                    if (isNewUser) {
                        saveNewUserInfo()
                    }

                    val intent = Intent(this, LandingPageActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
    }
    private fun saveNewUserInfo() {

        val currentUser = auth.currentUser
        val user = hashMapOf(
            "Username" to auth.currentUser?.email,
            "userID" to currentUser?.uid

        )

        val db = Firebase.firestore
        auth.currentUser?.uid?.let { userId ->
            db.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener {
                    Toast.makeText(this, "Användarinformationen har sparats.", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Det gick inte att spara ny användarinformation.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun handleLoginFailure(exception: Exception?) {
        when (exception) {
            is FirebaseAuthInvalidCredentialsException -> {
                Toast.makeText(this, "Fel email eller lösenord.", Toast.LENGTH_SHORT).show()
            }
            is FirebaseAuthInvalidUserException -> {
                val errorCode = exception.errorCode
                if (errorCode == "ERROR_USER_NOT_FOUND") {
                    Toast.makeText(this, "Email är inte registrerad.", Toast.LENGTH_SHORT).show()
                }
            } else -> {
            Toast.makeText(this, "Login failed. Please try again later.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
