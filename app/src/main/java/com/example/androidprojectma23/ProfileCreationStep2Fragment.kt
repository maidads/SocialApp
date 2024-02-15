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
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class ProfileCreationStep2Fragment : Fragment() {

    private val database by lazy { FirebaseFirestore.getInstance() }
    private lateinit var backButton: TextView
    private lateinit var userId: String

    private val imageViewIdToFirestoreDocumentIdMap = mapOf(

        R.id.icon_music to "qWAkLQAUlXIuJCn45ChZ",
        R.id.icon_sports to "9NPO76LYaq9hl5KOCtC4",
        R.id.icon_movies to "AYpDbrWtQOUOt7rBDXDH",
        R.id.icon_art to "iglkcuMPG8egGg4scETR",
        R.id.icon_books to "YmMikFDeggctuiqSYrmw",
        R.id.icon_wine to "YvVGXixVaQSsMhAZaCy2",
        R.id.icon_cooking to "HPQHhJeFC7wQaHyAFSnU",
        R.id.icon_travel to "M9RqxG3Caa0JNT9h6ZTX",
        R.id.icon_festival to "EymGn10U227Gf5xmducS",
        R.id.icon_fashion to "vs5sifFqzkrCyVILxya6",
        R.id.icon_dance to "zcz594bv81UYIWjhWgTy",
        R.id.icon_games to "SM8Oh6Hnba6Gzjpn77RJ",
        R.id.icon_yoga to "6O7GXIC0DWKz6T8wXCJa",
        R.id.icon_camping to "HvJnJ1QKuS2l9IAzYkGa",
        R.id.icon_fika to "0YB3cpO2ducwVQeuCmHC",
        R.id.icon_training to "xZ4sv4Th1Rx3xmUWPr7C",
        R.id.icon_animals to "YeByZ6w5see5N6GfBPYI",
        R.id.icon_garden to "zBgJksLJY1Fa0s3oUSvg",
        R.id.icon_photography to "ftWLcl8ag7pabyuSSJih",
        R.id.icon_technology to "GTPROJYniNOrFBivL7wE"
    )

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

        // list of all imageViews for the interest icons
        val userInterests = listOf(
            R.id.icon_music, R.id.icon_sports, R.id.icon_movies, R.id.icon_art,
            R.id.icon_books, R.id.icon_wine, R.id.icon_cooking, R.id.icon_travel,
            R.id.icon_festival, R.id.icon_fashion, R.id.icon_dance, R.id.icon_games,
            R.id.icon_yoga, R.id.icon_camping, R.id.icon_fika, R.id.icon_training,
            R.id.icon_animals, R.id.icon_garden, R.id.icon_photography, R.id.icon_technology
        )

        val selectedInterest = mutableListOf<Int>()

        val interestClickListener = View.OnClickListener { view ->
            val imageView = view as ImageView
            val isSelected = selectedInterest.contains(imageView.id)

            // Hämta Firestore-dokument-ID baserat på ImageView ID
            val documentId = imageViewIdToFirestoreDocumentIdMap[imageView.id]

            if (isSelected) {
                imageView.alpha = 1f
                selectedInterest.remove(imageView.id)
            } else {
                imageView.alpha = 0.5f
                selectedInterest.add(imageView.id)
            }
            Log.d("!!!", "Valt intresse: $documentId")
        }

        // Put OnClickListener on each ImageView
        userInterests.forEach { imageViewId ->
            view.findViewById<ImageView>(imageViewId).apply {
                setOnClickListener(interestClickListener)
                alpha = 1f
            }
        }

        view.findViewById<Button>(R.id.button_profile_creation_done).setOnClickListener {
            if (selectedInterest.isEmpty()) {

                Toast.makeText(context, "Välj minst ett intresse för att fortsätta.", Toast.LENGTH_LONG).show()
            } else {

                val selectedDocIdsList = selectedInterest.mapNotNull { imageViewId ->
                    imageViewIdToFirestoreDocumentIdMap[imageViewId]
                }.toSet().toList()

                val userDocRef = database.collection("users").document(userId)
                userDocRef.update("interests", selectedDocIdsList)
                    .addOnSuccessListener {
                        Log.d("!!!", "Användarens intressen har uppdaterats.")

                        val intent = Intent(activity, LandingPageActivity::class.java)
                        startActivity(intent)
                        activity?.finish()
                    }
                    .addOnFailureListener { e ->
                        Log.e("!!!", "Fel vid uppdatering av användarens intressen", e)
                    }
            }
        }

        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()

        }


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
