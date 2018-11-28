package no.ecm.movie.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import no.ecm.movie.model.converter.GenreConverter
import no.ecm.movie.service.GenreService
import no.ecm.utils.dto.movie.GenreDto
import no.ecm.utils.hal.HalLinkGenerator
import no.ecm.utils.hal.PageDto
import no.ecm.utils.response.ResponseDto
import no.ecm.utils.response.WrappedResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder

@Api(value = "/genres", description = "API for genre entity")
@RequestMapping(
        path = ["/genres"],
        produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
@RestController
class GenreController(
        private var genreService: GenreService
) {

    @ApiOperation("Get genres, possible filter by name")
    @GetMapping
    fun getGenres(@ApiParam("Name of the Genre")
                  @RequestParam("name", required = false)
                  name : String?,

                  @ApiParam("Offset in the list of genres")
                  @RequestParam("offset", defaultValue = "0")
                  offset: Int,

                  @ApiParam("Limit of genres in a single retrieved page")
                  @RequestParam("limit", defaultValue = "10")
                  limit: Int): ResponseEntity<WrappedResponse<GenreDto>> {
        val genreDtos = genreService.getGenres(name)

        val builder = UriComponentsBuilder.fromPath("/genres")

        if (!name.isNullOrEmpty()) {
            builder.queryParam("name", name)
        }

        val pageDto = GenreConverter.dtoListToPageDto(genreDtos, offset, limit)
        return HalLinkGenerator<GenreDto>().generateHalLinks(genreDtos, pageDto, builder, limit, offset)
    }

    @ApiOperation("Get a Genre by the id")
    @GetMapping(path = ["/{id}"])
    fun getGenre(
            @ApiParam("id of the Genre")
            @PathVariable("id") id: String): ResponseEntity<WrappedResponse<GenreDto>> {

        val dto = GenreConverter.entityToDto(genreService.getGenre(id), true)
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

    @ApiOperation("Create a Genre")
    @PostMapping(consumes = ["application/json"])
    fun createGenre(
            @ApiParam("JSON object representing the Genre")
            @RequestBody genreDto: GenreDto): ResponseEntity<WrappedResponse<GenreDto>> {
        return ResponseEntity.ok(
                ResponseDto(
                        code = HttpStatus.CREATED.value(),
                        page = PageDto(mutableListOf(genreService.createGenre(genreDto)))
                ).validated()
        )
    }


    @ApiOperation("Create a Genre")
    @PatchMapping(path = ["/{id}"], consumes = ["application/merge-patch+json"])
    fun updateGenre(@ApiParam("The id of the Genre")
              @PathVariable("id")
              id: String?,
              @ApiParam("The partial patch")
              @RequestBody
              jsonPatch: String) : ResponseEntity<WrappedResponse<GenreDto>> {
        return ResponseEntity.ok(
                ResponseDto(
                        code = HttpStatus.CREATED.value(),
                        page = PageDto(mutableListOf(genreService.patchGenre(id, jsonPatch)))
                ).validated()
        )
    }


    @ApiOperation("Delete a Genre by the id")
    @DeleteMapping(path = ["/{id}"])
    fun deleteGenre(
            @ApiParam("id of the Genre")
            @PathVariable("id") id: String): ResponseEntity<WrappedResponse<String?>> {
        return ResponseEntity.ok(
                ResponseDto(
                        code = HttpStatus.OK.value(),
                        page = PageDto(mutableListOf(genreService.deleteGenre(id)))
                ).validated()
        )
    }


}