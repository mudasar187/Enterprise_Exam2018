package no.ecm.movie.model.converter

import no.ecm.movie.model.entity.NowPlaying
import no.ecm.utils.dto.movie.NowPlayingDto


object NowPlayingConverter {
	
	fun entityToDto(entity: NowPlaying) : NowPlayingDto {
		return NowPlayingDto(
			id = entity.id.toString(),
			movieDto = MovieConverter.nowPlayingEntityToDto(entity.movie!!),
			roomId = entity.roomId.toString(),
			time = entity.timeWhenMoviePlay,
			seats = entity.freeSeats.toList()
		)
	}
	
	fun dtoToEntity(dto: NowPlayingDto) : NowPlaying {
		return NowPlaying(
			movie = MovieConverter.dtoToEntity(dto.movieDto!!),
			roomId = dto.roomId!!.toLong(),
			timeWhenMoviePlay = dto.time!!,
			freeSeats = dto.seats!!.toMutableSet()
		)
	}

	fun movieEntityToDto(entity: NowPlaying) : NowPlayingDto {
		return NowPlayingDto(
				id = entity.id.toString(),
				roomId = entity.roomId.toString(),
				time = entity.timeWhenMoviePlay,
				seats = entity.freeSeats.toList()
		)
	}
	
	fun entityListToDtoList(entities: Iterable<NowPlaying>): MutableList<NowPlayingDto> {
		return entities.map { entityToDto(it) }.toMutableList()
	}
	
	fun dtoListToDtoList(dto: Iterable<NowPlayingDto>): List<NowPlaying> {
		return dto.map { dtoToEntity(it) }
	}
	
}