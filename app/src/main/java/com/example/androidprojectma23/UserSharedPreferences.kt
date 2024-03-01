package com.example.androidprojectma23

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
object UserSharedPreferences {
    private const val PREFS_NAME = "user_prefs"
    private const val USER_ID_SET_KEY = "user_id_set"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveUserIds(context: Context, userIds: Set<String>) {
        val existingUserIds = getUserIds(context) ?: emptySet()
        val updatedUserIds = existingUserIds.union(userIds)
        val editor = getPreferences(context).edit()
        editor.putStringSet(USER_ID_SET_KEY, updatedUserIds)
        editor.apply() // Using apply() for saving asynchronous
        Log.d("UserSharedPreferences", "Sparade användar-ID:n: $userIds")
    }

    fun getUserIds(context: Context): Set<String>? {
        val userIds = getPreferences(context).getStringSet(USER_ID_SET_KEY, null)
        Log.d("UserSharedPreferences", "Återhämtade sparade användar-ID:n: $userIds")
        return userIds
    }

    // Call this from onCreate in a activity/fragment to clear all data.
    fun clearAllData(context: Context) {
        val editor = getPreferences(context).edit()
        editor.clear()
        editor.apply()
        Log.d("UserSharedPreferences", "All användardata har rensats.")
    }

}