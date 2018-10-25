package no.ecm.gateway.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping(
        path = ["/"])
@RestController
class GatewayController {

    @GetMapping
    fun get(): ResponseEntity<String>? {
        return ResponseEntity.ok("Gateway")
    }
}