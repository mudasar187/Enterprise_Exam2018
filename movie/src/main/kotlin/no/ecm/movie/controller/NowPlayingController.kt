package no.ecm.movie.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import no.ecm.movie.model.converter.NowPlayingConverter
import no.ecm.movie.model.entity.NowPlaying
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
import javax.websocket.server.PathParam

@Api(value = "/now-playing", description = "API for Now Playing entity")
@RequestMapping(
        path = ["/now-playing"],
        produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
@RestController
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

                      @ApiParam("Offset in the list of genres")
                      @RequestParam("offset", defaultValue = "0")
                      offset: Int,

                      @ApiParam("Limit of genres in a single retrieved page")
                      @RequestParam("limit", defaultValue = "10")
                      limit: Int): ResponseEntity<WrappedResponse<NowPlayingDto>> {
        val genreDtos = nowPlayingService.find(title, date)

        return ResponseEntity.ok().body(ResponseDto(
                code = 200,
                page = PageDto(list = genreDtos)
        )
        )

        //val builder = UriComponentsBuilder.fromPath("/genres")

        //if (!name.isNullOrBlank()) {
        //    builder.queryParam("name", name)
        //}

        //val pageDto = PageDtoGenerator<GenreDto>().generatePageDto(genreDtos, offset, limit)
        //return HalLinkGenerator<GenreDto>().generateHalLinks(genreDtos, pageDto, builder, limit, offset)
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