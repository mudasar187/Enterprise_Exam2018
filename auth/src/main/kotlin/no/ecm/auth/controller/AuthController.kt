package no.ecm.auth.controller

import io.swagger.annotations.Api
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Api(value = "/auth", description = "API for auth entity")
@RequestMapping(
		path = ["/auth"],
		produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
@RestController
class AuthController {

	@Value("\${cinemaService}")
	private lateinit var cinemaHost : String

	@Value("\${movieService}")
	private lateinit var movieHost : String
	
	@GetMapping
	fun get(): ResponseEntity<String>? {
		return ResponseEntity.ok("Auth")
	}
}