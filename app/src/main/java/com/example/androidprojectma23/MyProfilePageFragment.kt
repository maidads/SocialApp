package com.example.androidprojectma23

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class MyProfilePageFragment : Fragment() {

    private lateinit var profileImageView: ImageView
    private lateinit var displayNameTextView: TextView
    private lateinit var ageTextView: TextView
    private lateinit var aboutInfoTextView: TextView
    private lateinit var interestsInfoTextView: TextView
    private lateinit var editProfileButton: ExtendedFloatingActionButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_my_profile_page, container, false)

        profileImageView = view.findViewById(R.id.profileImageViewBack)
        displayNameTextView = view.findViewById(R.id.displayNameTextViewBack)
        ageTextView = view.findViewById(R.id.ageTextViewBack)
        aboutInfoTextView = view.findViewById(R.id.aboutInfoTextView)
        interestsInfoTextView = view.findViewById(R.id.interestsInfoTextView)
        editProfileButton = view.findViewById(R.id.editProfileButton)

        editProfileButton.setOnClickListener {
        }

        return view
    }

}
