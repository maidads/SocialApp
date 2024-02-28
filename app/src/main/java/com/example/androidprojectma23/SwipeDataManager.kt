package com.example.androidprojectma23

object SwipeDataManager {
    private val leftSwipedUserIds: MutableList<String> = mutableListOf()

    fun addUser(userId: String) {
        leftSwipedUserIds.add(userId)
    }

    fun getAndClearUsers(): List<String> {
        val users = leftSwipedUserIds.toList() // Make a copy of the list
        leftSwipedUserIds.clear() // Clear the original list
        return users
    }
}
