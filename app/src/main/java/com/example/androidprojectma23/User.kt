package com.example.androidprojectma23

data class User(
    var userId: String = "",
    var displayName: String = "",
    var profileImage: String = "",
    var interests: MutableList<String> = mutableListOf(),
    var commonInterests: MutableList<String> = mutableListOf(),
    val geohash: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    var about: String = "",
    var myInterests: String = "",
    val profileImageUrl: String? = null,
    var age: String = ""
)

