package com.example.androidprojectma23

import android.content.Intent
import android.os.Bundle
import android.util.Log
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

        KeyboardUtils.hideKeyboardOnAction(usernameEditText, this)
        KeyboardUtils.hideKeyboardOnAction(passwordEditText, this)

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

//        Remembers user to log in directly when opening the app
//                if (auth.currentUser != null) {
//                    val intent = Intent(this, LandingPageActivity::class.java)
//                    startActivity(intent)
//                    finish()
//                }


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
            Log.e("!!!", "signInResult:failed code=" + e.statusCode)
            Toast.makeText(this, "Inloggningen misslyckades, försök igen. Felkod: ${e.statusCode}", Toast.LENGTH_SHORT).show()
        }
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    val isNewUser = task.result?.additionalUserInfo?.isNewUser == true

                    if (isNewUser) {
                        saveNewUserInfo()
                        auth.currentUser?.uid?.let { userId ->
                            navigateToProfileCreationStep1(userId)
                        }
                    } else {
                        navigateToLandingPage()
                    }
                } else {
                    Toast.makeText(this, "Autentisering misslyckades.", Toast.LENGTH_SHORT).show()
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

    private fun navigateToLandingPage() {
        val intent = Intent(this, LandingPageActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun saveNewUserInfo() {

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userBasicInfo = hashMapOf(
                "userId" to currentUser.uid
            )

            val db = Firebase.firestore
            db.collection("users").document(currentUser.uid)
                .set(userBasicInfo)
                .addOnSuccessListener {
                    Toast.makeText(this, "Grundläggande användarinformation har sparats.", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Det gick inte att spara grundläggande användarinformation.", Toast.LENGTH_SHORT).show()
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
