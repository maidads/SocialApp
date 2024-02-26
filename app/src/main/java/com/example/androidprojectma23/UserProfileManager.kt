package com.example.androidprojectma23

import android.net.Uri
import android.util.Log
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

    fun getUserInterestsIcons(userId: String, onSuccess: (List<Int>) -> Unit, onFailure: (Exception) -> Unit) {
        val userDocumentRef = firestore.collection("users").document(userId)

        userDocumentRef.get().addOnSuccessListener { documentSnapshot ->
            val user = documentSnapshot.toObject(User::class.java)
            Log.d("UPM", "User interests: ${user?.interests}")
            val interestsIcons = user?.interests?.mapNotNull { interestDocId ->
                IconMapping.docIdToIconResMap[interestDocId]?.also {
                    Log.d("UPM", "Mapped icon: $it for interest: $interestDocId")
                }
            } ?: emptyList()

            if (interestsIcons.isEmpty()) {
                Log.d("UPM", "No icons found for user interests")
            }

            onSuccess(interestsIcons)
        }.addOnFailureListener { exception ->
            Log.e("UPM", "Error fetching user interests", exception)
            onFailure(exception)
        }
    }
}