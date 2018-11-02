package no.ecm.movie.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiParam
import no.ecm.movie.service.GenreService
import no.ecm.utils.dto.movie.GenreDto
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@Api(value = "/genre", description = "API for genre entity")
@RequestMapping(
        path = ["/genre"],
        produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
@RestController
class GenreController(
        private var genreService: GenreService
) {

    @GetMapping(produces = ["application/json"])
    fun getGenres(@ApiParam("Name of the Pokemon")
                  @RequestParam("name", required = false)
                  name : String?): List<GenreDto> {
        return genreService.getGenres(name)
    }
}