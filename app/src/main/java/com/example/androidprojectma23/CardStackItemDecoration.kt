package com.example.androidprojectma23

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class CardStackItemDecoration(private val overlap: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        outRect.top = overlap * position
    }
}