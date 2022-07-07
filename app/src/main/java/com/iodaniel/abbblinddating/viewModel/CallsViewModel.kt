package com.iodaniel.abbblinddating.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.iodaniel.abbblinddating.data_class.PersonData

class CallsViewModel: ViewModel() {
    val personData = MutableLiveData<PersonData>()
    fun setPersonData(input: PersonData){
        personData.value = input
    }
}