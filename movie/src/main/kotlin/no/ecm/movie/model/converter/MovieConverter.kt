package no.ecm.movie.model.converter

import no.ecm.movie.model.entity.Movie
import no.ecm.utils.dto.movie.MovieDto

object MovieConverter {
	
	fun entityToDto(entity: Movie) : MovieDto {
		return MovieDto(
			id = entity.id.toString(),
			movieName = entity.movieName,
			posterUrl = entity.posterURL,
			genre = GenreConverter.entityListToDtoList(entity.genre).toMutableSet(),
			movieDuration = entity.movieDuration,
			ageLimit = entity.ageLimit,
			nowPlaying = NowPlayingConverter.entityToDto(entity.nowPlaying!!)
		)
	}
	
	fun dtoToEntity(dto: MovieDto) : Movie {
		return Movie (
			id = dto.id!!.toLong(),
			movieName = dto.movieName!!,
			posterURL = dto.posterUrl!!,
			genre = GenreConverter.dtoListToEntityList(dto.genre!!).toMutableSet(),
			movieDuration = dto.movieDuration,
			ageLimit = dto.ageLimit!!,
			nowPlaying = NowPlayingConverter.dtoToEntity(dto.nowPlaying!!)
		)
	}
	
	fun entityListToDtoList(entities: Iterable<Movie>): List<MovieDto> {
		return entities.map { entityToDto(it) }
	}
	
	fun dtoListToDtoList(dto: Iterable<MovieDto>): List<Movie> {
		return dto.map { dtoToEntity(it) }
	}
	
}