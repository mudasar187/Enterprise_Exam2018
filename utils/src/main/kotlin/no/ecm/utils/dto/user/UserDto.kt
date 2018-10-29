package no.ecm.utils.dto.user

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel("DTO representing a user")
data class UserDto (

        @ApiModelProperty("The id of a user")
        var username: String? = null,

        @ApiModelProperty("The age of a user")
        var age: Int? = null,

        @ApiModelProperty("The name of a user")
        var name: String? = null,

        @ApiModelProperty("The email of a user")
        var email: String? = null
)