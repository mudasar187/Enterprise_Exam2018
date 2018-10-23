package no.ecm.cinema.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @author Christian Marker on 23/10/2018 at 15:32.
 */
@RequestMapping(
	path = ["/"])
@RestController
class CinemaController {
	
	@GetMapping
	fun get(): ResponseEntity<String>? {
		return ResponseEntity.ok("Cinema")
	}
}