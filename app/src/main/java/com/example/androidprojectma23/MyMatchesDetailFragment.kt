package com.example.androidprojectma23

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class MyMatchesDetailFragment : Fragment() {

    private lateinit var displayNameTextView: TextView
    private lateinit var ageTextView: TextView
    private lateinit var aboutUserTextView: TextView
    private lateinit var aboutInterestsTextView: TextView
    private lateinit var userProfileImage: ImageView
    private lateinit var interestImageViews: List<ImageView>


    // Initialisera UserProfileManager med Firestore-instansen
    private val userProfileManager by lazy {
        UserProfileManager(FirebaseStorage.getInstance().reference, FirebaseFirestore.getInstance())
    }

    companion object {
        private const val ARG_USER_ID = "userId"

        fun newInstance(userId: String): MyMatchesDetailFragment {
            val args = Bundle().apply {
                putString(ARG_USER_ID, userId)
            }
            return MyMatchesDetailFragment().apply {
                arguments = args
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_my_matches_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        displayNameTextView = view.findViewById(R.id.matches_details_displayName_textView)
        ageTextView = view.findViewById(R.id.matches_detaíls_age_textView)
        aboutUserTextView = view.findViewById(R.id.matches_details_about_textView)
        aboutInterestsTextView = view.findViewById(R.id.matches_details_interest_textView)
        userProfileImage = view.findViewById(R.id.matches_details_profile_image)

        interestImageViews = listOf(
            view.findViewById(R.id.matches_details_interest_imageView1),
            view.findViewById(R.id.matches_details_interest_imageView2),
            view.findViewById(R.id.matches_details_interest_imageView3),
            view.findViewById(R.id.matches_details_interest_imageView4),
            view.findViewById(R.id.matches_details_interest_imageView5)
        )

        displayNameTextView.visibility = View.INVISIBLE
        ageTextView.visibility = View.INVISIBLE
        aboutUserTextView.visibility = View.INVISIBLE
        aboutInterestsTextView.visibility = View.INVISIBLE
        userProfileImage.visibility = View.INVISIBLE
        interestImageViews.forEach { it.visibility = View.INVISIBLE }

        arguments?.getString(ARG_USER_ID)?.let { userId ->
            updateUserProfileUI(userId)
            updateUserInterestIcons(userId)
        }

    }

    private fun updateUserProfileUI(userId: String) {
        userProfileManager.getUserData(userId) { user ->
            activity?.runOnUiThread {
                if (user != null) {

                    displayNameTextView.text = user.displayName
                    ageTextView.text = user.age?.toString() ?: ""
                    aboutUserTextView.text = user.about
                    aboutInterestsTextView.text = user.myInterests


                    user.profileImageUrl?.let { imageUrl ->
                        Glide.with(this@MyMatchesDetailFragment)
                            .load(imageUrl)
                            .placeholder(R.drawable.profile_image_placeholder)
                            .error(R.drawable.profile_image_placeholder)
                            .into(userProfileImage)
                    }

                    displayNameTextView.visibility = View.VISIBLE
                    ageTextView.visibility = View.VISIBLE
                    aboutUserTextView.visibility = View.VISIBLE
                    aboutInterestsTextView.visibility = View.VISIBLE
                    userProfileImage.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun updateUserInterestIcons(userId: String) {
        userProfileManager.getUserInterestsIcons(userId, onSuccess = { iconsResIds ->

            interestImageViews.forEachIndexed { index, imageView ->
                if (index < iconsResIds.size) {
                    imageView.setImageResource(iconsResIds[index])
                    imageView.visibility = View.VISIBLE
                } else {
                    imageView.visibility = View.GONE
                }
            }
        }, onFailure = { exception ->
            Log.e("MyMatchesDetailFragment", "Fel vid hämtning av användarintressen: ${exception.message}")
        })
    }

}
