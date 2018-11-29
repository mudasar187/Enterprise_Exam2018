package no.ecm.movie.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import no.ecm.movie.model.converter.GenreConverter
import no.ecm.movie.model.entity.Genre
import no.ecm.movie.repository.GenreRepository
import no.ecm.utils.dto.movie.GenreDto
import no.ecm.utils.exception.ConflictException
import no.ecm.utils.messages.ExceptionMessages.Companion.illegalParameter
import no.ecm.utils.messages.ExceptionMessages.Companion.invalidParameter
import no.ecm.utils.messages.ExceptionMessages.Companion.missingRequiredField
import no.ecm.utils.messages.ExceptionMessages.Companion.notFoundMessage
import no.ecm.utils.messages.ExceptionMessages.Companion.resourceAlreadyExists
import no.ecm.utils.messages.ExceptionMessages.Companion.unableToParse
import no.ecm.utils.exception.NotFoundException
import no.ecm.utils.exception.UserInputValidationException
import no.ecm.utils.logger
import no.ecm.utils.messages.InfoMessages.Companion.entityCreatedSuccessfully
import no.ecm.utils.messages.InfoMessages.Companion.entityFieldUpdatedSuccessfully
import no.ecm.utils.messages.InfoMessages.Companion.entitySuccessfullyDeleted
import no.ecm.utils.messages.InfoMessages.Companion.entitySuccessfullyUpdated
import no.ecm.utils.validation.ValidationHandler.Companion.validateId
import org.springframework.stereotype.Service


@Service
class GenreService (
        private var genreRepository: GenreRepository
){

    val logger = logger<GenreService>()

    fun getGenres(name: String?): MutableList<GenreDto> {
        val genres = if (!name.isNullOrBlank()){
            genreRepository.findAllByNameContainsIgnoreCase(name!!).toMutableList()
        } else {
            genreRepository.findAll().toMutableList()
        }
        return GenreConverter.entityListToDtoList(genres, false)
    }

    fun getGenre(stringId: String?): Genre {
        val id = validateId(stringId, "id")

        if (!genreRepository.existsById(id)){
            val errorMsg = notFoundMessage("Genre", "id", stringId!!)
            logger.warn(errorMsg)
            throw NotFoundException(errorMsg)
        }
        
        return genreRepository.findById(id).get()
    }

    private fun handleIllegalField(fieldName: String){
        val errorMsg = illegalParameter(fieldName)
        logger.warn(errorMsg)
        throw UserInputValidationException(errorMsg)
    }

    fun createGenre(genreDto: GenreDto): GenreDto {

        validateGenreDto(genreDto)

        if (!genreDto.id.isNullOrEmpty()){
            handleIllegalField("id")
        }

        if (genreRepository.existsByNameIgnoreCase(genreDto.name!!)){
            val errorMsg = resourceAlreadyExists("Genre", "name", genreDto.name!!)
            logger.warn(errorMsg)
            throw ConflictException(errorMsg)
        }

        genreDto.name = genreDto.name!!.capitalize()

        val genre = GenreConverter.dtoToEntity(genreDto)
        val id = genreRepository.save(genre).id.toString()
        logger.info(entityCreatedSuccessfully("Genre", id))
        return GenreDto(id = id)
    }

    fun deleteGenre(stringId: String?): String? {
        val id = validateId(stringId, "id")

        if (!genreRepository.existsById(id)){
            val errorMsg = notFoundMessage("Genre", "id", stringId!!)
            logger.warn(errorMsg)
            throw NotFoundException(errorMsg)
        }
        
        genreRepository.deleteById(id)
        logger.info(entitySuccessfullyDeleted("Genre", id.toString()))

        return id.toString()
    }

    fun patchGenre(stringId: String?, body: String?) {

        val id = validateId(stringId, "id")

        if (!genreRepository.existsById(id)){
            val errorMsg = notFoundMessage("Genre", "id", stringId!!)
            logger.warn(errorMsg)
            throw NotFoundException(errorMsg)
        }

        val jackson = ObjectMapper()

        val jsonNode: JsonNode

        try {
            jsonNode = jackson.readValue(body, JsonNode::class.java)
        } catch (e: Exception) {
            val errorMsg = invalidParameter("JSON", "invalid JSON object")
            logger.warn(errorMsg)
            throw UserInputValidationException(errorMsg)
        }

        val genre = genreRepository.findById(id).get()

        when {
            jsonNode.has("id") -> {
                val errorMsg = illegalParameter("id")
                logger.warn(errorMsg)
                throw UserInputValidationException(errorMsg)
            }
            jsonNode.has("movies") -> {
                val errorMsg = illegalParameter("movies")
                logger.warn(errorMsg)
                throw UserInputValidationException(errorMsg)
            }
            !jsonNode.has("name") -> {
                val errorMsg = missingRequiredField("name")
                logger.warn(errorMsg)
                throw UserInputValidationException(errorMsg)
            }
            jsonNode.has("name") -> {
                val name = jsonNode.get("name")
                if (name.isTextual){
                    genre.name = name.asText()
                    logger.info(entityFieldUpdatedSuccessfully("Genre", genre.id.toString(), "name"))
                } else {
                    val errorMsg = unableToParse("name")
                    logger.warn(errorMsg)
                    throw UserInputValidationException(errorMsg)
                }
            }
        }

        genreRepository.save(genre)
        logger.info(entitySuccessfullyUpdated("Genre", genre.id.toString()))
    }

    fun putGenre(stringId: String?, genreDto: GenreDto) {

        validateId(stringId, "id")
        val genre = getGenre(stringId)

        validateGenreDto(genreDto)
        if (genreDto.id.isNullOrEmpty()){
            val errorMsg = missingRequiredField("id")
            logger.warn(errorMsg)
            throw UserInputValidationException(errorMsg)
        }

        if (!stringId.equals(genreDto.id)){
            val errorMsg = invalidParameter(stringId!!, genreDto.id!!)
            logger.warn(errorMsg)
            throw UserInputValidationException(errorMsg)
        }

        genre.name = genreDto.name!!.capitalize()
        genreRepository.save(genre)
        logger.info(entitySuccessfullyUpdated("Genre", genre.id.toString()))
    }

    private fun validateGenreDto(genreDto: GenreDto) {
        if (genreDto.name.isNullOrEmpty()) {
            val errorMsg = missingRequiredField("name")
            logger.warn(errorMsg)
            throw UserInputValidationException(errorMsg)
        } else if (genreDto.movies != null){
            handleIllegalField("movies")
        }
    }
}