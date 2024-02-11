package com.example.androidprojectma23

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ProfileCreationStep1Fragment : Fragment() {

    private lateinit var displayNameEditText: EditText
    private lateinit var nextStepButton: Button
    private lateinit var userImagePlaceholder: ImageView

    private lateinit var userProfileManager: UserProfileManager
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Extract userId from the fragment argument
        arguments?.let {
            userId = it.getString("USER_ID")
        }
        // Initialize UserProfileManager
//        userProfileManager = UserProfileManager(
//            FirebaseStorage.getInstance().reference,
//            FirebaseFirestore.getInstance()
//        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile_creation_step1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI(view)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Access granted
                   openCameraForImage()
                } else {
                    // Access denied
                }
                return
            }
            // handle other permission requests
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            // Use image thats been taken
        }
    }


    private fun initUI(view: View) {
        displayNameEditText = view.findViewById(R.id.displayName_textInputEditText)
        nextStepButton = view.findViewById(R.id.button_profile_creation_nextStep)
        userImagePlaceholder = view.findViewById(R.id.profile_image_placeholder)

        nextStepButton.setOnClickListener {

            val userId = "someUserId" // TODO: Replace with a users actual ID
            val displayName = displayNameEditText.text.toString()

            saveProfileDisplayName(userId, displayName)
            navigateToProfileCreationStep2()
        }

        userImagePlaceholder.setOnClickListener {
            showImageSelectionDialog()
        }
    }

    fun FragmentManager.navigateTo(fragment: Fragment, containerId: Int) {
        this.beginTransaction()
            .replace(containerId, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToProfileCreationStep2() {
        parentFragmentManager.navigateTo(ProfileCreationStep2Fragment(), R.id.fragment_container)
    }

    private fun showImageSelectionDialog() {
        val context = requireContext()
        val builder = AlertDialog.Builder(context)
        val view = LayoutInflater.from(context).inflate(R.layout.custom_dialog_appearance_profile_creation_image_selection, null)
        builder.setView(view)

        val dialog = builder.create()

        val galleryImageView = view.findViewById<ImageView>(R.id.galleryImageView)
        galleryImageView.setOnClickListener {
            // openGalleryForImage()

            dialog.dismiss()
        }

        val cameraImageView = view.findViewById<ImageView>(R.id.cameraImageView)
        cameraImageView.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestCameraPermission()
            } else {
                openCameraForImage()
            }
            dialog.dismiss()
        }

        dialog.show()
    }


    private fun openCameraForImage() {
        // Check for the camera permission
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted, open the camera
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            } catch (e: ActivityNotFoundException) {
                // Handle the error
            }
        } else {
            // Permission is not granted, request the permission
            requestCameraPermission()
        }
    }

    private fun requestCameraPermission() {
        // Explanation of why the permission is needed
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CAMERA)) {
            // Show an explanation asynchronously
            // TODO: Show a dialog or a toast explaining why camera access is needed
        } else {
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        }
    }

    companion object {

        private const val REQUEST_CAMERA_PERMISSION = 1
        private const val REQUEST_IMAGE_CAPTURE = 2
        fun newInstance(userId: String): ProfileCreationStep1Fragment {
            val fragment = ProfileCreationStep1Fragment()
            val args = Bundle().apply {
                putString("USER_ID", userId)
            }
            fragment.arguments = args
            return fragment
        }
    }
}