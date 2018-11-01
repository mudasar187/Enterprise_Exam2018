package no.ecm.utils.dto.user

import java.time.LocalDate

data class UserDto (

        var username: String? = null,

        var dateOfBirth: LocalDate? = null,

        var name: String? = null,

        var email: String? = null
)