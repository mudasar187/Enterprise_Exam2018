package no.ecm.utils.dto.user

import java.io.Serializable

data class UserDto (

        var username: String? = null,

        var dateOfBirth: String? = null,

        var name: String? = null,

        var email: String? = null
) : Serializable