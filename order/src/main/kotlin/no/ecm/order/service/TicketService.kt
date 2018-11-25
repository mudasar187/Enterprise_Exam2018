package no.ecm.order.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.base.Throwables
import no.ecm.order.model.converter.TicketConverter
import no.ecm.order.repository.ticket.TicketRepository
import no.ecm.utils.dto.order.TicketDto
import no.ecm.utils.hal.HalLink
import no.ecm.utils.hal.PageDto
import no.ecm.utils.response.TicketResponseDto
import no.ecm.utils.response.WrappedResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder
import javax.validation.ConstraintViolationException

@Service
class TicketService {
	
	@Autowired
	private lateinit var repository: TicketRepository
	
	fun get(paramId: String?, offset: Int, limit: Int): ResponseEntity<WrappedResponse<TicketDto>> {
		
		if(offset < 0 || limit < 1) {
			return ResponseEntity.status(400).body(
				TicketResponseDto(
					code = 400,
					message = "Invalid offset or limit.	 Rules: Offset > 0 && limit >= 1"
				).validated()
			)
		}
		
		val ticketResultList: List<TicketDto>
		val builder = UriComponentsBuilder.fromPath("/tickets")
		
		if (paramId.isNullOrBlank()) {
			
			ticketResultList = TicketConverter.entityListToDtoList(repository.findAll())
		} else {
			
			val id = try { paramId!!.toLong() }
			
			catch (e: Exception) {
				return ResponseEntity.status(404).body(
					TicketResponseDto(
						code = 404,
						message = "Invalid id: $paramId"
					).validated()
				)
			}
			
			val entity = repository.findById(id).orElse(null)
				?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					TicketResponseDto(
						code = HttpStatus.NOT_FOUND.value(),
						message = "could not find ticket with ID: $id"
					).validated()
				)
			
			ticketResultList = listOf(TicketConverter.entityToDto(entity))
			
			builder.queryParam("id", paramId)
			
		}
		
		if (offset != 0 && offset >= ticketResultList.size) {
			
			return ResponseEntity.status(400).body(
				TicketResponseDto(
					code = 400,
					message = "Too large offset, size of result is ${ticketResultList.size}"
				).validated()
			)
		}
		
		builder.queryParam("limit", limit)
		
		val dto = TicketConverter.dtoListToPageDto(ticketResultList, offset, limit)
		
		// Build HalLinks
		dto._self = HalLink(builder.cloneBuilder()
			.queryParam("offset", offset)
			.build().toString()
		)
		
		if (!ticketResultList.isEmpty() && offset > 0) {
			dto.previous = HalLink(builder.cloneBuilder()
				.queryParam("offset", Math.max(offset - limit, 0))
				.build().toString()
			)
		}
		
		if (offset + limit < ticketResultList.size) {
			dto.next = HalLink(builder.cloneBuilder()
				.queryParam("offset", (offset + limit))
				.build().toString()
			)
		}
		
		val etag = ticketResultList.hashCode().toString()
		
		return ResponseEntity.status(200)
			.eTag(etag)
			.body(
				TicketResponseDto(
					code = 200,
					page = dto
				).validated()
			)
	}
	
	fun create(dto: TicketDto): ResponseEntity<WrappedResponse<TicketDto>> {
		
		if (dto.id != null) {
			return ResponseEntity.status(404).body(
				TicketResponseDto(
					code = 404,
					message = "id != null, you cannot create a coupon with predefined id"
				).validated()
			)
		}
		
		if (dto.price!!.isNaN() || dto.seat.isNullOrEmpty()) {
			return ResponseEntity.status(400).body(
				TicketResponseDto(
					code = 400,
					message = "You need to specify a code, description and expireAt when creating a Coupon, " +
						"please check documentation for more info"
				).validated()
			)
		}
		
		if(!checkSeatRegex(dto.seat!!)){
			return ResponseEntity.status(400).body(
				TicketResponseDto(
					code = 400,
					message = "Wrong formatting of seat, please see documentation for RegEx"
				).validated()
			)
		}
		
		val id = try { repository.createTicket(dto.price!!, dto.seat!!) }
		
		catch (e: Exception) {
			
			if (Throwables.getRootCause(e) is ConstraintViolationException) {
				return ResponseEntity.status(400).body(
					TicketResponseDto(
						code = 400,
						message = "Error while creating a ticket, contact sys-adm"
					).validated()
				)
			}
			throw e
		}
		
		return ResponseEntity.status(201).body(
			TicketResponseDto(
				code = 201,
				page = PageDto(list = mutableListOf(TicketDto(id = id.toString()))),
				message = "Coupon with id: $id was created"
			).validated()
		)
	}
	
	fun delete(paramId: String): ResponseEntity<WrappedResponse<TicketDto>> {
		
		val id = try { paramId.toLong() }
		
		catch (e: Exception) {
			return ResponseEntity.status(400).body(
				TicketResponseDto(
					code = 400,
					message = "Invalid id: $paramId"
				).validated()
			)
		}
		
		//if the given is is not registred in the DB
		if (!repository.existsById(id)) {
			return ResponseEntity.status(404).body(
				TicketResponseDto(
					code = 404,
					message = "Could not find coupon with id: $id"
				).validated()
			)
		}
		
		repository.deleteById(id)
		return ResponseEntity.status(204).body(
			TicketResponseDto(
				code = 204,
				message = "Coupon with id: $id successfully deleted"
			).validated()
		)
	}
	
	fun patchSeat(paramId: String, jsonPatch: String): ResponseEntity<WrappedResponse<TicketDto>> {
		
		val id = try { paramId.toLong() }
		
		catch (e: Exception) {
			return ResponseEntity.status(400).body(
				TicketResponseDto(
					code = 400,
					message = "Invalid id: $paramId"
				).validated()
			)
		}
		
		if (!repository.existsById(id)) {
			return ResponseEntity.status(404).body(
				TicketResponseDto(
					code = HttpStatus.NOT_FOUND.value(),
					message = "could not find ticket with ID: $id"
				).validated()
			)
		}
		
		val jacksonObjectMapper = ObjectMapper()
		
		val jsonNode = try { jacksonObjectMapper.readValue(jsonPatch, JsonNode::class.java) }
		
		catch (e: Exception) {
			
			//Invalid JSON data
			return ResponseEntity.status(409).body(
				TicketResponseDto(
					code = 409,
					message = "Invalid JSON data"
				).validated()
			)
		}
		
		// Updating the id is not allowed
		if (jsonNode.has("id")) {
			return ResponseEntity.status(400).body(
				TicketResponseDto(
					code = 400,
					message = "Updating the id is not allowed"
				).validated()
			)
		}
		
		if (!jsonNode.has("seat")) {
			return ResponseEntity.status(400).body(
				TicketResponseDto(
					code = 400,
					message = "You need to specify a seat in the jsonPatch data"
				).validated()
			)
		}
		
		val seatNodeValue = jsonNode.get("seat").asText()
		
		if(!checkSeatRegex(seatNodeValue)){
			return ResponseEntity.status(400).body(
				TicketResponseDto(
					code = 400,
					message = "Wrong formatting of seat, please see documentation for RegEx"
				).validated()
			)
		}
		
		repository.updateSeat(id, seatNodeValue)
		
		return ResponseEntity.status(204).body(
			TicketResponseDto(
				code = 204,
				message = "Ticket with id: $id successfully patched"
			).validated()
		)
		
	}
	
	private fun checkSeatRegex(text: String): Boolean {
		
		val regex = "^[A-Z][0-9]{1,2}".toRegex()
		return regex.matches(text)
		
	}
	
}