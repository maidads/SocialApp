package com.example.androidprojectma23

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SwipeFriendsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_swipe_friends)

        val profiles = listOf(
            Profile("https://example.com/image1.jpg", "Fotboll, Musik, Matlagning"),
            Profile("https://example.com/image2.jpg", "Resor, Läsning, Träning"),
            Profile("https://example.com/image3.jpg", "Konst, Film, Teknik")
        )

        val adapter = ProfileCardAdapter()
        adapter.setProfiles(profiles)

        val recyclerView = findViewById<RecyclerView>(R.id.profilesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter

    }

}