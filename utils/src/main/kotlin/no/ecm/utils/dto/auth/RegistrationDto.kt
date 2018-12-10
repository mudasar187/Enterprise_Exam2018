package no.ecm.utils.dto.auth
import no.ecm.utils.dto.user.UserDto
import java.io.Serializable


data class RegistrationDto(

        var password: String? = null,

        var secretPassword: String? = null,

        var userInfo: UserDto? = null

) : Serializable