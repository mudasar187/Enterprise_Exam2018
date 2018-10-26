package no.ecm.movie.model.converter

import no.ecm.movie.model.entity.Genre
import no.ecm.schema.movie.GenreDto

object GenreConverter {
	
	fun entityToDto(entity: Genre) : GenreDto {
		return GenreDto(
			id = entity.id.toString(),
			name = entity.name,
			movies = MovieConverter.entityListToDtoList(entity.movies).toMutableSet()
		)
	}
	
	fun dtoToEntity(dto: GenreDto) : Genre {
		return Genre(
			id = dto.id!!.toLong(),
			name = dto.name!!,
			movies = MovieConverter.dtoListToDtoList(dto.movies!!).toMutableSet()
		)
	}
	
	fun entityListToDtoList(entities: Iterable<Genre>): List<GenreDto> {
		return entities.map { entityToDto(it) }
	}
	
	fun dtoListToEntityList(dto: Iterable<GenreDto>): List<Genre> {
		return dto.map { dtoToEntity(it) }
	}
	
}