package no.ecm.movie.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import no.ecm.movie.service.MovieService
import no.ecm.utils.dto.movie.MovieDto
import no.ecm.utils.hal.PageDto
import no.ecm.utils.response.ResponseDto
import no.ecm.utils.response.WrappedResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Api(value = "/movies", description = "API for movie entity")
@RequestMapping(
        path = ["/movies"],
        produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
@RestController
class MovieController (
        private val movieService: MovieService
){

    //TODO filter by age limit
    @ApiOperation("Get movies, possible filter by title")
    @GetMapping
    fun getMovies(@ApiParam("Title of the Movie")
                  @RequestParam("title", required = false)
                  title : String?,

                  @ApiParam("Offset in the list of movies")
                  @RequestParam("offset", defaultValue = "0")
                  offset: Int,

                  @ApiParam("Limit of movies in a single retrieved page")
                  @RequestParam("limit", defaultValue = "10")
                  limit: Int): ResponseEntity<WrappedResponse<MovieDto>> {
        return movieService.getMovies(title, offset, limit)
    }

    @ApiOperation("Get a Movie by the id")
    @GetMapping(path = ["/{id}"])
    fun getMovie(
            @ApiParam("id of the Movie")
            @PathVariable("id") id: String): ResponseEntity<WrappedResponse<MovieDto>> {

        val dto = movieService.getMovie(id)
        val etag = dto.hashCode().toString()

        return ResponseEntity
                .status(HttpStatus.OK.value())
                .eTag(etag)
                .body(
                        ResponseDto(
                                code = HttpStatus.OK.value(),
                                page = PageDto(mutableListOf(dto))
                        ).validated()
                )
    }

    @ApiOperation("Create a Movie")
    @PostMapping(consumes = ["application/json"])
    fun createMovie(
            @ApiParam("JSON object representing the Movie")
            @RequestBody movieDto: MovieDto): ResponseEntity<WrappedResponse<String>> {
        return ResponseEntity.ok(
                ResponseDto(
                        code = HttpStatus.CREATED.value(),
                        page = PageDto(mutableListOf(movieService.createMovie(movieDto)))
                ).validated()
        )
    }


    //TODO Update method here



    @ApiOperation("Delete a Movie by the id")
    @DeleteMapping(path = ["/{id}"])
    fun deleteMovie(
            @ApiParam("id of the Movie")
            @PathVariable("id") id: String): ResponseEntity<WrappedResponse<String?>> {
        return ResponseEntity.ok(
                ResponseDto(
                        code = HttpStatus.OK.value(),
                        page = PageDto(mutableListOf(movieService.deleteMovie(id)))
                ).validated()
        )
    }
}