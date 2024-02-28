package com.example.androidprojectma23

data class User(
    var userId: String = "",
    var displayName: String = "",
    var interests: MutableList<String> = mutableListOf(),
    var commonInterests: MutableList<String> = mutableListOf(),
    val geohash: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    var about: String = "Ingen information tillgänglig",
    var myInterests: String = "Ingen information tillgänglig",
    val profileImageUrl: String? = null,
    var age: Int? = 0
)

