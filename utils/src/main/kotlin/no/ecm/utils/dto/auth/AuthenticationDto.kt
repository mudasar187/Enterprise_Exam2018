package no.ecm.utils.dto.auth

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel("DTO representing user credentials")
data class AuthenticationDto(

        @ApiModelProperty("The username of an User")
        var username: String? = null,

        @ApiModelProperty("The password of an User")
        var password: String? = null,

        @ApiModelProperty("The role of an User")
        var role: Set<String>? = null,

        @ApiModelProperty("An Users current state, can disable an user")
        var enabled: Boolean? = null
)