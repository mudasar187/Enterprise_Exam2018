package no.ecm.order.controller

import io.swagger.annotations.Api
import no.ecm.order.service.TicketService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Api(value = "/tickets", description = "API for order entity")
@RequestMapping(
	path = ["/tickets"],
	produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
@RestController
class TicketController {
	
	@Autowired
	private lateinit var service: TicketService
	
	
	
	/*
	GET -> alle tickets
	GET /{id} -> henter ticket basert på id
	POST -> Opprette ticket
	PATCH /{id} -> Oppdatere ticket, f.eks. oppdatere seatNumber.
	DELETE /{id} -> Slette en invoice
	*/
	
	
	
}