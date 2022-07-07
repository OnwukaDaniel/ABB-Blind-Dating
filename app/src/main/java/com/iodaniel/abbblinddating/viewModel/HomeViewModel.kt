package com.iodaniel.abbblinddating.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.iodaniel.abbblinddating.data_class.PersonData

class HomeViewModel : ViewModel() {
    var tab = MutableLiveData<Int>()
    var myProfileData = MutableLiveData<PersonData>()
    fun setTab(input: Int) {
        tab.value = input
    }
    fun setMyProfileData(input: PersonData) {
        myProfileData.value = input
    }
}

object HomeTabSelector {
    const val discover = 0
    const val fav = 1
    const val update = 2
    const val msg = 3
    const val profile = 4
}