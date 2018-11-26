package no.ecm.cinema.model.converter

import no.ecm.cinema.model.entity.Cinema
import no.ecm.utils.dto.cinema.CinemaDto
import no.ecm.utils.hal.PageDto
import kotlin.streams.toList

object CinemaConverter{
	fun entityToDto (entity: Cinema): CinemaDto {
		return CinemaDto(
				id = entity.id.toString(),
				name = entity.name,
				location = entity.location
		)
	}

	fun dtoToEntity(dto: CinemaDto) : Cinema {
		return Cinema(
				name = dto.name!!,
				location = dto.location
		)
	}

	fun entityListToDtoList(entities: Iterable<Cinema>): MutableList<CinemaDto> {
		return entities.map { entityToDto(it) }.toMutableList()
	}

	fun dtoListToPageDto(cinemaList: List<CinemaDto>,
						 offset: Int,
						 limit: Int): PageDto<CinemaDto> {

		val dtoList: MutableList<CinemaDto> =
				cinemaList.stream()
						.skip(offset.toLong())
						.limit(limit.toLong())
						.toList().toMutableList()

		return PageDto(
				list = dtoList,
				rangeMin = offset,
				rangeMax = offset + dtoList.size - 1,
				totalSize = cinemaList.size
		)

	}
}