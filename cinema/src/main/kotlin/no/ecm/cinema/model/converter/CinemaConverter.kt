package no.ecm.cinema.model.converter

import no.ecm.cinema.model.entity.Cinema
import no.ecm.utils.dto.cinema.CinemaDto
import no.ecm.utils.hal.PageDto
import no.ecm.utils.validation.ValidationHandler
import kotlin.streams.toList

object CinemaConverter {
    fun entityToDto(entity: Cinema, loadRooms: Boolean): CinemaDto {
        val cinema = CinemaDto(
                id = entity.id.toString(),
                name = entity.name,
                location = entity.location
        )

        if (loadRooms) {
            cinema.rooms = RoomConverter.entityListToDtoList(entity.rooms).toMutableList()
        }

        return cinema
    }

    fun dtoToEntity(dto: CinemaDto): Cinema {
        return Cinema(
                name = dto.name!!,
                location = dto.location
        )
    }

    fun entityListToDtoList(entities: Iterable<Cinema>, loadRooms: Boolean): MutableList<CinemaDto> {
        return entities.map { entityToDto(it, loadRooms) }.toMutableList()
    }

    fun dtoListToPageDto(cinemaList: List<CinemaDto>,
                         offset: Int,
                         limit: Int): PageDto<CinemaDto> {

        ValidationHandler.validateLimitAndOffset(offset, limit)

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