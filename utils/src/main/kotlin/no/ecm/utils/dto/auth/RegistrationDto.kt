package no.ecm.utils.dto.auth
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import no.ecm.utils.dto.user.UserDto
import java.io.Serializable

@ApiModel("DTO representing an Registration")
data class RegistrationDto(

        @ApiModelProperty("Password")
        var password: String? = null,

        @ApiModelProperty("Secret password if want to create an admin")
        var secretPassword: String? = null,

        @ApiModelProperty("User details")
        var userInfo: UserDto? = null

) : Serializable