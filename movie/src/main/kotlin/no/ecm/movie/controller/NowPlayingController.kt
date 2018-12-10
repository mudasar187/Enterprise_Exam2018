package no.ecm.movie.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import no.ecm.movie.model.converter.NowPlayingConverter
import no.ecm.movie.service.NowPlayingService
import no.ecm.utils.cache.EtagHandler
import no.ecm.utils.dto.movie.NowPlayingDto
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

@Api(value = "/now-playings", description = "API for Now Playing entity")
@RequestMapping(
        path = ["/now-playings"],
        produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
@RestController
@CrossOrigin(origins = ["http://localhost:8080"])
class NowPlayingController(
        private var nowPlayingService: NowPlayingService
) {

    @ApiOperation("Get Now Playing, possible filter by name")
    @GetMapping
    fun getNowPlaying(@ApiParam("Title of the movie")
                      @RequestParam("title", required = false)
                      title: String?,

                      @ApiParam("date of the movie")
                      @RequestParam("date", required = false)
                      date: String?,

                      @ApiParam("cinemaId of the movie")
                      @RequestParam("cinemaId", required = false)
                      cinemaId: String?,

                      @ApiParam("Offset in the list of genres")
                      @RequestParam("offset", defaultValue = "0")
                      offset: Int,

                      @ApiParam("Limit of genres in a single retrieved page")
                      @RequestParam("limit", defaultValue = "10")
                      limit: Int): ResponseEntity<WrappedResponse<NowPlayingDto>> {
        val nowPlayingDtos = nowPlayingService.find(title, date, cinemaId)

        val builder = UriComponentsBuilder.fromPath("/now-playing")

        if (!title.isNullOrBlank()) {
        builder.queryParam("title", title)
        }
        if (!date.isNullOrBlank()) {
            builder.queryParam("date", date)
        }

        val pageDto = PageDtoGenerator<NowPlayingDto>().generatePageDto(nowPlayingDtos, offset, limit)
        return HalLinkGenerator<NowPlayingDto>().generateHalLinks(nowPlayingDtos, pageDto, builder, limit, offset)
    }

    @ApiOperation("Get Now Playing by Id")
    @GetMapping(path = ["/{id}"])
    fun getNowPlayingById(
            @ApiParam("Id of Now Playing")
            @PathVariable("id")
            id: String?
    ): ResponseEntity<WrappedResponse<NowPlayingDto>> {

        val dto = NowPlayingConverter.entityToDto(nowPlayingService.getNowPlayingById(id))
        val etag = EtagHandler<NowPlayingDto>().generateEtag(dto = dto)

        return ResponseEntity
                .status(HttpStatus.OK.value())
                .eTag(etag)
                .body(
                        ResponseDto(
                                code = HttpStatus.OK.value(),
                                page = PageDto(mutableListOf(dto))
                        )
                )
    }

    @ApiOperation("Create a Now Playing instanse")
    @PostMapping(consumes = ["application/json"])
    fun createMovie(
            @ApiParam("JSON object representing the Now Playing")
            @RequestBody nowPlayingDto: NowPlayingDto): ResponseEntity<WrappedResponse<NowPlayingDto>> {
        val dto = nowPlayingService.createNowPlaying(nowPlayingDto)
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(URI.create("/now-playing/${dto.id}"))
                .body(
                        ResponseDto(
                                code = HttpStatus.CREATED.value(),
                                page = PageDto(mutableListOf(dto))
                        ).validated()
                )
    }

    @ApiOperation("Update a seats using merge patch")
    @CrossOrigin(origins = ["http://localhost:8082", "http://localhost:7082", "http://order-server"])
    @PatchMapping(path = ["/{id}"], consumes = ["application/merge-patch+json"])
    fun patchNowPlaying(@ApiParam("The id of the Now Playing")
                   @PathVariable("id")
                   id: String,
                   @ApiParam("Content of ETag")
                   @RequestHeader("If-Match")
                   ifMatch: String?,
                   @ApiParam("The partial patch")
                   @RequestBody
                   jsonPatch: String) : ResponseEntity<Void> {

        val currentDto = NowPlayingConverter.entityToDto(nowPlayingService.getNowPlayingById(id))
        EtagHandler<NowPlayingDto>().validateEtags(currentDto, ifMatch)

        nowPlayingService.patchNowPlaying(id, jsonPatch)
        return ResponseEntity.noContent().build()
    }

    @ApiOperation("Delete by id")
    @DeleteMapping(path = ["/{id}"])
    fun deleteById(
            @ApiParam("Id of Now Playing")
            @PathVariable("id")
            id: String?
    ): ResponseEntity<WrappedResponse<String?>> {
        return ResponseEntity.ok(
                ResponseDto(
                        code = HttpStatus.OK.value(),
                        page = PageDto(mutableListOf(nowPlayingService.deleteById(id)))
                ).validated()
        )
    }


}