package no.ecm.cinema.service

import no.ecm.cinema.model.converter.RoomConverter
import no.ecm.cinema.repository.RoomRepository
import no.ecm.utils.dto.cinema.RoomDto
import no.ecm.utils.exception.ExceptionMessages
import no.ecm.utils.exception.NotFoundException
import no.ecm.utils.exception.UserInputValidationException
import no.ecm.utils.validation.ValidationHandler
import org.springframework.stereotype.Service

@Service
class RoomService(
        private var roomRepository: RoomRepository,
        private var cinemaService: CinemaService
) {

    fun getAllRoomsFromCinema(paramId: String?): MutableList<RoomDto> {

        val id = ValidationHandler.validateId(paramId, "id")

        val rooms = if (!paramId.isNullOrEmpty()) {
            roomRepository.findAllByCinemaId(id).toMutableList()
        } else {
            throw NotFoundException(ExceptionMessages.notFoundMessage("room", "cinema id", "$paramId"), 404)
        }

        if(rooms.size == 0) {
            throw NotFoundException(ExceptionMessages.notFoundMessage("room", "cinema id", "$paramId"), 404)
        }

        return RoomConverter.entityListToDtoList(rooms)
    }

    fun getSingleRoomFromCinema(paramCinemaId: String?, paramRoomId: String?) : RoomDto {

        val cinemaId = ValidationHandler.validateId(paramCinemaId, "cinema id")
        val roomId = ValidationHandler.validateId(paramRoomId, "room id")

        val room = if(paramCinemaId.isNullOrEmpty()) {
            throw UserInputValidationException(ExceptionMessages.invalidParameter("cinemaId", "$paramCinemaId"))
        } else if (paramRoomId.isNullOrEmpty()) {
            throw UserInputValidationException(ExceptionMessages.invalidParameter("roomId", "$paramRoomId"))
        } else {
            try {
                roomRepository.findByIdAndCinemaId(roomId, cinemaId)
            } catch (e: Exception) {
                throw NotFoundException(ExceptionMessages.notFoundMessage("room", "id", "$paramRoomId"))
            }
        }

        return RoomConverter.entityToDto(room)

    }


}