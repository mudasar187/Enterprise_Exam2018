package no.ecm.utils.dto.order

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel("DTO representing an Ticket")
data class TicketDto(

        @ApiModelProperty("The id of an Ticket")
        var id: String? = null,

        @ApiModelProperty("The price of an Ticket")
        var price: Double? = null,

        @ApiModelProperty("The seat of an Ticket")
        var seat: String? = null
)