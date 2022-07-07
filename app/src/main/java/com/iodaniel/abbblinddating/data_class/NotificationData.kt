package com.iodaniel.abbblinddating.data_class

data class NotificationData(
    var notificationMessage: String = "",
    var noMessages: Int = 0,
    var time: String = "",
    var document: Map<String, String> = mapOf(),
    var myPersonData: PersonData = PersonData(),
    var otherPersonData: PersonData = PersonData(),
)