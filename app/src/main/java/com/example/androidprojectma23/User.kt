package com.example.androidprojectma23

data class User(
    val userId: String = "",
    val displayName: String = "",
    val profileImage: String = "",
    var interests: MutableList<String> = mutableListOf(),
    var commonInterests: MutableList<String> = mutableListOf(),
    val geohash: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

