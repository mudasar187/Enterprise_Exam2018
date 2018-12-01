package no.ecm.movie.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.ecm.movie.model.converter.MovieConverter
import no.ecm.movie.model.entity.Movie
import no.ecm.movie.repository.MovieRepository
import no.ecm.utils.dto.movie.GenreDto
import no.ecm.utils.dto.movie.MovieDto
import no.ecm.utils.exception.ConflictException
import no.ecm.utils.messages.ExceptionMessages
import no.ecm.utils.messages.ExceptionMessages.Companion.illegalParameter
import no.ecm.utils.exception.NotFoundException
import no.ecm.utils.exception.UserInputValidationException
import no.ecm.utils.logger
import no.ecm.utils.messages.ExceptionMessages.Companion.inputFilterInvalid
import no.ecm.utils.messages.ExceptionMessages.Companion.invalidParameter
import no.ecm.utils.validation.ValidationHandler.Companion.validateId
import org.springframework.stereotype.Service

@Service
class MovieService (
        private var movieRepository: MovieRepository,
        private var genreService: GenreService){

    val logger = logger<MovieService>()

    fun getMovies(title: String?, ageLimit: Int?): MutableList<MovieDto> {

        val movies = if (!title.isNullOrBlank() && ageLimit != null){
            logger.warn(inputFilterInvalid())
            throw UserInputValidationException(inputFilterInvalid())
        }else if (!title.isNullOrBlank()){
            movieRepository.findAllByTitleContainsIgnoreCase(title!!).toMutableList()
        }else if (ageLimit != null){
            movieRepository.findAllByAgeLimitGreaterThanEqual(ageLimit).toMutableList()
        } else {
            movieRepository.findAll().toMutableList()
        }

        return MovieConverter.entityListToDtoList(movies)
    }

    fun getMovie(stringId: String?): Movie {

        val id = validateId(stringId, "id")

        if (!movieRepository.existsById(id)){
            val errorMsg = ExceptionMessages.notFoundMessage("Movie", "id", stringId!!)
            logger.warn(errorMsg)
            throw NotFoundException(errorMsg)
        }

        return movieRepository.findById(id).get()
    }

    private fun handleMissingField(fieldName: String){
        val errorMsg = ExceptionMessages.missingRequiredField(fieldName)
        logger.warn(errorMsg)
        throw UserInputValidationException(errorMsg)
    }

    fun createMovie(movieDto: MovieDto): MovieDto {

        validateMovieDto(movieDto)

        if (!movieDto.id.isNullOrEmpty()){
            val errorMsg = ExceptionMessages.illegalParameter("id")
            logger.warn(errorMsg)
            throw UserInputValidationException(errorMsg)
        }

        if (movieRepository.existsByTitleAndPosterUrlIgnoreCase(movieDto.title!!, movieDto.posterUrl!!)){
            val errorMsg = (ExceptionMessages
                    .resourceAlreadyExists("Movie", "title and posterUrl", "${movieDto.title} and ${movieDto.posterUrl}"))
            logger.error(errorMsg)
            throw ConflictException(errorMsg)
        }

        movieDto.title = movieDto.title!!.capitalize()

        val movie = MovieConverter.dtoToEntity(movieDto)

        if (movieDto.genre != null){
            movieDto.genre!!.forEach { genreService.getGenre(it.id).movies.add(movie) }
        }

        return MovieDto(id = movieRepository.save(movie).id.toString())
    }

    fun patchMovie(stringId: String?, body: String?) {

        val id = validateId(stringId, "id")

        if (!movieRepository.existsById(id)){
            val errorMsg = ExceptionMessages.notFoundMessage("Movie", "id", stringId!!)
            logger.warn(errorMsg)
            throw NotFoundException(errorMsg)
        }

        val jackson = ObjectMapper()

        val jsonNode: JsonNode

        try {
            jsonNode = jackson.readValue(body, JsonNode::class.java)
        } catch (e: Exception) {
            val errorMsg = ExceptionMessages.invalidParameter("JSON", "invalid JSON object")
            logger.error(errorMsg)
            throw UserInputValidationException(errorMsg)
        }

        val movie = movieRepository.findById(id).get()

        if (jsonNode.has("id")){
            throw UserInputValidationException(illegalParameter("id"))
        }

        if (jsonNode.has("title")) {
            val title = jsonNode.get("title")
            if (title.isTextual){
                movie.title = title.asText()
            } else {
                val errorMsg = ExceptionMessages.unableToParse("title")
                logger.warn(errorMsg)
                throw UserInputValidationException(errorMsg)
            }
        }

        if (jsonNode.has("posterUrl")) {
            val posterUrl = jsonNode.get("posterUrl")
            if (posterUrl.isTextual){
                movie.posterUrl = posterUrl.asText()
            } else {
                val errorMsg = ExceptionMessages.unableToParse("posterUrl")
                logger.warn(errorMsg)
                throw UserInputValidationException(errorMsg)
            }
        }

        if (jsonNode.has("ageLimit")) {
            val ageLimit = jsonNode.get("ageLimit")
            if (ageLimit.isInt){
                movie.ageLimit = ageLimit.asInt()
            } else {
                val errorMsg = ExceptionMessages.unableToParse("ageLimit")
                logger.warn(errorMsg)
                throw UserInputValidationException(errorMsg)
            }
        }

        if (jsonNode.has("movieDuration")) {
            val movieDuration = jsonNode.get("movieDuration")
            if (movieDuration.isInt){
                movie.movieDuration = movieDuration.asInt()
            } else {
                val errorMsg = ExceptionMessages.unableToParse("movieDuration")
                logger.warn(errorMsg)
                throw UserInputValidationException(errorMsg)
            }
        }

        if (jsonNode.has("genre")) {
            val genre = jsonNode.get("genre")
            when {
                genre.isNull -> movie.genre = mutableSetOf()
                genre.isArray -> {
                    val mapper = jacksonObjectMapper()
                    val genreDtos: Set<GenreDto> = mapper.readValue(genre.toString())
                    movie.genre.forEach { it.movies.remove(movie) }
                    genreDtos.forEach { genreService.getGenre(it.id).movies.add(movie) }
                    movie.genre = genreDtos.asSequence().map { genreService.getGenre(it.id) }.toMutableSet()

                }
                else -> throw UserInputValidationException("Unable to handle field: 'movies'")
            }
        }
        movieRepository.save(movie)
    }

    fun putMovie(stringId: String?, movieDto: MovieDto) {

        validateId(stringId, "id")
        validateMovieDto(movieDto)
        val movie = getMovie(stringId)

        if (!stringId.equals(movieDto.id)){
            throw UserInputValidationException(invalidParameter(stringId!!, movieDto.id!!))
        }

        movie.title = movieDto.title
        movie.posterUrl = movieDto.posterUrl
        movie.ageLimit = movieDto.ageLimit
        movie.ageLimit = movieDto.ageLimit
        movie.genre.forEach { it.movies.remove(movie) }
        if (movieDto.genre != null){
            movieDto.genre!!.forEach { genreService.getGenre(it.id).movies.add(movie) }
        }
        movieRepository.save(movie)
    }

    fun deleteMovie(stringId: String?): String? {

        val id = validateId(stringId, "id")

        if (!movieRepository.existsById(id)){
            val errorMsg = ExceptionMessages.notFoundMessage("Movie", "id", stringId!!)
            logger.warn(errorMsg)
            throw NotFoundException(errorMsg)
        }

        val movie = movieRepository.findById(id).get()
        movie.genre.forEach { it.movies.remove(movie) }

        movieRepository.deleteById(id)

        return id.toString()
    }

    fun validateMovieDto(movieDto: MovieDto) {
        when {
            movieDto.title.isNullOrEmpty() -> handleMissingField("title")
            movieDto.movieDuration == null -> handleMissingField("movieDuration")
            movieDto.posterUrl.isNullOrEmpty() -> handleMissingField("posterUrl")
            movieDto.ageLimit == null -> handleMissingField("ageLimit")
            movieDto.genre == null -> handleMissingField("genre")
            movieDto.genre!!.isEmpty() -> handleMissingField("genre")
        }
    }
}