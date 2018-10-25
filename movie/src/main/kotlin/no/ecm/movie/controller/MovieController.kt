package no.ecm.movie.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping(
        path = ["/"])
@RestController
class MovieController {

    @GetMapping
    fun get(): ResponseEntity<String>? {
        return ResponseEntity.ok("Movie")
    }
}