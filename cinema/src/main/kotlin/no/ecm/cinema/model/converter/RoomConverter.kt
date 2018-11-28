package no.ecm.cinema.model.converter

import no.ecm.cinema.model.entity.Room
import no.ecm.utils.dto.cinema.RoomDto
import no.ecm.utils.hal.PageDto
import no.ecm.utils.validation.ValidationHandler
import kotlin.streams.toList

object RoomConverter {
	
	fun entityToDto(entity: Room) : RoomDto {
		return RoomDto(
			id = entity.id.toString(),
			name = entity.name,
			seats =  entity.seats.toSet(),
			cinemaId = entity.cinema!!.id.toString())
	}
	
	fun dtoToEntity(dto: RoomDto) : Room {
		return Room(
			id = dto.id!!.toLong(),
			name = dto.name!!,
			seats = dto.seats!!.toMutableSet()
		)
	}
	
	fun entityListToDtoList(entities: Iterable<Room>): MutableList<RoomDto> {
		return entities.map { entityToDto(it) }.toMutableList()
	}

	fun dtoListToPageDto(cinemaList: List<RoomDto>,
						 offset: Int,
						 limit: Int): PageDto<RoomDto> {

		ValidationHandler.validateLimitAndOffset(offset,limit)

		val dtoList: MutableList<RoomDto> =
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