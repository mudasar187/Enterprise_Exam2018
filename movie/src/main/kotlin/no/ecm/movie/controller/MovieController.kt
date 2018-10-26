package no.ecm.movie.controller

import io.swagger.annotations.Api
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Api(value = "/movie", description = "API for movie entity")
@RequestMapping(
        path = ["/movie"],
        produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
@RestController
class MovieController {

    @Value("\${cinemaService}")
    private lateinit var cinemaHost : String

    @GetMapping
    fun get(): ResponseEntity<String>? {
        return ResponseEntity.ok("Movie")
    }
}