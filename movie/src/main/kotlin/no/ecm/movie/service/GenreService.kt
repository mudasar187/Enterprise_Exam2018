package no.ecm.movie.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import no.ecm.movie.model.converter.GenreConverter
import no.ecm.movie.model.entity.Genre
import no.ecm.movie.repository.GenreRepository
import no.ecm.utils.dto.movie.GenreDto
import no.ecm.utils.exception.ConflictException
import no.ecm.utils.exception.ExceptionMessages.Companion.illegalParameter
import no.ecm.utils.exception.ExceptionMessages.Companion.invalidIdParameter
import no.ecm.utils.exception.ExceptionMessages.Companion.invalidParameter
import no.ecm.utils.exception.ExceptionMessages.Companion.missingRequiredField
import no.ecm.utils.exception.ExceptionMessages.Companion.notFoundMessage
import no.ecm.utils.exception.ExceptionMessages.Companion.resourceAlreadyExists
import no.ecm.utils.exception.ExceptionMessages.Companion.toLargeOffset
import no.ecm.utils.exception.ExceptionMessages.Companion.unableToParse
import no.ecm.utils.exception.NotFoundException
import no.ecm.utils.exception.UserInputValidationException
import no.ecm.utils.hal.HalLink
import no.ecm.utils.logger
import no.ecm.utils.response.ResponseDto
import no.ecm.utils.response.WrappedResponse
import no.ecm.utils.validation.ValidationHandler.Companion.validateId
import no.ecm.utils.validation.ValidationHandler.Companion.validateLimitAndOffset
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder


@Service
class GenreService (
        private var genreRepository: GenreRepository
){

    val logger = logger<GenreService>()

    fun getGenres(name: String?): MutableList<GenreDto> {

        val genres = if (!name.isNullOrEmpty()){
            try {
                genreRepository.findByNameContainsIgnoreCase(name!!).toMutableList()
            } catch (e: Exception){
                val errorMsg = notFoundMessage("Genre", "name", name!!)
                logger.warn(errorMsg)
                throw NotFoundException(errorMsg)
            }
        } else {
            genreRepository.findAll().toMutableList()
        }

        if (genres.isEmpty()){
            throw NotFoundException(notFoundMessage("Genre", "name", name!!))
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

        if (genreDto.name.isNullOrEmpty()) {
            val errorMsg = missingRequiredField("name")
            logger.warn(errorMsg)
            throw UserInputValidationException(errorMsg)
        } else if (!genreDto.id.isNullOrEmpty()){
            handleIllegalField("id")
        } else if (genreDto.movies != null){
            handleIllegalField("movies")
        }

        if (genreRepository.existsByNameIgnoreCase(genreDto.name!!)){
            val errorMsg = (resourceAlreadyExists("Genre", "name", genreDto.name!!))
            logger.error(errorMsg)
            throw ConflictException(errorMsg)
        }

        genreDto.name = genreDto.name!!.capitalize()

        val genre = GenreConverter.dtoToEntity(genreDto)
        return GenreDto(id = genreRepository.save(genre).id.toString())
    }


    fun deleteGenre(stringId: String?): String? {
        val id = validateId(stringId, "id")

        if (!genreRepository.existsById(id)){
            val errorMsg = notFoundMessage("Genre", "id", stringId!!)
            logger.warn(errorMsg)
            throw NotFoundException(errorMsg)
        }
        
        genreRepository.deleteById(id)
        
        return id.toString()
    }

    fun patchGenre(stringId: String?, body: String?): GenreDto {

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
            logger.error(errorMsg)
            throw UserInputValidationException(errorMsg)
        }

        val genre = genreRepository.findById(id).get()

        if (jsonNode.has("id")){
            throw UserInputValidationException(illegalParameter("id"))
        }

        if (jsonNode.has("movies")){
            throw UserInputValidationException(illegalParameter("movies"))
        }

        if (jsonNode.has("name")) {
            val name = jsonNode.get("name")
            if (name.isTextual){
                genre.name = name.asText()
            } else {
                val errorMsg = unableToParse("name")
                logger.warn(errorMsg)
                throw UserInputValidationException(errorMsg)
            }
        }
        genreRepository.save(genre)

        return GenreConverter.entityToDto(genre, true)
    }
}