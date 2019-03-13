package com.zeeshan.chatapp.model

import java.io.Serializable

data class User(
    var userId: String = "",
    var userName: String = "",
    var userEmail: String = "",
    var userBio: String? = null,
    var profileImageUrl: String? = null,
    var registrationToken: String? = null
):Serializable {
    override fun toString(): String {
        return "User(userName='$userName')"
    }

    override fun equals(other: Any?): Boolean {

        if (this === other) return true

        if (javaClass != other?.javaClass) return false

        other as User

        if (userId != other.userId) return false

        return true
    }
}