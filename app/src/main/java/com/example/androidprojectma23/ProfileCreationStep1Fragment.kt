package com.example.androidprojectma23

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
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
            // TODO: Open dialog to choose image option, camera or gallery
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

    }

    private fun saveProfileDisplayName(userId: String, displayName: String) {
//        userProfileManager.saveDisplayName(userId, displayName, {
//            // Update success
//            Toast.makeText(context, "Display name updated successfully.", Toast.LENGTH_SHORT).show()
//        }, { exception ->
//            // Update failed
//            Toast.makeText(context, "Failed to update display name: ${exception.message}", Toast.LENGTH_LONG).show()
//        })
    }

    companion object {
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