package com.example.androidprojectma23

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChangeProfileFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private var userId: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_change_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()
        userId = FirebaseAuth.getInstance().currentUser?.uid

        loadUserProfile()

        view.findViewById<Button>(R.id.saveProfileButton).setOnClickListener {
            saveProfileChanges()
        }
    }

    private fun loadUserProfile() {
        // Load existing user data from Firestore and populate input fields
    }

    private fun saveProfileChanges() {
        val updatedName = view?.findViewById<EditText>(R.id.nameEditText)?.text.toString()

        val userUpdates = hashMapOf(
            "displayName" to updatedName
        )

        userId?.let { id ->
            firestore.collection("users").document(id).update(userUpdates as Map<String, Any>)
                .addOnSuccessListener {
                    Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    // Navigate back or update UI accordingly
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to update profile: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}

