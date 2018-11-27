package no.ecm.cinema.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import no.ecm.cinema.model.converter.CinemaConverter
import no.ecm.cinema.model.entity.Cinema
import no.ecm.cinema.repository.CinemaRepository
import no.ecm.utils.dto.cinema.CinemaDto
import no.ecm.utils.exception.ExceptionMessages
import no.ecm.utils.exception.NotFoundException
import no.ecm.utils.exception.UserInputValidationException
import no.ecm.utils.validation.ValidationHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.data.crossstore.ChangeSetPersister
import org.springframework.stereotype.Service

@Service
class CinemaService {

    @Autowired
    private lateinit var cinemaRepository: CinemaRepository

    fun get(paramName: String?, paramLocation: String?, paramId: String?, offset: Int, limit: Int) : MutableList<CinemaDto> {

        if(offset < 0 || limit < 1) {
            throw UserInputValidationException(ExceptionMessages.offsetAndLimitInvalid(), 400)
        }

        val cinemas = if (!paramName.isNullOrEmpty() && !paramId.isNullOrEmpty() && !paramLocation.isNullOrEmpty()) {
            throw UserInputValidationException(ExceptionMessages.inputFilterInvalid())
        }
        else if (paramName.isNullOrEmpty() && paramId.isNullOrEmpty() && !paramLocation.isNullOrEmpty()) {
            try {
                mutableListOf(cinemaRepository.findAllByLocationContainingIgnoreCase(paramLocation!!))
            } catch (e: Exception) {
                throw NotFoundException(ExceptionMessages.notFoundMessage("cinema", "location", "$paramLocation"))
            }
        }
        else if (!paramName.isNullOrEmpty() && paramId.isNullOrEmpty() && paramLocation.isNullOrEmpty()) {
            try {
                mutableListOf(cinemaRepository.findAllByNameContainingIgnoreCase(paramName!!))
            } catch (e: Exception) {
                throw NotFoundException(ExceptionMessages.notFoundMessage("cinema", "name", "$paramName"), 404)
            }

        } else if (!paramId.isNullOrEmpty() && paramName.isNullOrEmpty() && paramLocation.isNullOrEmpty()) {

            val id = ValidationHandler.validateId(paramId)

            try {
                mutableListOf(cinemaRepository.findById(id).get())
            } catch (e: Exception) {
                throw NotFoundException(ExceptionMessages.notFoundMessage("cinema", "id", "$paramId"), 404)
            }
        } else {
            cinemaRepository.findAll().toMutableList()
        }

        return CinemaConverter.entityListToDtoList(cinemas)
    }

    fun createCinema(cinemaDto: CinemaDto): String {

        val cinema: Cinema

        if (cinemaDto.name.isNullOrEmpty()) {
            throw UserInputValidationException(ExceptionMessages.missingRequiredField("name"))
        } else if (cinemaDto.location.isNullOrEmpty()) {
            throw UserInputValidationException(ExceptionMessages.missingRequiredField("location"))
        }

        val isExists = cinemaRepository.existsByNameAndLocationIgnoreCase(cinemaDto.name.toString(), cinemaDto.location.toString())

        if(isExists) {
            throw UserInputValidationException("Cinema '${cinemaDto.name.toString()}' with location '${cinemaDto.location.toString()}' already exists", 409)
        } else {
            cinema = CinemaConverter.dtoToEntity(cinemaDto)
        }

        return cinemaRepository.save(cinema).id.toString()
    }

    fun putUpdateCinema(paramId: String?, cinemaDto: CinemaDto): String? {

        val id = ValidationHandler.validateId(paramId)

        if(!cinemaRepository.existsById(id)) {
            throw NotFoundException(ExceptionMessages.notFoundMessage("cinema", "id", "$id"))
        }

        if (cinemaDto.name.isNullOrEmpty()) {
            throw UserInputValidationException(ExceptionMessages.missingRequiredField("name"))
        } else if (cinemaDto.location.isNullOrEmpty()) {
            throw UserInputValidationException(ExceptionMessages.missingRequiredField("location"))
        }

        val cinema = cinemaRepository.findById(id).get()

        cinema.name = cinemaDto.name!!
        cinema.location = cinemaDto.location!!

        return cinemaRepository.save(cinema).id.toString()
    }

    fun patchUpdateCinema(paramId: String?, body: String?): CinemaDto {

        val id = ValidationHandler.validateId(paramId)

        if (!cinemaRepository.existsById(id)){
            throw NotFoundException(ExceptionMessages.notFoundMessage("cinema", "id", "$id"))
        }

        val jackson = ObjectMapper()

        val jsonNode: JsonNode

        try {
            jsonNode = jackson.readValue(body, JsonNode::class.java)
        } catch (e: Exception) {
            throw UserInputValidationException("Invalid JSON object")
        }

        val cinema = cinemaRepository.findById(id).get()

        if (jsonNode.has("name")) {
            val name = jsonNode.get("name")
            if (name.isTextual) {
                cinema.name = name.asText()
            } else {
                throw UserInputValidationException("Unable to handle field: 'name'")
            }
        }

        if(jsonNode.has("location")) {
            val location = jsonNode.get("location")
            if(location.isTextual) {
                cinema.location = location.asText()
            } else {
                throw UserInputValidationException("Unable to handle field: 'location")
            }
        }

        cinemaRepository.save(cinema)

        return CinemaConverter.entityToDto(cinema)


    }

    fun deleteCinema(paramId: String?): String? {

        val id = ValidationHandler.validateId(paramId)

        if (!cinemaRepository.existsById(id)){
            throw NotFoundException(ExceptionMessages.notFoundMessage("cinema", "id", "$id"))
        }

        cinemaRepository.deleteById(id)

        return id.toString()
    }

}