package no.ecm.cinema.service

import no.ecm.cinema.model.converter.RoomConverter
import no.ecm.cinema.model.entity.Room
import no.ecm.cinema.repository.RoomRepository
import no.ecm.utils.dto.cinema.RoomDto
import no.ecm.utils.exception.ConflictException
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

        if (rooms.size == 0) {
            throw NotFoundException(ExceptionMessages.notFoundMessage("room", "cinema id", "$paramId"), 404)
        }

        return RoomConverter.entityListToDtoList(rooms)
    }

    fun getSingleRoomFromCinema(paramCinemaId: String?, paramRoomId: String?): RoomDto {

        val cinemaId = ValidationHandler.validateId(paramCinemaId, "cinema id")
        val roomId = ValidationHandler.validateId(paramRoomId, "room id")

        val room = when {
            paramCinemaId.isNullOrEmpty() -> throw UserInputValidationException(ExceptionMessages.invalidParameter("cinemaId", "$paramCinemaId"))
            paramRoomId.isNullOrEmpty() -> throw UserInputValidationException(ExceptionMessages.invalidParameter("roomId", "$paramRoomId"))
            else -> try {
                roomRepository.findByIdAndCinemaId(roomId, cinemaId)
            } catch (e: Exception) {
                throw NotFoundException(ExceptionMessages.notFoundMessage("room", "id", "$paramRoomId"))
            }
        }

        return RoomConverter.entityToDto(room)

    }

    fun createRoomForSpecificCinema(paramCinemaId: String?, roomDto: RoomDto): RoomDto {

        val cinema = cinemaService.getCinemaById(paramCinemaId)

        if (roomDto.cinemaId != paramCinemaId) throw UserInputValidationException(ExceptionMessages.subIdNotMatchingParentId("cinema_id", "cinema id"))
        else if (!roomDto.id.isNullOrEmpty()) throw UserInputValidationException(ExceptionMessages.illegalParameter("id"))
        else if (roomDto.name.isNullOrEmpty()) throw UserInputValidationException(ExceptionMessages.missingRequiredField("name"))
        else if (roomDto.seats == null) throw UserInputValidationException(ExceptionMessages.missingRequiredField("seats"))
        else {
            val isExists = roomRepository.existsByName(roomDto.name.toString())

            if (isExists) {
                throw ConflictException(ExceptionMessages.resourceAlreadyExists("Room", "name", "${roomDto.name}"))

            }

            val room: Room = RoomConverter.dtoToEntity(roomDto)

            cinema.rooms.add(room)

            return RoomDto(id = roomRepository.save(room).id.toString())

        }

    }




}