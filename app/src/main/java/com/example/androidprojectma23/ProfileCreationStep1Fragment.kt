package com.example.androidprojectma23

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ProfileCreationStep1Fragment : Fragment() {

    // private val database = FirebaseFirestore.getInstance()
    private lateinit var displayNameEditText: EditText
    private lateinit var nextStepButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_profile_creation_step1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI(view)
    }

    private fun initUI(view: View) {
        displayNameEditText = view.findViewById(R.id.displayName_textInputEditText)
        nextStepButton = view.findViewById(R.id.button_profile_creation_nextStep)

        nextStepButton.setOnClickListener {
            val displayName = displayNameEditText.text.toString().trim()
            if (displayName.isNotEmpty()) {
               // saveDisplayNameToFirestore(displayName)
            } else {
                // Visa felmeddelande om displaynamnet är tomt
            }
        }
    }

    private fun saveProfileImage(userId: String?, imageUri: Uri) {
        // Check so we have a user-ID and a image Uri
        if (userId == null || imageUri == null) return

        // reference to firebase storage
        val storageRef = FirebaseStorage.getInstance().reference.child("userImages/$userId/profileImage.jpg")

        // Upload the image
        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                // Save the url from firebase
            }
            .addOnFailureListener {
                // Handle exception
            }

    }

    private fun saveProfileDisplayName(userId: String?, displayName: String) {
        // Check so we have a user-ID
        if (userId == null) return

        val userDocumentRef = FirebaseFirestore.getInstance().collection("users").document(userId)

        // Update user display name
        userDocumentRef.update("displayName", displayName)
            .addOnSuccessListener {
                // Update successful
            }
            .addOnFailureListener {
                // Handle exception
            }

    }
}