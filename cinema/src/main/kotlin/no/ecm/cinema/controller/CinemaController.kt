package no.ecm.cinema.controller

import io.swagger.annotations.Api
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Api(value = "/cinema", description = "API for cinema entity")
@RequestMapping(
        path = ["/cinema"],
        produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
@RestController
class CinemaController {

    @GetMapping
    fun get(): ResponseEntity<String>? {
        return ResponseEntity.ok("Cinema")
    }
}