package com.iodaniel.abbblinddating.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignUpViewModel: ViewModel() {
    var resendTimer = MutableLiveData<String>()
    var showResend = MutableLiveData<Boolean>()
    var emailPasswordPair = MutableLiveData<Map<String, String>>()
    var activateResend = MutableLiveData<Boolean>()
    var birthday = MutableLiveData<Map<String, Int>>()
    var name = MutableLiveData<String>()
    var phone = MutableLiveData<String>()

    var university = MutableLiveData<String>()
    var highSchool = MutableLiveData<String>()

    fun setName(input: String){
        name.value = input
    }

    fun setPhone(input: String){
        phone.value = input
    }

    fun setUniversity(input: String){
        university.value = input
    }

    fun setHighSchool(input: String){
        highSchool.value = input
    }

    fun setEmailPasswordPair(input: Map<String, String>){
        emailPasswordPair.value = input
    }

    fun setBirthday(input: Map<String, Int>){
        birthday.value = input
    }

    fun setResendTimer(input: String){
        resendTimer.value = input
    }
    fun setShowResend(input: Boolean){
        showResend.value = input
    }
    fun setActivateResend(input: Boolean){
        activateResend.value = input
    }

    // -------------------------------------------
    var interests = MutableLiveData<ArrayList<String>>()
    var language = MutableLiveData<ArrayList<String>>()
    var friendship = MutableLiveData<String>()
    var maritalStatus = MutableLiveData<String>()
    var bodyStyle = MutableLiveData<String>()
    var dateSmoker = MutableLiveData<String>()
    var about = MutableLiveData<String>()
    var education = MutableLiveData<String>()
    var hairColor = MutableLiveData<String>()
    var gender = MutableLiveData<String>()

    fun setGender(input: String){
        gender.value = input
    }

    fun setHairColor(input: String){
        hairColor.value = input
    }

    fun setAbout(input: String){
        about.value = input
    }

    fun setEducation(input: String){
        education.value = input
    }

    fun setDateSmoker(input: String){
        dateSmoker.value = input
    }

    fun setFriendship(input: String){
        friendship.value = input
    }

    fun setBodyStyle(input: String){
        bodyStyle.value = input
    }

    fun setInterests(input: ArrayList<String>){
        interests.value = input
    }

    fun setLanguage(input: ArrayList<String>){
        language.value = input
    }

    fun setMaritalStatus(input: String){
        maritalStatus.value = input
    }
}