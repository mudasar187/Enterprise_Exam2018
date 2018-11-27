package no.ecm.movie.service

import no.ecm.movie.model.converter.MovieConverter
import no.ecm.movie.repository.MovieRepository
import no.ecm.utils.dto.movie.MovieDto
import no.ecm.utils.exception.ConflictException
import no.ecm.utils.exception.ExceptionMessages
import no.ecm.utils.exception.NotFoundException
import no.ecm.utils.exception.UserInputValidationException
import no.ecm.utils.hal.HalLink
import no.ecm.utils.logger
import no.ecm.utils.response.ResponseDto
import no.ecm.utils.response.WrappedResponse
import no.ecm.utils.validation.ValidationHandler
import no.ecm.utils.validation.ValidationHandler.Companion.validateId
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder

@Service
class MovieService (
        private var movieRepository: MovieRepository){

    val logger = logger<MovieService>()

    fun getMovies(title: String?): MutableList<MovieDto> {

        val movies = if (!title.isNullOrEmpty()){
            try {
                movieRepository.findByTitleContainsIgnoreCase(title!!).toMutableList()
            } catch (e: Exception){
                val errorMsg = ExceptionMessages.notFoundMessage("Movie", "title", title!!)
                logger.warn(errorMsg)
                throw NotFoundException(errorMsg)
            }
        } else {
            movieRepository.findAll().toMutableList()
        }

        return MovieConverter.entityListToDtoList(movies)
    }

    fun getMovies(title: String?, offset: Int, limit: Int): ResponseEntity<WrappedResponse<MovieDto>> {

        ValidationHandler.validateLimitAndOffset(offset, limit)

        val builder = UriComponentsBuilder.fromPath("/movies")

        if (!title.isNullOrEmpty()) {
            builder.queryParam("title", title)
        }

        val genres = getMovies(title)

        if (offset != 0 && offset >= genres.size) {
            throw UserInputValidationException(ExceptionMessages.toLargeOffset(offset))
        }

        builder.queryParam("limit", limit)

        val dto = MovieConverter.dtoListToPageDto(genres, offset, limit)

        // Build HalLinks
        dto._self = HalLink(builder.cloneBuilder()
                .queryParam("offset", offset)
                .build().toString()
        )

        if (!genres.isEmpty() && offset > 0) {
            dto.previous = HalLink(builder.cloneBuilder()
                    .queryParam("offset", Math.max(offset - limit, 0))
                    .build().toString()
            )
        }

        if (offset + limit < genres.size) {
            dto.next = HalLink(builder.cloneBuilder()
                    .queryParam("offset", (offset + limit))
                    .build().toString()
            )
        }
        val etag = genres.hashCode().toString()

        return ResponseEntity.status(HttpStatus.OK)
                .eTag(etag)
                .body(ResponseDto(
                        code = HttpStatus.OK.value(),
                        page = dto
                ).validated()
                )
    }

    fun getMovie(stringId: String?): MovieDto {

        val id = validateId(stringId)

        if (!movieRepository.existsById(id)){
            val errorMsg = ExceptionMessages.notFoundMessage("Movie", "id", stringId!!)
            logger.warn(errorMsg)
            throw NotFoundException(errorMsg)
        }

        val movie = movieRepository.findById(id).get()

        return MovieConverter.entityToDto(movie)
    }

    private fun handleMissingField(fieldName: String){
        val errorMsg = ExceptionMessages.missingRequiredField(fieldName)
        logger.warn(errorMsg)
        throw UserInputValidationException(errorMsg)
    }

    fun createMovie(movieDto: MovieDto): String {

        if (movieDto.title.isNullOrEmpty()) {
            handleMissingField("title")
        }  else if (movieDto.movieDuration == null){
            handleMissingField("movieDuration")
        } else if (movieDto.posterUrl.isNullOrEmpty()){
            handleMissingField("posterUrl")
        } else if (!movieDto.id.isNullOrEmpty()){
            val errorMsg = ExceptionMessages.illegalParameter("id")
            logger.warn(errorMsg)
            throw UserInputValidationException(errorMsg)
        }

        if (movieRepository.existsByTitleAndPosterUrlIgnoreCase(movieDto.title!!, movieDto.posterUrl!!)){
            val errorMsg = (ExceptionMessages.resourceAlreadyExists("Movie", "title and posterUrl", "${movieDto.title} and ${movieDto.posterUrl}"))
            logger.error(errorMsg)
            throw ConflictException(errorMsg)
        }

        movieDto.title = movieDto.title!!.capitalize()

        val movie = MovieConverter.dtoToEntity(movieDto)

        return movieRepository.save(movie).id.toString()
    }

    //TODO update method here

    fun deleteMovie(stringId: String?): String? {

        //TODO sjekke movies med denne genre
        val id = validateId(stringId)

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
}