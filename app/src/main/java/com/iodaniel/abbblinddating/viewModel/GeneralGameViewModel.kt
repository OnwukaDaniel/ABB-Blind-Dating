package com.iodaniel.abbblinddating.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.iodaniel.abbblinddating.data_class.PersonData

class GeneralGameViewModel: ViewModel() {
    var personData = MutableLiveData<PersonData>()
    fun setPersonData(input: PersonData){
        personData.value = input
    }
}