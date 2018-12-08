package no.ecm.utils.dto.auth

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel("DTO representing user credentials")
data class AuthenticationDto(

        @ApiModelProperty("The username of an User")
        var username: String? = null,

        @ApiModelProperty("The password of an User")
        var password: String? = null,

        @ApiModelProperty("Secret password to create admin user")
        var secretPassword: String? = null
)