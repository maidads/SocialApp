package com.example.androidprojectma23

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class ChangeProfileFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private var userId: String? = null
    private lateinit var nameEditText: EditText
    private lateinit var ageEditText: EditText
    private lateinit var aboutEditText: EditText
    private lateinit var interestsEditText: EditText
    lateinit var changeImageButton : FloatingActionButton
    private lateinit var interestImageViewBack: ImageView
    private lateinit var interestImageView2Back: ImageView
    private lateinit var interestImageView3Back: ImageView
    private lateinit var interestImageView4Back: ImageView
    private lateinit var interestImageView5Back: ImageView
    private lateinit var profileImageView: ImageView

    companion object {
        private const val REQUEST_CODE_PICK_IMAGE = 1001
    }
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
        interestImageViewBack = view.findViewById(R.id.interestImageViewBack)
        interestImageView2Back = view.findViewById(R.id.interestImageView2Back)
        interestImageView3Back = view.findViewById(R.id.interestImageView3Back)
        interestImageView4Back = view.findViewById(R.id.interestImageView4Back)
        interestImageView5Back = view.findViewById(R.id.interestImageView5Back)
        profileImageView = view.findViewById(R.id.profileImageViewBack)
        val saveProfileButton = view.findViewById<Button>(R.id.saveProfileButton)
        val interestImageViews = listOf(
            interestImageViewBack, interestImageView2Back,
            interestImageView3Back, interestImageView4Back,
            interestImageView5Back
        )

        loadUserProfile()

        saveProfileButton.setOnClickListener {
            saveProfileToFirestore()
        }

        interestImageViews.forEach { imageView ->
            imageView.setOnClickListener {
                val currentUser = FirebaseAuth.getInstance().currentUser
                val userId = currentUser?.uid
                if (userId != null) {
                    val changeInterestsFragment = ChangeInterestsFragment.newInstance(userId)
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragmentHolder, changeInterestsFragment)
                        .addToBackStack(null)
                        .commit()
                }
            }
        }

        profileImageView.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, REQUEST_CODE_PICK_IMAGE)
        }
    }

    private fun saveProfileToFirestore() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        userId?.let { uid ->
            val userMap: Map<String, Any> = hashMapOf(
                "displayName" to nameEditText.text.toString(),
                "age" to ageEditText.text.toString(),
                "about" to aboutEditText.text.toString(),
                "myInterests" to interestsEditText.text.toString()
            )

            firestore.collection("users").document(uid)
                .update(userMap)
                .addOnSuccessListener {
                    Toast.makeText(context, "Profile Updated", Toast.LENGTH_SHORT).show()
                    fragmentManager?.popBackStack()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error updating profile: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
        }
    }
    private fun loadUserProfile() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let { uid ->
            firestore.collection("users").document(uid).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val user = documentSnapshot.data
                        nameEditText.setText(user?.get("displayName")?.toString())
                        ageEditText.setText(user?.get("age")?.toString())
                        aboutEditText.setText(user?.get("about")?.toString())
                        interestsEditText.setText(user?.get("myInterests")?.toString())

                        val interestDocIds = user?.get("interests") as? List<String> ?: emptyList()
                        updateInterestIcons(interestDocIds)

                        val profileImageUrl = user?.get("profileImageUrl")?.toString()
                        context?.let { context ->
                            Glide.with(context)
                                .load(profileImageUrl)
                                .placeholder(R.drawable.profile_image_placeholder)  // Standardbild medan den riktiga bilden laddas
                                .error(R.drawable.profile_image_placeholder)        // Om det inte går att ladda den riktiga bilden
                                .circleCrop()                                       // Cirkulär form
                                .into(profileImageView)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to load profile: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun updateInterestIcons(interestDocIds: List<String>) {
        val interestImageViews = listOf(
            interestImageViewBack, interestImageView2Back,
            interestImageView3Back, interestImageView4Back,
            interestImageView5Back
        )

        interestImageViews.forEach { it.setImageResource(R.drawable.icon_empty) }

        interestDocIds.forEachIndexed { index, docId ->
            if (index < interestImageViews.size) {
                IconMapping.docIdToIconResMap[docId]?.let { iconResId ->
                    interestImageViews[index].setImageResource(iconResId)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                uploadImageToFirebaseStorage(uri)
            }
        }
    }

    // Steg 3: Funktion för att ladda upp bild till Firebase Storage
    private fun uploadImageToFirebaseStorage(imageUri: Uri) {
        val storageRef = FirebaseStorage.getInstance().reference.child("profileImages/${UUID.randomUUID()}")
        storageRef.putFile(imageUri).addOnSuccessListener { taskSnapshot ->
            taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { downloadUri ->
                saveProfileImageUrlToFirestore(downloadUri.toString())
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to upload image: ${it.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    // Steg 4: Spara bild-URL i Firestore
    private fun saveProfileImageUrlToFirestore(imageUrl: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let { uid ->
            firestore.collection("users").document(uid)
                .update("profileImageUrl", imageUrl)
                .addOnSuccessListener {
                    Toast.makeText(context, "Profile image updated", Toast.LENGTH_SHORT).show()
                    // Steg 5: Uppdatera UI med den nya bilden
                    Glide.with(this)
                        .load(imageUrl)
                        .into(profileImageView)
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to update profile image: ${it.localizedMessage}", Toast.LENGTH_LONG).show()
                }
        }
    }
}

