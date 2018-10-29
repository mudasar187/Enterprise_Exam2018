package no.ecm.movie.model.converter

import no.ecm.movie.model.entity.NowPlaying
import no.ecm.utils.dto.movie.NowPlayingDto


object NowPlayingConverter {
	
	fun entityToDto(entity: NowPlaying) : NowPlayingDto {
		return NowPlayingDto(
			id = entity.id.toString(),
			movieDto = MovieConverter.entityToDto(entity.movie!!),
			roomId = entity.roomId.toString(),
			time = entity.timeWhenMoviePlay,
			seats = entity.freeSeats.toList()
		)
	}
	
	fun dtoToEntity(dto: NowPlayingDto) : NowPlaying {
		return NowPlaying(
			id = dto.id!!.toLong(),
			movie = MovieConverter.dtoToEntity(dto.movieDto!!),
			roomId = dto.roomId!!.toLong(),
			timeWhenMoviePlay = dto.time!!,
			freeSeats = dto.seats!!.toMutableSet()
		)
	}
	
	fun entityListToDtoList(entities: Iterable<NowPlaying>): List<NowPlayingDto> {
		return entities.map { entityToDto(it) }
	}
	
	fun dtoListToDtoList(dto: Iterable<NowPlayingDto>): List<NowPlaying> {
		return dto.map { dtoToEntity(it) }
	}
	
}