package com.example.androidprojectma23

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference

class UserProfileManager(private val storageRef: StorageReference, private val firestore: FirebaseFirestore) {

    fun uploadProfileImage(userId: String, imageUri: Uri, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val ref = storageRef.child("userImages/$userId/profileImage.jpg")
        ref.putFile(imageUri).addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener {
            onFailure(it)
        }
    }

    fun updateDisplayName(userId: String, displayName: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userDocumentRef = firestore.collection("users").document(userId)
        userDocumentRef.update("displayName", displayName).addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener {
            onFailure(it)
        }
    }
}