package com.example.androidprojectma23

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class ProfileCreationStep2Fragment : Fragment() {

    private lateinit var database: FirebaseFirestore

    private val imageViewIdToDocIdMap = mutableMapOf<Int, String>()
    private val interestNameToImageViewId = mapOf(
        "music" to R.id.icon_music,
        "sport" to R.id.icon_sports,
        "movies" to R.id.icon_movies,
        "art" to R.id.icon_art,
        "books" to R.id.icon_books,
        "wine" to R.id.icon_wine,
        "cooking" to R.id.icon_cooking,
        "travel" to R.id.icon_travel,
        "festival" to R.id.icon_festival,
        "fashion" to R.id.icon_fashion,
        "dance" to R.id.icon_dance,
        "game" to R.id.icon_games,
        "yoga" to R.id.icon_yoga,
        "camping" to R.id.icon_camping,
        "fika" to R.id.icon_fika,
        "training" to R.id.icon_training,
        "animal" to R.id.icon_animals,
        "garden" to R.id.icon_garden,
        "photography" to R.id.icon_photography,
        "technology" to R.id.icon_technology
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_profile_creation_step2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = FirebaseFirestore.getInstance()

        database.collection("interest").get().addOnSuccessListener { documents ->
            for (document in documents) {
                val interestName = document.getString("name")?.toLowerCase(Locale.ROOT) ?: continue
                val documentId = document.id
                interestNameToImageViewId[interestName]?.let { imageViewId ->
                    imageViewIdToDocIdMap[imageViewId] = documentId
                }
            }
        }
        initUI(view)

    }

    private fun initUI(view: View) {

        // list of all imageViews for the interest icons
        val userInterests = listOf(
            R.id.icon_music, R.id.icon_sports, R.id.icon_movies, R.id.icon_art,
            R.id.icon_books, R.id.icon_wine, R.id.icon_cooking, R.id.icon_travel,
            R.id.icon_festival, R.id.icon_fashion, R.id.icon_dance, R.id.icon_games,
            R.id.icon_yoga, R.id.icon_camping, R.id.icon_fika, R.id.icon_training,
            R.id.icon_animals, R.id.icon_garden, R.id.icon_photography, R.id.icon_technology
        )

        val selectedInterest = mutableListOf<Int>()

        val interestClickListener = View.OnClickListener { imageView ->
            imageView as ImageView // Cast view to imageView
            val isSelected = selectedInterest.contains(imageView.id)

            if (isSelected) {
                imageView.alpha = 1f
                selectedInterest.remove(imageView.id)
            } else {
                imageView.alpha = 0.5f
                selectedInterest.add(imageView.id)
            }
        }

        // Put OnClickListener on each ImageView
        userInterests.forEach { imageViewId ->
            view.findViewById<ImageView>(imageViewId).apply {
                setOnClickListener(interestClickListener)
                alpha = 1f
            }
        }

        view.findViewById<Button>(R.id.button_profile_creation_done).setOnClickListener {

            val selectedDocIds = selectedInterest.mapNotNull { imageViewId ->
                imageViewIdToDocIdMap[imageViewId]
            }

            val userId = "användarens ID här"
            val userDocRef = database.collection("users").document(userId)

            // Update user docoument with the id of the interests
            userDocRef.update("selectedInterests", selectedDocIds)
                .addOnSuccessListener {
                    Log.d("ProfileCreationStep2Fragment", "Intressen uppdaterade.")
                }
                .addOnFailureListener { e ->
                    Log.e("ProfileCreationStep2Fragment", "Fel vid uppdatering av intressen", e)
                }
        }

    }
}
