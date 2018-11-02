package no.ecm.creditcard.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping(
        path = ["/creditcard"],
        produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
@RestController
class CreditCardController {

    @GetMapping
    fun get(): ResponseEntity<String>? {
        return ResponseEntity.ok("CreditCard")
    }
}