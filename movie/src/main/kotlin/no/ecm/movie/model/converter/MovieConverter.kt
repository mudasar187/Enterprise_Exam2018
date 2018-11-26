package no.ecm.movie.model.converter

import no.ecm.movie.model.entity.Movie
import no.ecm.utils.dto.movie.MovieDto

object MovieConverter {
	
	fun entityToDto(entity: Movie) : MovieDto {
		return MovieDto(
			id = entity.id.toString(),
			movieName = entity.movieName,
			posterUrl = entity.posterURL,
			genre = GenreConverter.movieEntityListToDtoList(entity.genre).toMutableSet(),
			movieDuration = entity.movieDuration,
			ageLimit = entity.ageLimit,
			nowPlaying = NowPlayingConverter.movieEntityToDto(entity.nowPlaying!!)
		)
	}
	
	fun dtoToEntity(dto: MovieDto) : Movie {
		return Movie (
			movieName = dto.movieName!!,
			posterURL = dto.posterUrl!!,
			//genre = dto.genre!!,
			movieDuration = dto.movieDuration,
			ageLimit = dto.ageLimit!!
			//nowPlaying = NowPlayingConverter.dtoToEntity(dto.nowPlaying!!)
		)
	}

	fun genreEntityToDto(entity: Movie) : MovieDto {
		return MovieDto(
				id = entity.id.toString(),
				movieName = entity.movieName,
				posterUrl = entity.posterURL,
				movieDuration = entity.movieDuration,
				ageLimit = entity.ageLimit
		)
	}

	fun nowPlayingEntityToDto(entity: Movie) : MovieDto {
		return MovieDto(
				id = entity.id.toString(),
				movieName = entity.movieName,
				posterUrl = entity.posterURL,
				genre = GenreConverter.movieEntityListToDtoList(entity.genre).toMutableSet(),
				movieDuration = entity.movieDuration,
				ageLimit = entity.ageLimit
		)
	}
	
	fun entityListToDtoList(entities: Iterable<Movie>): List<MovieDto> {
		return entities.map { entityToDto(it) }
	}
	
	fun dtoListToDtoList(dto: Iterable<MovieDto>): List<Movie> {
		return dto.map { dtoToEntity(it) }
	}

	fun genreEntityListToDtoList(entities: Iterable<Movie>): List<MovieDto> {
		return entities.map { genreEntityToDto(it) }
	}
	
}