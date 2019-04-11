package com.zeeshan.chatapp.model

class GroupChat(
    var groupId: String = "",   //Group  id
    var groupName: String = "",
    var groupAdminId: String = "",  //Group Admin
//    var groupMember : HashMap<String, String> //Group Member List
    var groupMember: ArrayList<String>? = null
) {
    constructor() : this("", "", "", null)
}