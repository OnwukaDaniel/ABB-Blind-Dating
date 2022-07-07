package com.iodaniel.abbblinddating.viewModel

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MessageDialogViewModel: ViewModel() {
    var positiveAction = MutableLiveData<Runnable>()
    var message = MutableLiveData<String>()
    var subMessage = MutableLiveData<String>()
    var inflateAction = MutableLiveData<Fragment>()
    fun setPositiveAction(input: Runnable){
        positiveAction.value = input
    }
    fun setInflateAction(input: Fragment){
        inflateAction.value = input
    }
    fun setMessage(input: String){
        message.value = input
    }
    fun setSubMessage(input: String){
        subMessage.value = input
    }
}