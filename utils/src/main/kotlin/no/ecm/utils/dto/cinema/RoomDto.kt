package no.ecm.utils.dto.cinema

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel("DTO representing an Movie playing now")
data class RoomDto (

        @ApiModelProperty("The id of a Room")
        var id: String? = null,

        @ApiModelProperty("The name of a Room")
        var name: String? = null,

        @ApiModelProperty("A List of all seats in a Room")
        var seats: Set<String>? = null,

        @ApiModelProperty("The id of the Cinema this Room belongs to")
        var cinemaId: String? = null
)