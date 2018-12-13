package no.ecm.movie.model.converter

import no.ecm.movie.model.entity.Movie
import no.ecm.utils.dto.movie.MovieDto

object MovieConverter {
	
	fun entityToDto(entity: Movie) : MovieDto {
		return MovieDto(
			id = entity.id.toString(),
			title = entity.title,
			posterUrl = entity.posterUrl,
			genre = GenreConverter.movieEntityListToDtoList(entity.genre).toMutableSet(),
			movieDuration = entity.movieDuration,
			ageLimit = entity.ageLimit
		)
	}
	
	fun dtoToEntity(dto: MovieDto) : Movie {
		return Movie (
			title = dto.title!!,
			posterUrl = dto.posterUrl!!,
			movieDuration = dto.movieDuration!!,
			ageLimit = dto.ageLimit!!
		)
	}

	fun genreEntityToDto(entity: Movie) : MovieDto {
		return MovieDto(
				id = entity.id.toString(),
				title = entity.title,
				posterUrl = entity.posterUrl,
				movieDuration = entity.movieDuration,
				ageLimit = entity.ageLimit
		)
	}

	fun nowPlayingEntityToDto(entity: Movie) : MovieDto {
		return MovieDto(
				id = entity.id.toString(),
				title = entity.title,
				posterUrl = entity.posterUrl,
				genre = GenreConverter.movieEntityListToDtoList(entity.genre).toMutableSet(),
				movieDuration = entity.movieDuration,
				ageLimit = entity.ageLimit
		)
	}
	
	fun entityListToDtoList(entities: Iterable<Movie>): MutableList<MovieDto> {
		return entities.map { entityToDto(it) }.toMutableList()
	}

	fun genreEntityListToDtoList(entities: Iterable<Movie>): List<MovieDto> {
		return entities.map { genreEntityToDto(it) }
	}
}