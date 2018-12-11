package no.ecm.movie.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import no.ecm.movie.model.converter.MovieConverter
import no.ecm.movie.service.MovieService
import no.ecm.utils.cache.EtagHandler
import no.ecm.utils.dto.movie.MovieDto
import no.ecm.utils.hal.HalLinkGenerator
import no.ecm.utils.hal.PageDto
import no.ecm.utils.hal.PageDtoGenerator
import no.ecm.utils.response.ResponseDto
import no.ecm.utils.response.WrappedResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

@Api(value = "/movies", description = "API for movie entity")
@RequestMapping(
        path = ["/movies"],
        produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
@RestController
@CrossOrigin(origins = ["http://localhost:8080"])
class MovieController (
        private val movieService: MovieService
){

    @ApiOperation("Get movies, possible filter by title")
    @GetMapping
    fun getMovies(@ApiParam("Title of the Movie")
                  @RequestParam("title", required = false)
                  title : String?,

                  @ApiParam("Age limit of the Movie")
                  @RequestParam("ageLimit", required = false)
                  ageLimit : Int?,

                  @ApiParam("Offset in the list of movies")
                  @RequestParam("offset", defaultValue = "0")
                  offset: Int,

                  @ApiParam("Limit of movies in a single retrieved page")
                  @RequestParam("limit", defaultValue = "10")
                  limit: Int): ResponseEntity<WrappedResponse<MovieDto>> {
        val movieDtos = movieService.getMovies(title, ageLimit)

        val builder = UriComponentsBuilder.fromPath("/movies")
        if (!title.isNullOrEmpty()) {
            builder.queryParam("title", title)
        }
        val pageDto = PageDtoGenerator<MovieDto>().generatePageDto(movieDtos, offset, limit)
        return HalLinkGenerator<MovieDto>().generateHalLinks(movieDtos, pageDto, builder, limit, offset)
    }

    @ApiOperation("Get a Movie by the id")
    @GetMapping(path = ["/{id}"])
    fun getMovie(
            @ApiParam("id of the Movie")
            @PathVariable("id") id: String): ResponseEntity<WrappedResponse<MovieDto>> {

        val dto = MovieConverter.entityToDto(movieService.getMovie(id))
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
            @RequestBody movieDto: MovieDto): ResponseEntity<WrappedResponse<MovieDto>> {
        val dto = movieService.createMovie(movieDto)
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(URI.create("/movies/${dto.id}"))
                .body(
                ResponseDto(
                        code = HttpStatus.CREATED.value(),
                        page = PageDto(mutableListOf(dto))
                ).validated()
                )
    }


    @ApiOperation("Update a Movie using merge patch")
    @PatchMapping(path = ["/{id}"], consumes = ["application/merge-patch+json"])
    fun patchMovie(@ApiParam("The id of the Movie")
                    @PathVariable("id")
                    id: String?,
                    @ApiParam("Content of ETag")
                    @RequestHeader("If-Match")
                    ifMatch: String?,
                    @ApiParam("The partial patch")
                    @RequestBody
                    jsonPatch: String) : ResponseEntity<Void> {

        val currentDto = MovieConverter.entityToDto(movieService.getMovie(id))
        EtagHandler<MovieDto>().validateEtags(currentDto, ifMatch)

        movieService.patchMovie(id, jsonPatch)
        return ResponseEntity.noContent().build()
    }


    @ApiOperation("Update a Movie")
    @PutMapping(path = ["/{id}"], consumes = ["application/json"])
    fun putMovie(@ApiParam("The id of the Movie")
                 @PathVariable("id")
                 id: String?,
                 @ApiParam("Content of ETag")
                 @RequestHeader("If-Match")
                 ifMatch: String?,
                 @ApiParam("JSON object representing the Movie")
                 @RequestBody
                 movieDto: MovieDto): ResponseEntity<Void> {

        val currentDto = MovieConverter.entityToDto(movieService.getMovie(id))
        EtagHandler<MovieDto>().validateEtags(currentDto, ifMatch)

        movieService.putMovie(id, movieDto)
        return ResponseEntity.noContent().build()
    }

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