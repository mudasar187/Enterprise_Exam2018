package no.ecm.utils.dto.user

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import java.io.Serializable

@ApiModel("DTO representing an UserInfo")
data class UserDto (

        @ApiModelProperty("Username")
        var username: String? = null,

        @ApiModelProperty("Date of Birth")
        var dateOfBirth: String? = null,

        @ApiModelProperty("Full name")
        var name: String? = null,

        @ApiModelProperty("Email")
        var email: String? = null

) : Serializable