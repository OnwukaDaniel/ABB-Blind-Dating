package com.iodaniel.abbblinddating.data_class

data class StatusData(
    var empty: Boolean = false,
    var lastMedia: String = "",
    var media: ArrayList<String> = arrayListOf(),
    var personData: PersonData = PersonData(),
)
