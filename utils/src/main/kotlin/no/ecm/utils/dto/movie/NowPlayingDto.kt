package no.ecm.utils.dto.movie

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel("DTO representing an Movie playing now")
data class NowPlayingDto(
    
    @ApiModelProperty("The id of a now playing movie")
    var id: String? = null,
    
    @ApiModelProperty("The id of a movie")
    var movieDto: MovieDto? = null,
    
    @ApiModelProperty("The id of a room")
    var roomId: String? = null,

    @ApiModelProperty("The id of a cinema")
    var cinemaId: String? = null,

    @ApiModelProperty("The name of a cinema")
    var cinemaName: String? = null,
    
    @ApiModelProperty("The time and date for when a movie is going to be displayed")
    var time: String? = null,
    
    @ApiModelProperty("All free seats for this display")
    var seats: List<String>? = null
)