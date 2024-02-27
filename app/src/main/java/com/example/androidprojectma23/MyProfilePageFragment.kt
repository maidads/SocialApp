package com.example.androidprojectma23

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.auth.FirebaseAuth

class MyProfilePageFragment : Fragment() {

    private lateinit var profileImageView: ImageView
    private lateinit var displayNameTextView: TextView
    private lateinit var ageTextView: TextView
    private lateinit var aboutInfoTextView: TextView
    private lateinit var interestsInfoTextView: TextView
    private lateinit var editProfileButton: ExtendedFloatingActionButton

    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_my_profile_page, container, false)

        profileImageView = view.findViewById(R.id.profileImageViewBack)
        displayNameTextView = view.findViewById(R.id.displayNameTextViewBack)
        ageTextView = view.findViewById(R.id.ageTextViewBack)
        aboutInfoTextView = view.findViewById(R.id.aboutInfoTextView)
        interestsInfoTextView = view.findViewById(R.id.interestsInfoTextView)
        editProfileButton = view.findViewById(R.id.editProfileButton)

        editProfileButton.setOnClickListener {
            val changeProfileFragment = ChangeProfileFragment()

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentHolder, changeProfileFragment)
                .addToBackStack(null)
                .commit()
        }
        firestore = FirebaseFirestore.getInstance()

        loadUserProfile()

        return view
    }
    private fun loadUserProfile() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    val user = document.toObject<User>()
                    if (user != null) {
                        displayNameTextView.text = user.displayName
                        ageTextView.text = user.age.toString()

                        context?.let { it ->
                            Glide.with(it)
                                .load(user.profileImage)
                                .placeholder(R.drawable.user_image_icon)            // Standardbild medan den riktiga bilden laddas
                                .error(R.drawable.user_image_icon)                  // Om det inte går att ladda den riktiga bilden
                                .circleCrop()                                       // Cirkulär form
                                .into(profileImageView)
                        }
                    } else {
                    }
                }
                .addOnFailureListener { exception ->
                }
        }
    }
}
