package no.ecm.user.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping(
        path = ["/user"],
        produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
@RestController
class UserController {

    @Value("\${authService}")
    private lateinit var authHost : String

    @GetMapping
    fun get(): ResponseEntity<String>? {
        return ResponseEntity.ok("User")
    }
}