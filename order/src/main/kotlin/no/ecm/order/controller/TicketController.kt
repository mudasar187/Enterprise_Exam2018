package no.ecm.order.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import no.ecm.order.service.TicketService
import no.ecm.utils.dto.order.TicketDto
import no.ecm.utils.response.WrappedResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Api(value = "/tickets", description = "API for order entity")
@RequestMapping(
	path = ["/tickets"],
	produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
@RestController
class TicketController {
	
	@Autowired
	private lateinit var service: TicketService
	
	@GetMapping
	fun getAll(
		@ApiParam("Offset in the list of tickets")
		@RequestParam("offset", defaultValue = "0")
		offset: Int,
		//
		@ApiParam("Limit of tickets in a single retrieved page")
		@RequestParam("limit", defaultValue = "10")
		limit: Int
	): ResponseEntity<WrappedResponse<TicketDto>> {
		return service.get(null, offset, limit)
	}
	
	@ApiOperation("Get a ticket by its ID")
	@GetMapping(path = ["/{id}"])
	fun getById(
		@ApiParam("Id of the ticket to be returned")
		@PathVariable("id", required = true)
		id: String,
		//
		@ApiParam("Offset in the list of tickets")
		@RequestParam("offset", defaultValue = "0")
		offset: Int,
		//
		@ApiParam("Limit of tickets in a single retrieved page")
		@RequestParam("limit", defaultValue = "10")
		limit: Int
	): ResponseEntity<WrappedResponse<TicketDto>> {
		
		return service.get(id, offset, limit)
	}
	
	@ApiOperation("Create a new ticket")
	@PostMapping(consumes = [MediaType.APPLICATION_JSON_UTF8_VALUE])
	fun createCoupon(
		@ApiParam("Dto of a tickets: price, seatnumber, seat")
		@RequestBody dto: TicketDto
	) : ResponseEntity<WrappedResponse<TicketDto>> {
		
		return service.create(dto)
		
	}
	
	@ApiOperation("Delete a ticket with the given id")
	@DeleteMapping(path = ["/{id}"])
	fun deletePokemon(@ApiParam("id of ticket")
					  @PathVariable("id", required = true)
					  id: String
	): ResponseEntity<WrappedResponse<TicketDto>> {
		return service.delete(id)
	}
	
	/*
	# GET -> alle tickets
	# GET /{id} -> henter ticket basert på id
	# POST -> Opprette ticket
	PATCH /{id} -> Oppdatere ticket, f.eks. oppdatere seatNumber.
	# DELETE /{id} -> Slette en invoice
	*/
	
	
	
}