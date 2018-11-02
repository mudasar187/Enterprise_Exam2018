package no.ecm.utils.dto.movie

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel("DTO representing an Movie")
data class MovieDto (
	
	@ApiModelProperty("The id of a movie")
	var id: String? = null,
	
	@ApiModelProperty("The name of the movie")
	var movieName: String? = null,
	
	@ApiModelProperty("The URL of a poster")
	var posterUrl: String? = null,
	
	@ApiModelProperty("The GenreDto of the movie")
	var genre: MutableSet<String>? = null,
	
	@ApiModelProperty("The duration of the movie in minutes")
	var movieDuration: Int,
	
	@ApiModelProperty("The age limit of the movie")
	var ageLimit: Int? = null,
	
	@ApiModelProperty("The NowPlayingDto of the movie ")
	var nowPlaying: NowPlayingDto? = null

)