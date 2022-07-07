package com.iodaniel.abbblinddating.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FavViewModel : ViewModel() {
    var tab = MutableLiveData<Int>()
    fun setTab(input: Int) {
        tab.value = input
    }
}

object FavTabSelector{
    const val favourite = 0
    const val matches = 1
}