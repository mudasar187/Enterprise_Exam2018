package no.ecm.movie.model.converter

import no.ecm.movie.model.entity.NowPlaying
import no.ecm.utils.converter.ConvertionHandler
import no.ecm.utils.dto.movie.NowPlayingDto
import no.ecm.utils.validation.ValidationHandler


object NowPlayingConverter {
	
	fun entityToDto(entity: NowPlaying) : NowPlayingDto {
		return NowPlayingDto(
			id = entity.id.toString(),
			movieDto = MovieConverter.nowPlayingEntityToDto(entity.movie!!),
			cinemaId = entity.cinemaId.toString(),
			roomId = entity.roomId.toString(),
			time = entity.timeWhenMoviePlay.toString(),
			seats = entity.freeSeats.toList()
		)
	}
	
	fun dtoToEntity(dto: NowPlayingDto) : NowPlaying {
		return NowPlaying(
			cinemaId = dto.cinemaId!!.toLong(),
			roomId = dto.roomId!!.toLong(),
			timeWhenMoviePlay = ConvertionHandler.convertTimeStampToZonedTimeDate(
					ValidationHandler.validateTimeFormat("${dto.time!!}.000000"))!!
		)
	}
	
	fun entityListToDtoList(entities: Iterable<NowPlaying>): MutableList<NowPlayingDto> {
		return entities.map { entityToDto(it) }.toMutableList()
	}
	
}