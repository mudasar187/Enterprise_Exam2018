package no.ecm.utils.dto.cinema

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty

@ApiModel("DTO representing an Cinema")
data class CinemaDto (
	
	@ApiModelProperty("The id of a Cinema")
	var id: String? = null,
	
	@ApiModelProperty("The name of a Cinema")
	var name: String? = null
)