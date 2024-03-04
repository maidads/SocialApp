package com.example.androidprojectma23

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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
                .addToBackStack(ChangeProfileFragment::class.java.simpleName)
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
                        ageTextView.text = user.age + " år"
                        aboutInfoTextView.text = user.about.ifEmpty { "Ingen information tillgänglig" }
                        interestsInfoTextView.text = user.myInterests.ifEmpty { "Ingen information tillgänglig" }

                        context?.let { context ->
                            Glide.with(context)
                                .load(user.profileImageUrl)
                                .placeholder(R.drawable.profile_image_placeholder)            // Standardbild medan den riktiga bilden laddas
                                .error(R.drawable.profile_image_placeholder)                  // Om det inte går att ladda den riktiga bilden
                                .circleCrop()                                                 // Cirkulär form
                                .into(profileImageView)
                        }
                        updateInterestIcons(user.interests)

                    } else {
                        displayNameTextView.text = "Användare hittades inte"
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("MyProfilePageFragment", "Error loading user profile", exception)
                    Toast.makeText(context, "Error loading user profile", Toast.LENGTH_SHORT).show()
                }
        }
    }
    private fun updateInterestIcons(interestDocIds: List<String>) {

        val interestImageViews = IconMapping.fragmentInterestIconImage.mapNotNull { id ->
            view?.findViewById<ImageView>(id)
        }

        val interestTextViews = IconMapping.fragmentInterestIconText.mapNotNull { id ->
            view?.findViewById<TextView>(id)
        }

        interestImageViews.forEach { it.setImageResource(R.drawable.icon_empty) }

        interestTextViews.forEach { it.text = "" }

        interestDocIds.forEachIndexed { index, docId ->
            if (index < interestImageViews.size) {
                IconMapping.docIdToIconResMap[docId]?.let { iconResId ->
                    interestImageViews[index].setImageResource(iconResId)
                }
            }
            if (index < interestTextViews.size) {
                IconMapping.docIdToInterestNameMap[docId]?.let { interestNameResId ->
                    interestTextViews[index].text = getString(interestNameResId)
                }
            }
        }
    }
}
