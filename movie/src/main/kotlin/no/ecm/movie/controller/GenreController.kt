package no.ecm.movie.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import no.ecm.movie.model.response.GenreResponse
import no.ecm.movie.service.GenreService
import no.ecm.utils.dto.movie.GenreDto
import no.ecm.utils.response.WrappedResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Api(value = "/genre", description = "API for genre entity")
@RequestMapping(
        path = ["/genre"],
        produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
@RestController
class GenreController(
        private var genreService: GenreService
) {

    @ApiOperation("Get genres, possible filter by name")
    @GetMapping
    fun getGenres(@ApiParam("Name of the Genre")
                  @RequestParam("name", required = false)
                  name : String?): ResponseEntity<WrappedResponse<List<GenreDto>>>? {
        return ResponseEntity.ok(
                WrappedResponse(
                        code = HttpStatus.OK.value(),
                        data = genreService.getGenres(name)
                ).validated()
        )
    }

    @ApiOperation("Get a Genre by the id")
    @GetMapping(path = ["/{id}"])
    fun getGenre(
            @ApiParam("id of the Genre")
            @PathVariable("id") id: String): ResponseEntity<WrappedResponse<GenreDto>> {
        return ResponseEntity.ok(
                WrappedResponse(
                        code = HttpStatus.OK.value(),
                        data = genreService.getGenre(id)
                ).validated()
        )
    }

    @ApiOperation("Create a Genre")
    @PostMapping
    fun createGenre(
            @ApiParam("JSON object representing the Genre")
            @RequestBody genreDto: GenreDto): ResponseEntity<WrappedResponse<String>> {
        return ResponseEntity.ok(
                WrappedResponse(
                        code = HttpStatus.CREATED.value(),
                        data = genreService.createGenre(genreDto)
                ).validated()
        )
    }


    @ApiOperation("Create a Genre")
    @PatchMapping(path = ["/{id}"])
    fun updateGenre(@ApiParam("The id of the Genre")
              @PathVariable("id")
              id: String?,
              @ApiParam("The partial patch")
              @RequestBody
              jsonPatch: String) : ResponseEntity<WrappedResponse<GenreDto>> {
        return ResponseEntity.ok(
                WrappedResponse(
                        code = HttpStatus.CREATED.value(),
                        data = genreService.updateGenre(id, jsonPatch)
                ).validated()
        )
    }


    @ApiOperation("Delete a Genre by the id")
    @DeleteMapping(path = ["/{id}"])
    fun deleteGenre(
            @ApiParam("id of the Genre")
            @PathVariable("id") id: String): ResponseEntity<WrappedResponse<String>> {
        return ResponseEntity.ok(
                WrappedResponse(
                        code = HttpStatus.OK.value(),
                        data = genreService.deleteGenre(id)
                ).validated()
        )
    }


}