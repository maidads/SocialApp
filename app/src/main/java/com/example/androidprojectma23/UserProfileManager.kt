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

    private fun getUserInterests(userId: String, mapFunc: (String) -> Int?, onSuccess: (List<Int>) -> Unit, onFailure: (Exception) -> Unit) {
        val userDocumentRef = firestore.collection("users").document(userId)

        userDocumentRef.get().addOnSuccessListener { documentSnapshot ->
            val user = documentSnapshot.toObject(User::class.java)
            Log.d("UPM", "User interests: ${user?.interests}")
            val interestsResIds = user?.interests?.mapNotNull { interestDocId ->
                mapFunc(interestDocId)?.also {
                    Log.d("UPM", "Mapped resource: $it for interest: $interestDocId")
                }
            } ?: emptyList()

            if (interestsResIds.isEmpty()) {
                Log.d("UPM", "No resources found for user interests")
            }

            onSuccess(interestsResIds)
        }.addOnFailureListener { exception ->
            Log.e("UPM", "Error fetching user interests", exception)
            onFailure(exception)
        }
    }

    fun getUserInterestsIcons(userId: String, onSuccess: (List<Int>) -> Unit, onFailure: (Exception) -> Unit) {
        getUserInterests(userId, { interestDocId -> IconMapping.docIdToIconResMap[interestDocId] }, onSuccess, onFailure)
    }

    fun getUserInterestsTexts(userId: String, onSuccess: (List<Int>) -> Unit, onFailure: (Exception) -> Unit) {
        getUserInterests(userId, { interestDocId -> IconMapping.docIdToInterestNameMap[interestDocId] }, onSuccess, onFailure)
    }

    fun getUserData(userId: String, callback: (User?) -> Unit) {
        firestore.collection("users").document(userId).get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val user = document.toObject(User::class.java)
                callback(user)
            } else {
                callback(null)
            }
        }.addOnFailureListener {
            callback(null)
        }
    }
}