package com.example.androidprojectma23

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
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
    private var imageUri: Uri? = null


    companion object {
        private const val REQUEST_CODE_PICK_IMAGE = 1001
        private const val REQUEST_CODE_CAPTURE_IMAGE = 1002
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1003
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_change_profile, container, false)
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
            showImagePickerOptions()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCameraForImage()
                } else {
                    Toast.makeText(context, "Kameratillstånd nekades", Toast.LENGTH_SHORT).show()
                }
            }
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

    private fun showImagePickerOptions() {
        val options = arrayOf<CharSequence>("Ta en bild", "Välj från galleri", "Avbryt")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Lägg till foto!")
        builder.setItems(options) { dialog, item ->
            when (options[item]) {
                "Ta en bild" -> if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                    openCameraForImage()
                                } else {
                                    requestCameraPermission()
                                }
                "Välj från galleri" -> pickImageFromGallery()
                "Avbryt" -> dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun openCameraForImage() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                startActivityForResult(takePictureIntent,
                    ChangeProfileFragment.REQUEST_CODE_CAPTURE_IMAGE
                )
            } catch (e: ActivityNotFoundException) {
            }
        } else {
            requestCameraPermission()
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_PICK_IMAGE -> {
                    data?.data?.let { uri ->
                        uploadImageToFirebaseStorage(uri)
                    }
                }
                REQUEST_CODE_CAPTURE_IMAGE -> {
                    imageUri?.let { uploadImageToFirebaseStorage(it) }
                }
            }
        }
    }

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

    private fun saveProfileImageUrlToFirestore(imageUrl: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let { uid ->
            firestore.collection("users").document(uid)
                .update("profileImageUrl", imageUrl)
                .addOnSuccessListener {
                    Toast.makeText(context, "Profile image updated", Toast.LENGTH_SHORT).show()
                    Glide.with(this@ChangeProfileFragment)
                        .load(imageUrl)
                        .placeholder(R.drawable.profile_image_placeholder)
                        .error(R.drawable.profile_image_placeholder)
                        .circleCrop()
                        .into(profileImageView)
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to update profile image: ${it.localizedMessage}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun requestCameraPermission() {
        if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
            AlertDialog.Builder(requireContext())
                .setTitle("Kräver kameratillstånd")
                .setMessage("Denna app behöver tillgång till din kamera för att kunna ta bilder.")
                .setPositiveButton("OK") { _, _ ->
                    requestPermissions(arrayOf(android.Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
                }
                .create()
                .show()
        } else {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        }
    }
}

