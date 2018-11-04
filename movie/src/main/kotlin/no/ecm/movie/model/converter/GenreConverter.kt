package no.ecm.movie.model.converter

import no.ecm.movie.model.entity.Genre
import no.ecm.utils.dto.movie.GenreDto

object GenreConverter {
	
	fun entityToDto(entity: Genre) : GenreDto {
		return GenreDto(
			id = entity.id.toString(),
			name = entity.name,
			movies = MovieConverter.genreEntityListToDtoList(entity.movies).toMutableSet()
		)
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
	
	fun entityListToDtoList(entities: Iterable<Genre>): List<GenreDto> {
		return entities.map { entityToDto(it) }
	}
	
	fun dtoListToEntityList(dto: Iterable<GenreDto>): List<Genre> {
		return dto.map { dtoToEntity(it) }
	}

	fun movieEntityListToDtoList(entities: Iterable<Genre>): List<GenreDto> {
		return entities.map { movieEntityToDto(it) }
	}
	
}