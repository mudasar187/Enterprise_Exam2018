package no.ecm.cinema.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import no.ecm.cinema.model.converter.CinemaConverter
import no.ecm.cinema.model.entity.Cinema
import no.ecm.cinema.repository.CinemaRepository
import no.ecm.utils.dto.cinema.CinemaDto
import no.ecm.utils.exception.ConflictException
import no.ecm.utils.messages.ExceptionMessages
import no.ecm.utils.exception.NotFoundException
import no.ecm.utils.exception.UserInputValidationException
import no.ecm.utils.logger
import no.ecm.utils.messages.InfoMessages
import no.ecm.utils.validation.ValidationHandler
import org.springframework.stereotype.Service

@Service
class CinemaService(
        private var cinemaRepository: CinemaRepository
) {

    val logger = logger<CinemaService>()

    fun get(paramName: String?, paramLocation: String?): MutableList<CinemaDto> {

        val cinemas = when {
            !paramName.isNullOrBlank() && !paramLocation.isNullOrBlank() -> {
                val errorMsg = ExceptionMessages.inputFilterInvalid()
                logger.warn(errorMsg)
                throw UserInputValidationException(errorMsg)
            }
            paramName.isNullOrBlank() && !paramLocation.isNullOrBlank() -> try {
                cinemaRepository.findAllByLocationContainingIgnoreCase(paramLocation!!).toMutableList()
            } catch (e: Exception) {
                val errorMsg = ExceptionMessages.notFoundMessage("cinema", "location", "$paramLocation")
                logger.warn(errorMsg)
                throw NotFoundException(errorMsg)
            }
            !paramName.isNullOrBlank() && paramLocation.isNullOrBlank() -> try {
                cinemaRepository.findAllByNameContainingIgnoreCase(paramName!!).toMutableList()
            } catch (e: Exception) {
                val errorMsg = ExceptionMessages.notFoundMessage("cinema", "name", "$paramName")
                logger.warn(errorMsg)
                throw NotFoundException(errorMsg)
            }
            else -> cinemaRepository.findAll().toMutableList()
        }

        return CinemaConverter.entityListToDtoList(cinemas, false)
    }

    fun getCinemaById(paramId: String?): Cinema {

        val id = ValidationHandler.validateId(paramId, "id")

        when {
            !cinemaRepository.existsById(id) -> {
                val errorMsg = ExceptionMessages.notFoundMessage("cinema", "id", "$paramId")
                logger.warn(errorMsg)
                throw NotFoundException(errorMsg)
            }
            else -> return cinemaRepository.findById(id).get()
        }

    }

    fun createCinema(cinemaDto: CinemaDto): CinemaDto {

        when {
            cinemaDto.name.isNullOrBlank() -> {
                val errorMsg = ExceptionMessages.missingRequiredField("name")
                logger.warn(errorMsg)
                throw UserInputValidationException(errorMsg)
            }
            cinemaDto.location.isNullOrBlank() -> {
                val errorMsg = ExceptionMessages.missingRequiredField("location")
                logger.warn(errorMsg)
                throw UserInputValidationException(errorMsg)
            }
            cinemaDto.rooms != null -> {
                val errorMsg = ExceptionMessages.illegalParameter("rooms")
                logger.warn(errorMsg)
                throw UserInputValidationException(errorMsg)
            }
            !cinemaDto.id.isNullOrBlank() -> {
                val errorMsg = ExceptionMessages.illegalParameter("id")
                logger.warn(errorMsg)
                throw UserInputValidationException(errorMsg)
            }
            else -> {
                val isExists = cinemaRepository.existsByNameAndLocationIgnoreCase(cinemaDto.name.toString(), cinemaDto.location.toString())

                when {
                    isExists -> {
                        val errorMsg = ExceptionMessages.resourceAlreadyExists("Cinema", "name & location", "${cinemaDto.name}, ${cinemaDto.location}")
                        logger.warn(errorMsg)
                        throw ConflictException(errorMsg)
                    }
                    else ->  {
                        val id = cinemaRepository.save(CinemaConverter.dtoToEntity(cinemaDto)).id.toString()
                        val infoMsg = InfoMessages.entityCreatedSuccessfully("cinema", "$id")
                        logger.info(infoMsg)
                        return CinemaDto(id = id)
                    }
                }

            }
        }

    }

    fun putUpdateCinema(paramId: String?, cinemaDto: CinemaDto) {

        val id = ValidationHandler.validateId(paramId, "id")

        when {
            !cinemaRepository.existsById(id) -> {
                val errorMsg = ExceptionMessages.notFoundMessage("cinema", "id", "$id")
                logger.warn(errorMsg)
                throw NotFoundException(errorMsg)
            }
            cinemaDto.id != paramId -> {
                val errorMsg = ExceptionMessages.notMachingIds()
                logger.warn(errorMsg)
                throw UserInputValidationException(errorMsg)
            }
            cinemaDto.name.isNullOrBlank() -> {
                val errorMsg = ExceptionMessages.missingRequiredField("name")
                logger.warn(errorMsg)
                throw UserInputValidationException(errorMsg)
            }
            cinemaDto.location.isNullOrBlank() -> {
                val errorMsg = ExceptionMessages.missingRequiredField("location")
                logger.warn(errorMsg)
                throw UserInputValidationException(errorMsg)
            }
            cinemaDto.rooms != null -> {
                val errorMsg = ExceptionMessages.illegalParameter("rooms")
                logger.warn(errorMsg)
                throw UserInputValidationException(errorMsg)
            }
            else -> {
                val cinema = cinemaRepository.findById(id).get()

                cinema.name = cinemaDto.name!!
                cinema.location = cinemaDto.location!!

                cinemaRepository.save(cinema)
                val infoMsg = InfoMessages.entitySuccessfullyUpdated("cinema", "${cinema.id}")
                logger.info(infoMsg)
            }
        }

    }

    fun patchUpdateCinema(paramId: String?, body: String?) {

        val id = ValidationHandler.validateId(paramId, "id")

        when {
            !cinemaRepository.existsById(id) -> {
                val errorMsg = ExceptionMessages.notFoundMessage("cinema", "id", "$id")
                logger.warn(errorMsg)
                throw NotFoundException(errorMsg)
            }
            else -> {
                val jackson = ObjectMapper()

                val jsonNode: JsonNode

                try {
                    jsonNode = jackson.readValue(body, JsonNode::class.java)
                } catch (e: Exception) {
                    val errorMsg = "Invalid JSON object"
                    logger.warn(errorMsg)
                    throw UserInputValidationException(errorMsg)
                }

                val cinema = cinemaRepository.findById(id).get()

                when {
                    jsonNode.has("id") -> {
                        val errorMsg = ExceptionMessages.illegalParameter("id")
                        logger.warn(errorMsg)
                        throw UserInputValidationException(errorMsg)
                    }
                    jsonNode.has("rooms") -> {
                        val rooms = jsonNode.get("rooms")
                        if (!rooms.isNull) {
                            throw UserInputValidationException(ExceptionMessages.illegalParameter("room"))
                        }
                    }
                }

                when {
                    jsonNode.has("name") -> {
                        val name = jsonNode.get("name")
                        if (name.isTextual) {
                            cinema.name = name.asText()
                        } else {
                            throw UserInputValidationException("Unable to handle field: 'name'")
                        }
                    }
                }

                when {
                    jsonNode.has("location") -> {
                        val location = jsonNode.get("location")
                        if (location.isTextual) {
                            cinema.location = location.asText()
                        } else {
                            throw UserInputValidationException("Unable to handle field: 'location")
                        }
                    }
                }

                cinemaRepository.save(cinema)
            }
        }
    }

    fun deleteCinemaById(paramId: String?): String? {

        val id = ValidationHandler.validateId(paramId, "id")

        when {
            !cinemaRepository.existsById(id) -> throw NotFoundException(ExceptionMessages.notFoundMessage("cinema", "id", "$id"))
            else -> {
                cinemaRepository.deleteById(id)

                return id.toString()
            }
        }

    }

}