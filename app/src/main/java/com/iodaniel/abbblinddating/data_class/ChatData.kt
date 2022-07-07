package com.iodaniel.abbblinddating.data_class

data class  ChatData(
    var message: String = "",
    var timeSent: String = "",
    var timeDelivered: String = "",
    var timeSeen: String = "",
    var document: Map<String, String> = mapOf(),
    var reaction: String = "",
    var senderName: String = "",
    var receiverName: String = "",
    var senderAuth: String = "",
    var receiverAuth: String = "",
    var chatTimeKey: String = "",
)