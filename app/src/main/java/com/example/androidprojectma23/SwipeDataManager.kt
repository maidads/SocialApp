package com.example.androidprojectma23

object SwipeDataManager {
    val leftSwipedUserIds: MutableList<String> = mutableListOf()

    fun addUser(userId: String) {
        leftSwipedUserIds.add(userId)
    }

    fun getAndClearUsers(): List<String> {
        val users = leftSwipedUserIds.toList() // Kopiera listan
        leftSwipedUserIds.clear() // Rensa den ursprungliga listan
        return users
    }
}
