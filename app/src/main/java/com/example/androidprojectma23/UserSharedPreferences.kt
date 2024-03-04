package com.example.androidprojectma23

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

object UserSharedPreferences {
    private const val PREFS_NAME = "user_prefs"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveUserIds(context: Context, userId: String, userIds: Set<String>) {
        val userSpecificKey = "user_id_set_$userId"
        val existingUserIds = getUserIds(context, userId) ?: emptySet()
        val updatedUserIds = existingUserIds.union(userIds)
        val editor = getPreferences(context).edit()
        editor.putStringSet(userSpecificKey, updatedUserIds)
        editor.apply() // Using apply() for saving asynchronous
    }

    fun getUserIds(context: Context, userId: String): Set<String>? {
        val userSpecificKey = "user_id_set_$userId"
        return getPreferences(context).getStringSet(userSpecificKey, null)
    }

    // Call this from onCreate in a activity/fragment to clear all data.
    fun clearAllData(context: Context) {
        val editor = getPreferences(context).edit()
        editor.clear()
        editor.apply()
    }

}