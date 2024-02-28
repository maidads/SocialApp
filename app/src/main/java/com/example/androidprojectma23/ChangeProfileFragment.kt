package com.example.androidprojectma23

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChangeProfileFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private var userId: String? = null
    private lateinit var nameEditText: EditText
    private lateinit var ageEditText: EditText
    private lateinit var aboutEditText: EditText
    private lateinit var interestsEditText: EditText
    lateinit var changeImageButton : FloatingActionButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_change_profile, container, false)
        Log.d("MyFragment", "onCreateView called for ChangeProfilePageFragment")
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()

        nameEditText = view.findViewById(R.id.nameEditText)
        ageEditText = view.findViewById(R.id.ageEditText)
        aboutEditText = view.findViewById(R.id.aboutEditText)
        interestsEditText = view.findViewById(R.id.interestsEditText)
        val saveProfileButton = view.findViewById<Button>(R.id.saveProfileButton)


        saveProfileButton.setOnClickListener {
            saveProfileToFirestore()
        }
    }

    private fun saveProfileToFirestore() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        userId?.let { uid ->
            val userMap = hashMapOf(
                "displayName" to nameEditText.text.toString(),
                "age" to ageEditText.text.toString(),
                "about" to aboutEditText.text.toString(),
                "myInterests" to interestsEditText.text.toString()
            )

            firestore.collection("users").document(uid)
                .update(userMap as Map<String, Any>)
                .addOnSuccessListener {
                    Toast.makeText(context, "Profile Updated", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error updating profile: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
        }
    }

    /*
        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            firestore = FirebaseFirestore.getInstance()
            userId = FirebaseAuth.getInstance().currentUser?.uid

            nameEditText = view.findViewById(R.id.nameEditText)
            ageEditText = view.findViewById(R.id.ageEditText)
            aboutEditText = view.findViewById(R.id.aboutEditText)
            interestsEditText = view.findViewById(R.id.interestsEditText)
            val saveProfileButton = view.findViewById<FloatingActionButton>(R.id.saveProfileButton)


     */
        //loadExistingUserInfo()
/*
        saveProfileButton.setOnClickListener {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener
            val updatedName = nameEditText.text.toString()
            //val updatedAge = ageEditText.text.toString().toIntOrNull() ?: 0
            val updatedAbout = if (aboutEditText.text.toString().isBlank()) "Ingen information tillgänglig" else aboutEditText.text.toString()
            // val updatedInterests = interestsEditText.text.toString()

            val userUpdates = mapOf(
                "displayName" to updatedName,
               // "age" to updatedAge,
                "about" to updatedAbout,
                //"interests" to updatedInterests
            )
            FirebaseFirestore.getInstance().collection("users").document(userId)
                .update(userUpdates)
                .addOnSuccessListener {
                    Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to update profile: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }

 */
    //}
/*
    private fun loadExistingUserInfo() {
        userId?.let { uid ->
            firestore.collection("users").document(uid).get()
                .addOnSuccessListener { documentSnapshot ->
                    val user = documentSnapshot.toObject(User::class.java)
                    user?.let {
                        nameEditText.setText(it.displayName)
                        ageEditText.setText(it.age.toString())
                        aboutEditText.setText(it.about ?: "Ingen information tillgänglig")
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to load existing data: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

 */

}

