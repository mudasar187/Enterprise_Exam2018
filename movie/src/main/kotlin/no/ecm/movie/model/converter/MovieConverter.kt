package no.ecm.movie.model.converter

import no.ecm.movie.model.entity.Movie
import no.ecm.utils.dto.movie.MovieDto
import no.ecm.utils.hal.PageDto
import no.ecm.utils.validation.ValidationHandler
import kotlin.streams.toList

object MovieConverter {
	
	fun entityToDto(entity: Movie) : MovieDto {
		return MovieDto(
			id = entity.id.toString(),
			title = entity.title,
			posterUrl = entity.posterUrl,
			genre = GenreConverter.movieEntityListToDtoList(entity.genre).toMutableSet(),
			movieDuration = entity.movieDuration,
			ageLimit = entity.ageLimit,
			nowPlaying = entity.nowPlaying?.let { NowPlayingConverter.movieEntityToDto(it) }
		)
	}
	
	fun dtoToEntity(dto: MovieDto) : Movie {
		return Movie (
			title = dto.title!!,
			posterUrl = dto.posterUrl!!,
			//genre = dto.genre!!,
			movieDuration = dto.movieDuration!!,
			ageLimit = dto.ageLimit!!
			//nowPlaying = NowPlayingConverter.dtoToEntity(dto.nowPlaying!!)
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
	
	fun dtoListToDtoList(dto: Iterable<MovieDto>): List<Movie> {
		return dto.map { dtoToEntity(it) }
	}

	fun genreEntityListToDtoList(entities: Iterable<Movie>): List<MovieDto> {
		return entities.map { genreEntityToDto(it) }
	}

	fun dtoListToPageDto(genreList: List<MovieDto>,
						 offset: Int,
						 limit: Int): PageDto<MovieDto> {

		ValidationHandler.validateLimitAndOffset(offset, limit)

		val dtoList: MutableList<MovieDto> =
				genreList.stream()
						.skip(offset.toLong())
						.limit(limit.toLong())
						.toList().toMutableList()

		return PageDto(
				list = dtoList,
				rangeMin = offset,
				rangeMax = offset + dtoList.size - 1,
				totalSize = genreList.size
		)

	}
	
}