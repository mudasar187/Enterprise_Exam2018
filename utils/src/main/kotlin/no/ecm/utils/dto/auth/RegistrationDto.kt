package no.ecm.utils.dto.auth
import java.io.Serializable


data class RegistrationDto(
        var username: String? = null,

        var password: String? = null,

        var secretPassword: String? = null,

        var dateOfBirth: String? = null,

        var name: String? = null,

        var email: String? = null
) : Serializable