package com.iodaniel.abbblinddating.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.iodaniel.abbblinddating.data_class.PersonData

class ProfileViewModel : ViewModel() {
    var personData = MutableLiveData<PersonData>()
    var favData = MutableLiveData<ArrayList<PersonData>>()
    var personDataList = MutableLiveData<ArrayList<PersonData>>()
    fun setPersonData(input: PersonData) {
        personData.value = input
    }
    fun setFavData(input: ArrayList<PersonData>) {
        favData.value = input
    }
    fun setPersonDataList(input: ArrayList<PersonData>) {
        personDataList.value = input
    }
}