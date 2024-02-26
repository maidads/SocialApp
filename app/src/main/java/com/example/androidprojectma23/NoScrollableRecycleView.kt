package com.example.androidprojectma23

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager

class NoScrollableRecycleView(context: Context) : LinearLayoutManager(context) {

    override fun canScrollVertically(): Boolean {
        // Return false to inactivate vertical scroll
        return false
    }
}