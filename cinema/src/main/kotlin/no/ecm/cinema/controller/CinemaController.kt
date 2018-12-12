package no.ecm.cinema.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import no.ecm.cinema.model.converter.CinemaConverter
import no.ecm.cinema.service.CinemaService
import no.ecm.cinema.service.RoomService
import no.ecm.utils.cache.EtagHandler
import no.ecm.utils.dto.cinema.CinemaDto
import no.ecm.utils.dto.cinema.RoomDto
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

@Api(value = "/cinemas", description = "API for cinema and room entity")
@RequestMapping(
        path = ["/cinemas"],
        produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
@RestController
class CinemaController(
        private var cinemaService: CinemaService,
        private var roomService: RoomService
) {

    /**
     * Cinema entity
     */

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
        if (!name.isNullOrEmpty()) {
            builder.queryParam("name", name)
        }

        if (!location.isNullOrEmpty()) {
            builder.queryParam("location", location)
        }

        val pageDto = PageDtoGenerator<CinemaDto>().generatePageDto(cinemasDtos, offset, limit)
        return HalLinkGenerator<CinemaDto>().generateHalLinks(cinemasDtos, pageDto, builder, limit, offset)
    }

    @ApiOperation("Get cinema by id")
    @GetMapping(path = ["/{id}"])
    fun getCinemaById(
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
    @PostMapping(consumes = ["application/json"])
    fun createCinema(
            @ApiParam("JSON object representing the Cinema")
            @RequestBody cinemaDto: CinemaDto
    ): ResponseEntity<WrappedResponse<CinemaDto>> {
        val dto = cinemaService.createCinema(cinemaDto)
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(URI.create("/cinemas/${dto.id}"))
                .body(
                ResponseDto(
                        code = HttpStatus.CREATED.value(),
                        page = PageDto(mutableListOf(dto))
                ).validated()
        )
    }

    @ApiOperation("Update partial information of a cinema by id ")
    @PatchMapping(path = ["/{id}"], consumes = ["application/merge-patch+json"])
    fun patchUpdateCinema(@ApiParam("id of the cinema")
                          @PathVariable("id")
                          id: String?,
                          @ApiParam("Content of ETag")
                          @RequestHeader("If-Match")
                          ifMatch: String?,
                          @ApiParam("The partial patch")
                          @RequestBody
                          jsonPatch: String
    ): ResponseEntity<Void> {
        val currentDto = CinemaConverter.entityToDto(cinemaService.getCinemaById(id), true)
        EtagHandler<CinemaDto>().validateEtags(currentDto, ifMatch)

        cinemaService.patchUpdateCinema(id, jsonPatch)
        return ResponseEntity.noContent().build()
    }

    @ApiOperation("Update whole information of a cinema by id")
    @PutMapping(path = ["/{id}"], consumes = ["application/json"])
    fun putUpdateCinema(
            @ApiParam("id of the cinema")
            @PathVariable("id")
            id: String,
            @ApiParam("Content of ETag")
            @RequestHeader("If-Match")
            ifMatch: String?,
            @ApiParam("Cinema data")
            @RequestBody
            cinemaDto: CinemaDto
    ): ResponseEntity<Void> {
        val currentDto = CinemaConverter.entityToDto(cinemaService.getCinemaById(id), true)
        EtagHandler<CinemaDto>().validateEtags(currentDto, ifMatch)

        cinemaService.putUpdateCinema(id, cinemaDto)
        return ResponseEntity.noContent().build()
    }

    @ApiOperation("Delete a cinema by id")
    @DeleteMapping(path = ["/{id}"])
    fun deleteCinemaById(
            @ApiParam("id of the cinema")
            @PathVariable("id")
            id: String
    ): ResponseEntity<WrappedResponse<String?>> {
        return ResponseEntity.ok(
                ResponseDto(
                        code = HttpStatus.OK.value(),
                        page = PageDto(mutableListOf(cinemaService.deleteCinemaById(id)))
                ).validated()
        )
    }


    /**
     * Rooms entity
     */

    @ApiOperation("Get all rooms based on cinema id")
    @GetMapping(path = ["/{cinema_id}/rooms"])
    fun getAllRoomsFromCinemaById(
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

        val roomsDtos = roomService.getAllRoomsFromCinemaByCinemaId(id)
        val builder = UriComponentsBuilder.fromPath("/cinemas/$id/rooms")

        val pageDto = PageDtoGenerator<RoomDto>().generatePageDto(roomsDtos, offset, limit)
        return HalLinkGenerator<RoomDto>().generateHalLinks(roomsDtos, pageDto, builder, limit, offset)
    }

    @ApiOperation("Get single room by id and cinema id")
    @GetMapping(path = ["/{cinema_id}/rooms/{room_id}"])
    fun getRoomByIdAndCinemaId(
            @ApiParam("id of cinema")
            @PathVariable("cinema_id")
            cinemaId: String?,

            @ApiParam("id of room")
            @PathVariable("room_id")
            roomId: String?
    ): ResponseEntity<WrappedResponse<RoomDto>> {

        val dto = roomService.getRoomByIdAndCinemaId(cinemaId, roomId)
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

    @ApiOperation("Create a room to specific cinema")
    @PostMapping(path = ["/{cinema_id}/rooms"], consumes = ["application/json"])
    fun createRoomToSpecificCinemaByCinemaId(
            @ApiParam("id of cinema")
            @PathVariable("cinema_id")
            cinemaId: String?,

            @ApiParam("JSON object representing the Room")
            @RequestBody roomDto: RoomDto
    ): ResponseEntity<WrappedResponse<RoomDto>> {
        val dto = roomService.createRoomForSpecificCinemaByCinemaId(cinemaId, roomDto)
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(URI.create("/cinemas/$cinemaId/rooms/${roomDto.id}"))
                .body(
                ResponseDto(
                        code = HttpStatus.CREATED.value(),
                        page = PageDto(mutableListOf(dto))
                ).validated()
        )
    }

    @ApiOperation("Update partial information of a room by id ")
    @PatchMapping(path = ["/{cinema_id}/rooms/{room_id}"], consumes = ["application/merge-patch+json"])
    fun patchUpdateRoomByIdAndCinemaId(
            @ApiParam("id of the cinema")
            @PathVariable("cinema_id")
            cinemaId: String?,

            @ApiParam("id of the room")
            @PathVariable("room_id")
            roomId: String?,

            @ApiParam("Content of ETag")
            @RequestHeader("If-Match")
            ifMatch: String?,

            @ApiParam("The partial patch")
            @RequestBody
            jsonPatch: String
    ): ResponseEntity<Void> {
        val currentDto = roomService.getRoomByIdAndCinemaId(cinemaId, roomId)
        EtagHandler<RoomDto>().validateEtags(currentDto, ifMatch)

        roomService.patchUpdateRoomByIdAndCinemaId(cinemaId, roomId, jsonPatch)
        return ResponseEntity.noContent().build()
    }

    @ApiOperation("Update whole information of a room by id")
    @PutMapping(path = ["/{cinema_id}/rooms/{room_id}"], consumes = ["application/json"])
    fun putUpdateRoomIdByCinemaId(
            @ApiParam("id of the cinema")
            @PathVariable("cinema_id")
            cinemaId: String?,

            @ApiParam("id of the room")
            @PathVariable("room_id")
            roomId: String?,

            @ApiParam("Content of ETag")
            @RequestHeader("If-Match")
            ifMatch: String?,

            @ApiParam("Room data")
            @RequestBody
            roomDto: RoomDto
    ): ResponseEntity<Void> {
        val currentDto = roomService.getRoomByIdAndCinemaId(cinemaId, roomId)
        EtagHandler<RoomDto>().validateEtags(currentDto, ifMatch)

        roomService.putUpdateRoomIdByCinemaId(cinemaId, roomId, roomDto)
        return ResponseEntity.noContent().build()
    }

    @ApiOperation("Delete a room for specific cinema")
    @DeleteMapping(path = ["/{cinema_id}/rooms/{room_id}"])
    fun deleteRoomByIdAndCinemaId(
            @ApiParam("id of the cinema")
            @PathVariable("cinema_id")
            cinemaId: String?,

            @ApiParam("id of the room")
            @PathVariable("room_id")
            roomId: String?
    ): ResponseEntity<WrappedResponse<String?>> {
        return ResponseEntity.ok(
                ResponseDto(
                        code = HttpStatus.OK.value(),
                        page = PageDto(mutableListOf(roomService.deleteRoomByIdAndCinemaId(roomId, cinemaId)))
                ).validated()
        )
    }
}