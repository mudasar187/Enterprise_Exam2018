package no.ecm.cinema.model.converter

import no.ecm.cinema.model.entity.Room
import no.ecm.schema.cinema.RoomDto

object RoomConverter {
	
	fun entityToDto(entity: Room) : RoomDto {
		return RoomDto(
			id = entity.id.toString(),
			name = entity.name,
			seats =  entity.seats.toSet(),
			cinemaId = entity.cinemaId!!.toString())
	}
	
	fun dtoToEntity(dto: RoomDto) : Room {
		return Room(
			id = dto.id!!.toLong(),
			name = dto.name!!,
			seats = dto.seats!!.toMutableSet(),
			cinemaId = dto.cinemaId!!.toLong()
		)
	}
	
	fun entityListToDtoList(entities: Iterable<Room>): List<RoomDto> {
		return entities.map { entityToDto(it) }
	}
}