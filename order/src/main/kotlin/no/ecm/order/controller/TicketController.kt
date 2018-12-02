package no.ecm.order.controller

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import no.ecm.order.model.converter.TicketConverter
import no.ecm.order.service.TicketService
import no.ecm.utils.dto.order.TicketDto
import no.ecm.utils.hal.HalLinkGenerator
import no.ecm.utils.hal.PageDto
import no.ecm.utils.hal.PageDtoGenerator
import no.ecm.utils.response.ResponseDto
import no.ecm.utils.response.WrappedResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

@Api(value = "/tickets", description = "API for ticket entity")
@RequestMapping(
	path = ["/tickets"],
	produces = [MediaType.APPLICATION_JSON_UTF8_VALUE])
@RestController
class TicketController {
	
	@Autowired
	private lateinit var service: TicketService
	
	@GetMapping
	fun getAll(@ApiParam("Offset in the list of tickets")
			   @RequestParam("offset", defaultValue = "0")
			   offset: Int,
				//
				@ApiParam("Limit of tickets in a single retrieved page")
				@RequestParam("limit", defaultValue = "10")
				limit: Int
	): ResponseEntity<WrappedResponse<TicketDto>> {
		val ticketResultList = service.get(null, offset, limit)
		
		val pageDto = PageDtoGenerator<TicketDto>().generatePageDto(ticketResultList, offset, limit)
		val builder = UriComponentsBuilder.fromPath("/tickets")
		
		return HalLinkGenerator<TicketDto>().generateHalLinks(ticketResultList, pageDto, builder, limit, offset)
	}
	
	@ApiOperation("Get a ticket by its ID")
	@GetMapping(path = ["/{id}"])
	fun getById(@ApiParam("Id of the ticket to be returned")
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
		
		val ticketResultList =  service.get(id, offset, limit)
		
		val pageDto = PageDtoGenerator<TicketDto>().generatePageDto(ticketResultList, offset, limit)
		val builder = UriComponentsBuilder.fromPath("/tickets")
		builder.queryParam("id", id)
		
		return HalLinkGenerator<TicketDto>().generateHalLinks(ticketResultList, pageDto, builder, limit, offset)
	}
	
	@ApiOperation("Create a new ticket")
	@PostMapping(consumes = [MediaType.APPLICATION_JSON_UTF8_VALUE])
	fun createCoupon(@ApiParam("Dto of a tickets: price, seat")
					 @RequestBody
					 dto: TicketDto
	) : ResponseEntity<WrappedResponse<TicketDto>> {
		
		val returnId = service.create(dto)
		
		return ResponseEntity
			.status(201)
			.location(URI.create("/tickets/$returnId"))
			.body(
				ResponseDto(
					code = 201,
					page = PageDto(list = mutableListOf(TicketDto(id = returnId)))
				).validated()
		)
	}
	
	@ApiOperation("Update all info for a given ticket")
	@PutMapping("/{id}", consumes = [MediaType.APPLICATION_JSON_UTF8_VALUE])
	fun updateTicket(@ApiParam("Id of the ticket to be updated")
					 @PathVariable("id", required = true)
					 id: String,
					//
					 @ApiParam("The updated ticketDto")
					 @RequestBody
					 updatedTicketDto: TicketDto
	): ResponseEntity<Void> {
		
		service.put(id, updatedTicketDto)
		return ResponseEntity.noContent().build()
	}
	
	@ApiOperation("Update a ticket with the given id")
	@PatchMapping(path = ["/{id}"], consumes = ["application/merge-patch+json"])
	fun patchTicketSeat(@ApiParam("id of ticket")
						@PathVariable("id", required = true)
						id: String,
						//
						@ApiParam("The partial patch (seat only).")
						@RequestBody jsonPatch: String
	): ResponseEntity<Void> {
		
		service.patchSeat(id, jsonPatch)
		return ResponseEntity.noContent().build()
	}
	
	@ApiOperation("Delete a ticket with the given id")
	@DeleteMapping(path = ["/{id}"])
	fun delete(@ApiParam("id of ticket")
			   @PathVariable("id", required = true)
			   id: String
	): ResponseEntity<WrappedResponse<TicketDto>> {
		val returnId = service.delete(id)
		
		return ResponseEntity.status(200).body(
			ResponseDto<TicketDto>(
				code = 200,
				message = "Coupon with paramId: $returnId successfully deleted"
			).validated()
		)
	}
}