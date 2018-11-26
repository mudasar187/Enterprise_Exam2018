package no.ecm.cinema.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import no.ecm.cinema.service.CinemaService
import no.ecm.utils.dto.cinema.CinemaDto
import no.ecm.utils.hal.PageDto
import no.ecm.utils.response.ResponseDto
import no.ecm.utils.response.WrappedResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Api(value = "/cinemas", description = "API for cinema entity")
@RequestMapping(
        path = ["/cinemas"],
        produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
@RestController
class CinemaController {

    @Autowired
    private lateinit var cinemaService: CinemaService

    @ApiOperation("Get all cinemas")
    @GetMapping
    fun get(
            @ApiParam("search for cinema")
            @RequestParam("name", required = false)
            name: String?,

            @ApiParam("offset in the list of cinemas")
            @RequestParam("offset", defaultValue = "0")
            offset: Int,

            @ApiParam("limit of cinemas in a single retrived page")
            @RequestParam("limit", defaultValue = "10")
            limit: Int
    ): ResponseEntity<WrappedResponse<CinemaDto>> {

        val dtos = cinemaService.get(name, null, offset, limit)
        val etag = dtos.hashCode().toString()

        return ResponseEntity.status(HttpStatus.OK.value())
                .eTag(etag)
                .body(
                        ResponseDto(
                                code = HttpStatus.OK.value(),
                                page = PageDto(list = dtos, totalSize = dtos.size)
                        ).validated()
                )
    }

    @ApiOperation("Get cinema by id")
    @GetMapping(path = ["/{id}"])
    fun get(
            @PathVariable("id")
            id: String?
    ): ResponseEntity<WrappedResponse<CinemaDto>> {

        val dto = cinemaService.get(null, id, 0, 1)
        val etag = dto.hashCode().toString()

        return ResponseEntity.status(HttpStatus.OK.value())
                .eTag(etag)
                .body(
                        ResponseDto(
                                code = HttpStatus.OK.value(),
                                page = PageDto(list = dto, totalSize = dto.size)
                        ).validated()
                )
    }

    @ApiOperation("Create a cinema")
    @PostMapping
    fun createCinema(
            @ApiParam("JSON object representing the Cinema")
            @RequestBody cinemaDto: CinemaDto
    ): ResponseEntity<WrappedResponse<String>> {
        return ResponseEntity.ok(
                ResponseDto(
                        code = HttpStatus.CREATED.value(),
                        page = PageDto(mutableListOf(cinemaService.createCinema(cinemaDto)))
                ).validated()
        )
    }

    @ApiOperation("Delete a cinema by id")
    @DeleteMapping(path = ["/{id}"])
    fun deleteGenre(
            @ApiParam("id of the cinema")
            @PathVariable("id") id: String): ResponseEntity<WrappedResponse<String?>> {
        return ResponseEntity.ok(
                ResponseDto(
                        code = HttpStatus.OK.value(),
                        page = PageDto(mutableListOf(cinemaService.deleteCinema(id)))
                ).validated()
        )
    }

}