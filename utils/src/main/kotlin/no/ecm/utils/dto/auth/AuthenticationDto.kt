package no.ecm.utils.dto.auth

data class AuthenticationDto(

        var username: String? = null,

        var password: String? = null,

        var secretPassword: String? = null
)