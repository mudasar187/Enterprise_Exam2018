package no.ecm.cinema.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.ecm.cinema.model.converter.RoomConverter
import no.ecm.cinema.model.entity.Room
import no.ecm.cinema.repository.RoomRepository
import no.ecm.utils.dto.cinema.RoomDto
import no.ecm.utils.exception.ConflictException
import no.ecm.utils.messages.ExceptionMessages
import no.ecm.utils.exception.NotFoundException
import no.ecm.utils.exception.UserInputValidationException
import no.ecm.utils.validation.ValidationHandler
import org.springframework.stereotype.Service


@Service
class RoomService(
        private var roomRepository: RoomRepository,
        private var cinemaService: CinemaService
) {

    fun getAllRoomsFromCinemaByCinemaId(paramId: String?): MutableList<RoomDto> {

        val id = ValidationHandler.validateId(paramId, "id")

        val rooms = when {
            !paramId.isNullOrBlank() -> roomRepository.findAllByCinemaId(id).toMutableList()
            else -> throw NotFoundException(ExceptionMessages.notFoundMessage("room", "cinema id", "$paramId"), 404)
        }

        when {
            rooms.size == 0 -> throw NotFoundException(ExceptionMessages.notFoundMessage("room", "cinema id", "$paramId"), 404)
            else -> return RoomConverter.entityListToDtoList(rooms)
        }

    }

    fun getRoomByIdAndCinemaId(paramCinemaId: String?, paramRoomId: String?): RoomDto {

        val cinemaId = ValidationHandler.validateId(paramCinemaId, "cinema id")
        val roomId = ValidationHandler.validateId(paramRoomId, "room id")

        val room = when {
            paramCinemaId.isNullOrBlank() -> throw UserInputValidationException(ExceptionMessages.invalidParameter("cinemaId", "$paramCinemaId"))
            paramRoomId.isNullOrBlank() -> throw UserInputValidationException(ExceptionMessages.invalidParameter("roomId", "$paramRoomId"))
            else -> try {
                roomRepository.findByIdAndCinemaId(roomId, cinemaId)
            } catch (e: Exception) {
                throw NotFoundException(ExceptionMessages.notFoundMessage("room", "id", "$paramRoomId"))
            }
        }

        return RoomConverter.entityToDto(room)

    }

    fun createRoomForSpecificCinemaByCinemaId(paramCinemaId: String?, roomDto: RoomDto): RoomDto {

        val cinema = cinemaService.getCinemaById(paramCinemaId)

        when {
            roomDto.cinemaId != paramCinemaId -> throw UserInputValidationException(ExceptionMessages.subIdNotMatchingParentId("cinema_id", "cinema id"))
            !roomDto.id.isNullOrBlank() -> throw UserInputValidationException(ExceptionMessages.illegalParameter("id"))
            roomDto.name.isNullOrBlank() -> throw UserInputValidationException(ExceptionMessages.missingRequiredField("name"))
            roomDto.seats == null -> throw UserInputValidationException(ExceptionMessages.missingRequiredField("seats"))
            else -> {
                val isExists = roomRepository.existsByName(roomDto.name.toString())

                when {
                    isExists -> throw ConflictException(ExceptionMessages.resourceAlreadyExists("Room", "name", "${roomDto.name}"))
                    else -> {
                        val room: Room = RoomConverter.dtoToEntity(roomDto)
                        room.cinema = cinema

                        return RoomDto(id = roomRepository.save(room).id.toString())

                    }
                }

            }
        }

    }

    fun patchUpdateRoomByIdAndCinemaId(paramCinemaId: String?, paramRoomId: String?, body: String?) {

        val roomId = ValidationHandler.validateId(paramRoomId, "room id")

        val cinema = cinemaService.getCinemaById(paramCinemaId)

        val isExists = roomRepository.existsByIdAndCinemaId(roomId, paramCinemaId!!.toLong())

        when {
            !isExists -> throw NotFoundException(ExceptionMessages.notFoundMessage("room", "id", "$paramRoomId"))
            else -> {
                val jackson = ObjectMapper()

                val jsonNode: JsonNode

                try {
                    jsonNode = jackson.readValue(body, JsonNode::class.java)
                } catch (e: Exception) {
                    throw UserInputValidationException("Invalid JSON object")
                }

                val room = cinema.rooms.first { it.id == roomId }

                when {
                    jsonNode.has("id") -> throw UserInputValidationException(ExceptionMessages.illegalParameter("id"))
                    jsonNode.has("name") -> {
                        val name = jsonNode.get("name")
                        if (name.isTextual) {
                            room.name = name.asText()
                        } else {
                            throw UserInputValidationException(ExceptionMessages.illegalParameter("Unable to handle field: 'name'"))

                        }
                    }
                }

                when {
                    jsonNode.has("seats") -> {
                        val seats = jsonNode.get("seats")
                        if (seats.isNull) room.seats = mutableSetOf()
                        else if (seats.isArray) {
                            val mapper = jacksonObjectMapper()
                            val seatsDto: Set<String> = mapper.readValue(seats.toString())

                            room.seats = seatsDto.toMutableSet()

                        } else throw UserInputValidationException("Unable to handle field: 'seats'")
                    }
                }

                roomRepository.save(room)
            }
        }

    }

    fun putUpdateRoomIdByCinemaId(paramCinemaId: String?, paramRoomId: String?, roomDto: RoomDto) {

        val roomId = ValidationHandler.validateId(paramRoomId, "room id")

        val cinema = cinemaService.getCinemaById(paramCinemaId)

        val isExists = roomRepository.existsByIdAndCinemaId(roomId, paramCinemaId!!.toLong())

        when {
            !isExists -> throw NotFoundException(ExceptionMessages.notFoundMessage("room", "id", "$paramRoomId"))
            roomDto.id != paramRoomId -> throw UserInputValidationException(ExceptionMessages.notMachingIds("room id"))
            roomDto.cinemaId != paramCinemaId -> throw UserInputValidationException(ExceptionMessages.notMachingIds("cinema id"))
            else -> {
                val room = cinema.rooms.first { it.id == roomId }

                room.name = roomDto.name!!
                room.seats = roomDto.seats!!.toMutableSet()

                room.cinema = cinema

                roomRepository.save(room)
            }
        }

    }

    fun deleteRoomByIdAndCinemaId(paramRoomId: String?, paramCinemaId: String?): String? {

        val roomId = ValidationHandler.validateId(paramRoomId, "room id")

        val cinema = cinemaService.getCinemaById(paramCinemaId)

        val isExists = roomRepository.existsByIdAndCinemaId(roomId, paramCinemaId!!.toLong())

        when {
            !isExists -> throw NotFoundException(ExceptionMessages.notFoundMessage("room", "id", "$paramRoomId"))
            else -> {
                val room = cinema.rooms.first { it.id == roomId }
                cinema.rooms.remove(room)

                roomRepository.deleteById(roomId)

                return roomId.toString()
            }
        }

    }




}