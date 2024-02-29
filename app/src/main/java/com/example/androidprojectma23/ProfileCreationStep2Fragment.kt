package com.example.androidprojectma23

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.androidprojectma23.IconMapping.iconIdToDocIdMap
import com.example.androidprojectma23.IconMapping.userInterests
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class ProfileCreationStep2Fragment : Fragment() {

    private val database by lazy { FirebaseFirestore.getInstance() }
    private lateinit var backButton: TextView
    private lateinit var userId: String

    private var selectedInterest = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getString("USER_ID") ?: throw IllegalArgumentException("User ID is required")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_profile_creation_step2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI(view)
    }

    private fun initUI(view: View) {

        backButton = view.findViewById(R.id.go_back_textView)

        // List of all imageViews for the interest icons
        val userInterests = userInterests

        val interestClickListener = View.OnClickListener { view ->
            val imageView = view as ImageView
            val isSelected = selectedInterest.contains(imageView.id)


            if (isSelected) {
                imageView.alpha = 1f
                selectedInterest.remove(imageView.id)
            } else if (selectedInterest.size < 5) {
                imageView.alpha = 0.5f
                selectedInterest.add(imageView.id)
            } else {
                Toast.makeText(context, getString(R.string.profile_creation_step2_max_interest_message), Toast.LENGTH_SHORT).show()
            }
            Log.d("!!!", "Valt intresse: ${iconIdToDocIdMap[imageView.id]}")
        }

        // Put OnClickListener on each ImageView
        userInterests.forEach { imageViewId ->
            view.findViewById<ImageView>(imageViewId).apply {
                setOnClickListener(interestClickListener)
                alpha = 1f
            }
        }

        view.findViewById<Button>(R.id.button_profile_creation_done).setOnClickListener {
            handleProfileCompletion()
        }

        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()

        }

    }

    private fun handleProfileCompletion() {
        if (selectedInterest.isEmpty()) {
            Toast.makeText(context, getString(R.string.profile_creation_step2_minimum_interest_message), Toast.LENGTH_LONG).show()
        } else {
            saveSelectedInterestsAndNavigate()
        }
    }

    private fun saveSelectedInterestsAndNavigate() {
        val selectedDocIdsList = selectedInterest.mapNotNull { imageViewId ->
            iconIdToDocIdMap[imageViewId]
        }.toSet().toList()

        updateInterestsInFirestore(selectedDocIdsList)
    }

    private fun updateInterestsInFirestore(selectedDocIdsList: List<String>) {
        val userDocRef = database.collection("users").document(userId)
        userDocRef.update("interests", selectedDocIdsList)
            .addOnSuccessListener {
                Log.d("!!!", "Användarens intressen har uppdaterats.")
                navigateToLandingPage()
            }
            .addOnFailureListener { e ->
                Log.e("!!!", "Fel vid uppdatering av användarens intressen", e)
            }
    }

    private fun navigateToLandingPage() {
        val intent = Intent(activity, LandingPageActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    companion object {
        fun newInstance(userId: String): ProfileCreationStep2Fragment {
            val fragment = ProfileCreationStep2Fragment()
            val args = Bundle().apply {
                putString("USER_ID", userId)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
