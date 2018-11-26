package no.ecm.cinema.service

import no.ecm.cinema.model.converter.CinemaConverter
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

    fun get(paramName: String?, paramId: String?, offset: Int, limit: Int) : MutableList<CinemaDto> {

        if(offset < 0 || limit < 1) {
            throw UserInputValidationException(ExceptionMessages.offsetAndLimitInvalid(), 400)
        }

        val cinemas = if (!paramName.isNullOrEmpty() && !paramId.isNullOrEmpty()) {
            throw UserInputValidationException(ExceptionMessages.inputFilterInvalid())
        }
        else if (!paramName.isNullOrEmpty() && paramId.isNullOrEmpty()) {
            try {
                mutableListOf(cinemaRepository.findAllByNameIgnoreCase(paramName!!))
            } catch (e: Exception) {
                throw NotFoundException(ExceptionMessages.notFoundMessage("cinema", "name", "$paramName"), 404)
            }

        } else if (!paramId.isNullOrEmpty() && paramName.isNullOrEmpty()) {

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

        if (cinemaDto.name.isNullOrEmpty()) {
            throw UserInputValidationException(ExceptionMessages.missingRequiredField("name"))
        } else if (cinemaDto.location.isNullOrEmpty()) {
            throw UserInputValidationException(ExceptionMessages.missingRequiredField("location"))
        }

        val cinema = CinemaConverter.dtoToEntity(cinemaDto)

        return cinemaRepository.save(cinema).id.toString()
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