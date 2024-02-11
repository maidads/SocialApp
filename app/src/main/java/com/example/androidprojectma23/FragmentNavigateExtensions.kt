package com.example.androidprojectma23

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class FragmentNavigateExtensions {

    fun FragmentManager.navigateTo(fragment: Fragment, containerId: Int) {
        this.beginTransaction()
            .replace(containerId, fragment)
            .addToBackStack(null)
            .commit()
    }
}