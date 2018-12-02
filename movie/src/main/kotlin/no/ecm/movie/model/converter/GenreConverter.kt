package no.ecm.movie.model.converter

import no.ecm.movie.model.entity.Genre
import no.ecm.utils.dto.movie.GenreDto
import no.ecm.utils.hal.PageDto
import no.ecm.utils.validation.ValidationHandler
import kotlin.streams.toList

object GenreConverter {
	
	fun entityToDto(entity: Genre, loadMovies: Boolean) : GenreDto {
		val genre = GenreDto(
			id = entity.id.toString(),
			name = entity.name
		)
		if (loadMovies){
			genre.movies = MovieConverter.genreEntityListToDtoList(entity.movies).toMutableSet()
		}
		return genre
	}
	
	fun dtoToEntity(dto: GenreDto) : Genre {
		return Genre(
			name = dto.name!!
		)
	}

	fun movieEntityToDto(entity: Genre) : GenreDto {
		return GenreDto(
				id = entity.id.toString(),
				name = entity.name
		)
	}
	
	fun entityListToDtoList(entities: Iterable<Genre>, loadMovies: Boolean): MutableList<GenreDto> {
		return entities.map { entityToDto(it, loadMovies) }.toMutableList()
	}

	fun movieEntityListToDtoList(entities: Iterable<Genre>): List<GenreDto> {
		return entities.map { movieEntityToDto(it) }
	}
}