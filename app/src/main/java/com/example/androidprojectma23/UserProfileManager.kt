package com.example.androidprojectma23

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserProfileManager(private val storageRef: StorageReference, private val firestore: FirebaseFirestore) {

    fun uploadProfileImage(userId: String, imageUri: Uri, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userImageRef = storageRef.child("userImages/$userId/profileImage.jpg")

        userImageRef.putFile(imageUri).addOnSuccessListener {
            userImageRef.downloadUrl.addOnSuccessListener { uri ->
                saveUserProfileImage(userId, uri.toString(), onSuccess, onFailure)
            }.addOnFailureListener { e ->
                onFailure(e)
            }
        }.addOnFailureListener { e ->
            onFailure(e)
        }
    }

    private fun saveUserProfileImage(userId: String, imageUrl: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userDocumentRef = firestore.collection("users").document(userId)

        userDocumentRef.update("profileImageUrl", imageUrl).addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener { e ->
            onFailure(e)
        }
    }

    fun saveDisplayName(userId: String, displayName: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userDocumentRef = firestore.collection("users").document(userId)

        // Update displayName data field
        userDocumentRef.update("displayName", displayName).addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener {
            onFailure(it)
        }
    }
}