package com.iodaniel.abbblinddating.data_class

data class ChatDisplayData(
    var mostRecentMessage: String = "",
    var timeSent: String = "",
    var timeReceived: String = "",
    var document: Map<String, String> = mapOf(),
    var myPersonData: PersonData = PersonData(),
    var otherPersonData: PersonData = PersonData(),
    var senderAuth: String = "",
    var receiverAuth: String = "",
)