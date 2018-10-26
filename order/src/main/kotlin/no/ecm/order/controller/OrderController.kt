package no.ecm.order.controller

import io.swagger.annotations.Api
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Api(value = "/order", description = "API for order entity")
@RequestMapping(
        path = ["/order"],
        produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
@RestController
class OrderController {

    @Value("\${authService}")
    private lateinit var authHost : String

    @Value("\${movieService}")
    private lateinit var movieHost : String

    @GetMapping
    fun get(): ResponseEntity<String>? {
        return ResponseEntity.ok("Order")
    }
}