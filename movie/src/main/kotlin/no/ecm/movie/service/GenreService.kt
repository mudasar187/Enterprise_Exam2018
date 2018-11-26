package no.ecm.movie.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import no.ecm.movie.model.converter.GenreConverter
import no.ecm.movie.repository.GenreRepository
import no.ecm.utils.dto.movie.GenreDto
import no.ecm.utils.exception.ConflictException
import no.ecm.utils.exception.ExceptionMessages
import no.ecm.utils.exception.ExceptionMessages.Companion.illegalParameter
import no.ecm.utils.exception.ExceptionMessages.Companion.invalidIdParameter
import no.ecm.utils.exception.ExceptionMessages.Companion.invalidParameter
import no.ecm.utils.exception.ExceptionMessages.Companion.missingRequiredField
import no.ecm.utils.exception.ExceptionMessages.Companion.notFoundMessage
import no.ecm.utils.exception.ExceptionMessages.Companion.resourceAlreadyExists
import no.ecm.utils.exception.ExceptionMessages.Companion.unableToParse
import no.ecm.utils.exception.NotFoundException
import no.ecm.utils.exception.UserInputValidationException
import no.ecm.utils.logger
import org.springframework.stereotype.Service
import kotlin.math.log


@Service
class GenreService (
        private var genreRepository: GenreRepository
){

    val logger = logger<GenreService>()

    fun getGenres(name: String?): MutableList<GenreDto> {

        val genres = if (!name.isNullOrEmpty()){
            try {
                mutableListOf(genreRepository.findByName(name!!))
            } catch (e: Exception){
                val errorMsg = notFoundMessage("Genre", "name", name!!)
                logger.warn(errorMsg)
                throw NotFoundException(errorMsg)
            }
        } else {
            genreRepository.findAll().toMutableList()
        }

        return GenreConverter.entityListToDtoList(genres, false)
    }

    //TODO maybe return entity in stead of dto
    fun getGenre(stringId: String?): GenreDto {

        val id = validateId(stringId)

        if (!genreRepository.existsById(id)){
            val errorMsg = notFoundMessage("Genre", "id", stringId!!)
            logger.warn(errorMsg)
            throw NotFoundException(errorMsg)
        }
        
        val genre = genreRepository.findById(id).get()
        
        return GenreConverter.entityToDto(genre, true)
    }

    private fun handleIllegalField(fieldName: String){
        val errorMsg = illegalParameter(fieldName)
        logger.warn(errorMsg)
        throw UserInputValidationException(errorMsg)
    }

    fun createGenre(genreDto: GenreDto): String {

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

        return genreRepository.save(genre).id.toString()
    }


    fun deleteGenre(stringId: String?): String? {

        val id = validateId(stringId)

        if (!genreRepository.existsById(id)){
            val errorMsg = notFoundMessage("Genre", "id", stringId!!)
            logger.warn(errorMsg)
            throw NotFoundException(errorMsg)
        }
        
        genreRepository.deleteById(id)
        
        return id.toString()
    }

    fun updateGenre(stringId: String?, body: String?): GenreDto {

        val id = validateId(stringId)

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

        //TODO kanskje ikke la Genre oppdatere dette? kun via movie
//        if (jsonNode.has("movies")) {
//            val movies = jsonNode.get("movies")
//            when {
//                movies.isNull -> genre.movies = mutableSetOf()
//                movies.isArray -> {
//                    val mapper = jacksonObjectMapper()
//                    val tmp: Set<MovieDto> = mapper.readValue(movies.toString())
//                    genre.movies = MovieConverter.dtoListToDtoList(tmp).toMutableSet()
//                }
//                else -> throw UserInputValidationException("Unable to handle field: 'movies'")
//            }
//        }
        genreRepository.save(genre)

        return GenreConverter.entityToDto(genre, true)
    }


    private fun validateId(stringId: String?): Long {
        try {
            return stringId!!.toLong()
        } catch (e: Exception){
            val errorMsg = invalidIdParameter()
            logger.warn(errorMsg)
            throw UserInputValidationException(errorMsg)
        }
    }
}