package no.ecm.movie.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import no.ecm.movie.model.converter.GenreConverter
import no.ecm.movie.repository.GenreRepository
import no.ecm.utils.dto.movie.GenreDto
import no.ecm.utils.exception.ExceptionMessages
import no.ecm.utils.exception.NotFoundException
import no.ecm.utils.exception.UserInputValidationException
import org.springframework.stereotype.Service



@Service
class GenreService (
        private var genreRepository: GenreRepository){

    fun getGenres(name: String?): MutableList<GenreDto> {

        val genres = if (!name.isNullOrEmpty()){
            try {
                mutableListOf(genreRepository.findByName(name!!))
            } catch (e: Exception){
                throw NotFoundException("Genre with name: $name not found")
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
            throw NotFoundException("Genre not found")
        }
        
        val genre = genreRepository.findById(id).get()
        
        return GenreConverter.entityToDto(genre, true)
    }


    fun createGenre(genreDto: GenreDto): String {

        if (genreDto.name.isNullOrEmpty()) {
            throw UserInputValidationException("Empty field: 'name'")
        } else if (!genreDto.id.isNullOrEmpty()){
            throw UserInputValidationException("")
        }

        val genre = GenreConverter.dtoToEntity(genreDto)

        return genreRepository.save(genre).id.toString()
    }


    fun deleteGenre(stringId: String?): String? {

        val id = validateId(stringId)

        if (!genreRepository.existsById(id)){
            throw NotFoundException("Genre not found")
        }
        
        genreRepository.deleteById(id)
        
        return id.toString()
    }

    fun updateGenre(stringId: String?, body: String?): GenreDto {

        val id = validateId(stringId)

        if (!genreRepository.existsById(id)){
            throw NotFoundException("Genre not found")
        }

        val jackson = ObjectMapper()

        val jsonNode: JsonNode

        try {
            jsonNode = jackson.readValue(body, JsonNode::class.java)
        } catch (e: Exception) {
            throw UserInputValidationException(
                    ExceptionMessages.invalidParameter("JSON", "invalid JSON object")
            )
        }

        val genre = genreRepository.findById(id).get()

        if (jsonNode.has("name")) {
            val name = jsonNode.get("name")
            if (name.isTextual){
                genre.name = name.asText()
            } else {
                throw UserInputValidationException(ExceptionMessages.unableToParse("name"))
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
            throw UserInputValidationException(ExceptionMessages.invalidIdParameter())
        }
    }
}