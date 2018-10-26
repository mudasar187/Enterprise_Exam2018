package no.ecm.utils.dto.order

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import java.time.ZonedDateTime

@ApiModel("DTO representing an Order")
data class OrderDto (

        @ApiModelProperty("The id of an order")
        var id: String? = null,

        @ApiModelProperty("The id of a user that this order belong to")
        var username: String? = null,

        @ApiModelProperty("The date of an order")
        var orderDate: ZonedDateTime? = null,

        @ApiModelProperty("A coupon code for an order")
        var couponCode: String? = null,

        @ApiModelProperty("A coupon for an order")
        var nowPlayingId: String? = null,

        @ApiModelProperty("All tickets in an order")
        var tickets: List<TicketDto>? = null

)