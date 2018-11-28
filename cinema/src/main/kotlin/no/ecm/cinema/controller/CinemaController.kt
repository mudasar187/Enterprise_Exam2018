package no.ecm.cinema.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import no.ecm.cinema.model.converter.CinemaConverter
import no.ecm.cinema.model.converter.RoomConverter
import no.ecm.cinema.service.CinemaService
import no.ecm.cinema.service.RoomService
import no.ecm.utils.dto.cinema.CinemaDto
import no.ecm.utils.dto.cinema.RoomDto
import no.ecm.utils.hal.HalLinkGenerator
import no.ecm.utils.hal.PageDto
import no.ecm.utils.response.ResponseDto
import no.ecm.utils.response.WrappedResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder

@Api(value = "/cinemas", description = "API for cinema entity")
@RequestMapping(
        path = ["/cinemas"],
        produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
@RestController
class CinemaController(
        private var cinemaService: CinemaService,
        private var roomService: RoomService
) {

    @ApiOperation("Get all cinemas")
    @GetMapping
    fun getCinemas(
            @ApiParam("search for cinema")
            @RequestParam("name", required = false)
            name: String?,

            @ApiParam("search for location")
            @RequestParam("location", required = false)
            location: String?,

            @ApiParam("offset in the list of cinemas")
            @RequestParam("offset", defaultValue = "0")
            offset: Int,

            @ApiParam("limit of cinemas in a single retrived page")
            @RequestParam("limit", defaultValue = "10")
            limit: Int
    ): ResponseEntity<WrappedResponse<CinemaDto>> {

        val cinemasDtos = cinemaService.get(name, location)
        val builder = UriComponentsBuilder.fromPath("/cinemas")
        if(!name.isNullOrEmpty()) {
            builder.queryParam("name", name)
        }

        if(!location.isNullOrEmpty()) {
            builder.queryParam("location", location)
        }

        val pageDto = CinemaConverter.dtoListToPageDto(cinemasDtos, offset, limit)
        return HalLinkGenerator<CinemaDto>().generateHalLinks(cinemasDtos, pageDto, builder, limit, offset)
    }

    @ApiOperation("Get cinema by id")
    @GetMapping(path = ["/{id}"])
    fun findBy(
            @PathVariable("id")
            id: String?
    ): ResponseEntity<WrappedResponse<CinemaDto>> {

        val dto = CinemaConverter.entityToDto(cinemaService.getCinemaById(id), true)
        val etag = dto.hashCode().toString()

        return ResponseEntity.status(HttpStatus.OK.value())
                .eTag(etag)
                .body(
                        ResponseDto(
                                code = HttpStatus.OK.value(),
                                page = PageDto(list = mutableListOf(dto), totalSize = mutableListOf(dto).size)
                        ).validated()
                )
    }

    @ApiOperation("Create a cinema")
    @PostMapping
    fun createCinema(
            @ApiParam("JSON object representing the Cinema")
            @RequestBody cinemaDto: CinemaDto
    ): ResponseEntity<WrappedResponse<CinemaDto>> {
        return ResponseEntity.ok(
                ResponseDto(
                        code = HttpStatus.CREATED.value(),
                        page = PageDto(mutableListOf(cinemaService.createCinema(cinemaDto)))
                ).validated()
        )
    }

    // TODO: fix return no content
    @ApiOperation("Update partial information of a cinema by id ")
    @PatchMapping(path = ["/{id}"], consumes = ["application/merge-patch+json"])
    fun updateGenre(@ApiParam("id of the cinema")
                    @PathVariable("id")
                    id: String?,
                    @ApiParam("The partial patch")
                    @RequestBody
                    jsonPatch: String) : ResponseEntity<WrappedResponse<CinemaDto>> {
        return ResponseEntity.ok(
                ResponseDto(
                        code = HttpStatus.CREATED.value(),
                        page = PageDto(mutableListOf(cinemaService.patchUpdateCinema(id, jsonPatch)))
                ).validated()
        )
    }

    // TODO: fix return no content
    @ApiOperation("Update whole information of a cinema by id")
    @PutMapping(path = ["/{id}"])
    fun updateCinema(
            @ApiParam("id of the cinema")
            @PathVariable("id")
            id: String,

            @ApiParam("Cinema data")
            @RequestBody
            cinemaDto: CinemaDto
    ): ResponseEntity<WrappedResponse<String?>> {
        return ResponseEntity.ok(
                ResponseDto(
                        code = HttpStatus.NO_CONTENT.value(),
                        page = PageDto(mutableListOf(cinemaService.putUpdateCinema(id, cinemaDto)))
                ).validated()
        )
    }

    // TODO: check if all rooms are gone from database when delete a cinema
    @ApiOperation("Delete a cinema by id")
    @DeleteMapping(path = ["/{id}"])
    fun deleteCinema(
            @ApiParam("id of the cinema")
            @PathVariable("id")
            id: String
    ): ResponseEntity<WrappedResponse<String?>> {
        return ResponseEntity.ok(
                ResponseDto(
                        code = HttpStatus.OK.value(),
                        page = PageDto(mutableListOf(cinemaService.deleteCinema(id)))
                ).validated()
        )
    }


    // Rooms

    @ApiOperation("Get all rooms based on cinema id")
    @GetMapping(path = ["/{cinema_id}/rooms"])
    fun getRoomsFromCinema(
            @ApiParam("id of cinema")
            @PathVariable("cinema_id")
            id: String,

            @ApiParam("offset in the list of rooms")
            @RequestParam("offset", defaultValue = "0")
            offset: Int,

            @ApiParam("limit of rooms in a single retrived page")
            @RequestParam("limit", defaultValue = "10")
            limit: Int
    ): ResponseEntity<WrappedResponse<RoomDto>> {

        val roomsDto = roomService.getAllRoomsFromCinema(id)
        val builder = UriComponentsBuilder.fromPath("/cinemas/$id/rooms")

        val pageDto = RoomConverter.dtoListToPageDto(roomsDto, offset, limit)
        return HalLinkGenerator<RoomDto>().generateHalLinks(roomsDto, pageDto, builder, limit, offset)
    }

    @ApiOperation("Create a room to specific cinema")
    @PostMapping(path = ["/{cinema_id}/rooms/"])
    fun createRoomToSpecificCinema(
            @ApiParam("id of cinema")
            @PathVariable("cinema_id")
            cinemaId: String?,

            @ApiParam("JSON object representing the Room")
            @RequestBody roomDto: RoomDto
    ): ResponseEntity<WrappedResponse<RoomDto>> {
        return ResponseEntity.ok(
                ResponseDto(
                        code = HttpStatus.CREATED.value(),
                        page = PageDto(mutableListOf(roomService.createRoomForSpecificCinema(cinemaId,roomDto)))
                ).validated()
        )
    }

    @ApiOperation("Get room by id and cinema id")
    @GetMapping(path = ["/{cinema_id}/rooms/{room_id}"])
    fun findBy(
            @ApiParam("id of cinema")
            @PathVariable("cinema_id")
            cinemaId: String?,

            @ApiParam("id of room")
            @PathVariable("room_id")
            roomId: String?
    ): ResponseEntity<WrappedResponse<RoomDto>> {

        val dto = roomService.getSingleRoomFromCinema(cinemaId, roomId)
        val etag = dto.hashCode().toString()

        return ResponseEntity.status(HttpStatus.OK.value())
                .eTag(etag)
                .body(
                        ResponseDto(
                                code = HttpStatus.OK.value(),
                                page = PageDto(list = mutableListOf(dto), totalSize = mutableListOf(dto).size)
                        ).validated()
                )
    }
}