package no.ecm.cinema.model.converter

import no.ecm.cinema.model.entity.Room
import no.ecm.utils.dto.cinema.RoomDto

object RoomConverter {

    fun entityToDto(entity: Room): RoomDto {
        return RoomDto(
                id = entity.id.toString(),
                name = entity.name,
                seats = entity.seats.toSet(),
                cinemaId = entity.cinema!!.id.toString())
    }

    fun dtoToEntity(dto: RoomDto): Room {
        return Room(
                name = dto.name!!,
                seats = dto.seats!!.toMutableSet()
        )
    }

    fun entityListToDtoList(entities: Iterable<Room>): MutableList<RoomDto> {
        return entities.map { entityToDto(it) }.toMutableList()
    }

}