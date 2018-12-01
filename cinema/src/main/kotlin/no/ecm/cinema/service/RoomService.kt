package no.ecm.cinema.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.ecm.cinema.model.converter.RoomConverter
import no.ecm.cinema.model.entity.Cinema
import no.ecm.cinema.model.entity.Room
import no.ecm.cinema.repository.RoomRepository
import no.ecm.utils.dto.cinema.RoomDto
import no.ecm.utils.exception.ConflictException
import no.ecm.utils.messages.ExceptionMessages
import no.ecm.utils.exception.NotFoundException
import no.ecm.utils.exception.UserInputValidationException
import no.ecm.utils.logger
import no.ecm.utils.messages.InfoMessages
import no.ecm.utils.validation.ValidationHandler
import org.springframework.stereotype.Service


@Service
class RoomService(
        private var roomRepository: RoomRepository,
        private var cinemaService: CinemaService
) {

    val logger = logger<RoomService>()

    fun getAllRoomsFromCinemaByCinemaId(paramId: String?): MutableList<RoomDto> {

        val cinemaId = cinemaService.getCinemaById(paramId).id!!.toLong()

        val rooms = roomRepository.findAllByCinemaId(cinemaId).toMutableList()

        return RoomConverter.entityListToDtoList(rooms)

    }

    fun getRoomByIdAndCinemaId(paramCinemaId: String?, paramRoomId: String?): RoomDto {

        val cinemaId = cinemaService.getCinemaById(paramCinemaId).id!!.toLong()
        val roomId = ValidationHandler.validateId(paramRoomId, "room id")

        val room = try {
            roomRepository.findByIdAndCinemaId(roomId, cinemaId)
        } catch (e: Exception) {
            val errorMsg = ExceptionMessages.notFoundMessage("room", "id", "$paramRoomId")
            logger.warn(errorMsg)
            throw NotFoundException(errorMsg)
        }

        return RoomConverter.entityToDto(room)

    }

    fun createRoomForSpecificCinemaByCinemaId(paramCinemaId: String?, roomDto: RoomDto): RoomDto {

        val cinema = cinemaService.getCinemaById(paramCinemaId)

        when {
            roomDto.cinemaId != paramCinemaId -> {
                val errorMsg = ExceptionMessages.subIdNotMatchingParentId("cinemaId", "cinema id")
                logger.warn(errorMsg)
                throw UserInputValidationException(errorMsg)
            }
            !roomDto.id.isNullOrBlank() -> {
                val errorMsg = ExceptionMessages.illegalParameter("id")
                logger.warn(errorMsg)
                throw UserInputValidationException(errorMsg)
            }
            roomDto.name.isNullOrBlank() -> {
                val errorMsg = ExceptionMessages.missingRequiredField("name")
                logger.warn(errorMsg)
                throw UserInputValidationException(errorMsg)
            }
            roomDto.seats == null -> {
                val errorMsg = ExceptionMessages.missingRequiredField("seats")
                logger.warn(errorMsg)
                throw UserInputValidationException(errorMsg)
            }
            else -> {
                val roomExists = roomRepository.existsByName(roomDto.name.toString())

                when {
                    roomExists -> {
                        val errorMsg = ExceptionMessages.resourceAlreadyExists("Room", "name", "${roomDto.name}")
                        logger.warn(errorMsg)
                        throw ConflictException(errorMsg)
                    }
                    else -> {
                        roomDto.seats!!.forEach { ValidationHandler.validateSeatFormat(it) }
                        val room: Room = RoomConverter.dtoToEntity(roomDto)
                        room.cinema = cinema

                        val id = roomRepository.save(room).id.toString()
                        val infoMsg = InfoMessages.entityCreatedSuccessfully("room", "$id")
                        logger.info(infoMsg)
                        return RoomDto(id = id)

                    }
                }

            }
        }

    }

    fun patchUpdateRoomByIdAndCinemaId(paramCinemaId: String?, paramRoomId: String?, body: String?) {

        val array = checkRoomIdAndFindCinemaAndCheckIfRoomExists(paramRoomId, paramCinemaId)
        val roomId = array[0] as Long
        val cinema = array[1] as Cinema
        val isRoomExists = array[2] as Boolean

        when {
            !isRoomExists -> {
                val errorMsg = ExceptionMessages.notFoundMessage("room", "id", "$paramRoomId")
                logger.warn(errorMsg)
                throw NotFoundException(errorMsg)
            }
            else -> {
                val jackson = ObjectMapper()

                val jsonNode: JsonNode

                try {
                    jsonNode = jackson.readValue(body, JsonNode::class.java)
                } catch (e: Exception) {
                    val errorMsg = ExceptionMessages.invalidJsonFormat()
                    logger.warn(errorMsg)
                    throw UserInputValidationException(errorMsg)
                }

                val room = cinema.rooms.first { it.id == roomId }

                when {
                    jsonNode.has("id") -> {
                        val errorMsg = ExceptionMessages.illegalParameter("id")
                        logger.warn(errorMsg)
                        throw UserInputValidationException(errorMsg)
                    }
                    jsonNode.has("name") -> {
                        val name = jsonNode.get("name")
                        if (name.isTextual) {
                            room.name = name.asText()
                            val infoMsg = InfoMessages.entityFieldUpdatedSuccessfully("room", "${room.id}", "name")
                            logger.info(infoMsg)
                        } else {
                            val errorMsg = ExceptionMessages.unableToParse("name")
                            logger.warn(errorMsg)
                            throw UserInputValidationException(errorMsg)

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

                            seatsDto.forEach { ValidationHandler.validateSeatFormat(it) }
                            room.seats = seatsDto.toMutableSet()
                            val infoMsg = InfoMessages.entityFieldUpdatedSuccessfully("room", "${room.id}", "seats")
                            logger.info(infoMsg)

                        } else {
                            val errorMsg = ExceptionMessages.unableToParse("seats")
                            logger.warn(errorMsg)
                            throw UserInputValidationException(errorMsg)
                        }
                    }
                }

                roomRepository.save(room)
                val infoMsg = InfoMessages.entitySuccessfullyUpdated("room", "${room.id}")
                logger.info(infoMsg)
            }
        }

    }

    fun putUpdateRoomIdByCinemaId(paramCinemaId: String?, paramRoomId: String?, roomDto: RoomDto) {

        val array = checkRoomIdAndFindCinemaAndCheckIfRoomExists(paramRoomId, paramCinemaId)
        val roomId = array[0] as Long
        val cinema = array[1] as Cinema
        val isRoomExists = array[2] as Boolean

        when {
            !isRoomExists -> {
                val errorMsg = ExceptionMessages.notFoundMessage("room", "id", "$paramRoomId")
                logger.warn(errorMsg)
                throw NotFoundException(errorMsg)
            }
            roomDto.id != paramRoomId -> {
                val errorMsg = ExceptionMessages.notMachingIds("room id")
                logger.warn(errorMsg)
                throw UserInputValidationException(errorMsg)
            }
            roomDto.cinemaId != paramCinemaId -> {
                val errorMsg = ExceptionMessages.notMachingIds("cinema id")
                logger.warn(errorMsg)
                throw UserInputValidationException(errorMsg)
            }
            else -> {
                val room = cinema.rooms.first { it.id == roomId }

                roomDto.seats!!.forEach { ValidationHandler.validateSeatFormat(it) }

                room.name = roomDto.name!!
                room.seats = roomDto.seats!!.toMutableSet()

                room.cinema = cinema

                roomRepository.save(room)
                val infoMsg = InfoMessages.entitySuccessfullyUpdated("room", "${room.id}")
                logger.info(infoMsg)
            }
        }

    }

    fun deleteRoomByIdAndCinemaId(paramRoomId: String?, paramCinemaId: String?): String? {

        val array = checkRoomIdAndFindCinemaAndCheckIfRoomExists(paramRoomId, paramCinemaId)
        val roomId = array[0] as Long
        val cinema = array[1] as Cinema
        val isRoomExists = array[2] as Boolean

        when {
            !isRoomExists -> {
                val errorMsg = ExceptionMessages.notFoundMessage("room", "id", "$paramRoomId")
                logger.warn(errorMsg)
                throw NotFoundException(errorMsg)
            }
            else -> {
                val room = cinema.rooms.first { it.id == roomId }
                cinema.rooms.remove(room)

                roomRepository.deleteById(roomId)

                val infoMsg = InfoMessages.entitySuccessfullyDeleted("room", "${room.id}")
                logger.info(infoMsg)

                return roomId.toString()
            }
        }
    }

    private fun checkRoomIdAndFindCinemaAndCheckIfRoomExists(paramRoomId: String?, paramCinemaId: String?) : Array<Any> {

        val roomId = ValidationHandler.validateId(paramRoomId, "room id")

        val cinema = cinemaService.getCinemaById(paramCinemaId)

        val isRoomExists = roomRepository.existsByIdAndCinemaId(roomId, paramCinemaId!!.toLong())

        return arrayOf(roomId, cinema, isRoomExists)
    }

}