package no.ecm.schema.movie

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel("DTO representing a movie genre")
data class GenreDto (
	
	@ApiModelProperty("The id of a genre")
	var id: String? = null,
	
	@ApiModelProperty("The name of the genre")
	var name: String? = null,
	
	@ApiModelProperty("Set of movies in a genre")
	var movies: MutableSet<MovieDto>? = null
)