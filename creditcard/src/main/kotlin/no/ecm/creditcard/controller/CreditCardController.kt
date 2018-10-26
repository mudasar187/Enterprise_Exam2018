package no.ecm.creditcard.controller

import io.swagger.annotations.Api
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Api(value = "/creditcard", description = "API for creditcard entity")
@RequestMapping(
        path = ["/creditcard"],
        produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
@RestController
class CreditCardController {

    @Value("\${authService}")
    private lateinit var authHost : String

    @GetMapping
    fun get(): ResponseEntity<String>? {
        return ResponseEntity.ok("CreditCard")
    }
}