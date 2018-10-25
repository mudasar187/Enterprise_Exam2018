package no.ecm.schema.user

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel("DTO representing a user")
data class UserDto (

        @ApiModelProperty("The id of a user")
        var id: String? = null,

        @ApiModelProperty("The age of a user")
        var age: Int? = null,

        @ApiModelProperty("The name of a user")
        var name: Int? = null,

        @ApiModelProperty("The email of a user")
        var email: String? = null,

        @ApiModelProperty("The password of a user")
        var password: String? = null,

        @ApiModelProperty("A Coupon Code for discount")
        var couponCode: String? = null,

        @ApiModelProperty("A Credit card owend by a user")
        var creditCard: String? = null
        )