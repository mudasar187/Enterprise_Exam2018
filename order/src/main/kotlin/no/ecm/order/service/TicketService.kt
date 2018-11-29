package no.ecm.order.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.base.Throwables
import no.ecm.order.model.converter.TicketConverter
import no.ecm.order.repository.ticket.TicketRepository
import no.ecm.utils.dto.order.TicketDto
import no.ecm.utils.exception.ExceptionMessages
import no.ecm.utils.exception.NotFoundException
import no.ecm.utils.exception.UserInputValidationException
import no.ecm.utils.hal.HalLink
import no.ecm.utils.hal.PageDto
import no.ecm.utils.logger
import no.ecm.utils.response.ResponseDto
import no.ecm.utils.response.TicketResponseDto
import no.ecm.utils.response.WrappedResponse
import no.ecm.utils.validation.ValidationHandler
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
	
	val logger = logger<CouponService>()
	
	fun get(paramId: String?, offset: Int, limit: Int): MutableList<TicketDto> {
		
		ValidationHandler.validateLimitAndOffset(offset, limit)
		
		val ticketResultList: MutableList<TicketDto>
		//val builder = UriComponentsBuilder.fromPath("/tickets")
		
		if (paramId.isNullOrBlank()) {
			
			ticketResultList = TicketConverter.entityListToDtoList(repository.findAll())
			
		} else {
			
			val id = ValidationHandler.validateId(paramId)
			
			ticketResultList = try {
				mutableListOf(TicketConverter.entityToDto(repository.findById(id).get()))
			} catch (e: Exception) {
				val errorMsg = ExceptionMessages.notFoundMessage("coupon", "id", paramId!!)
				logger.warn(errorMsg)
				throw NotFoundException(errorMsg, 404)
			}
			
		}
		
		return ticketResultList
	}
	
	fun create(dto: TicketDto): String {
		
		when {
			dto.id != null -> throw UserInputValidationException(ExceptionMessages.idInCreationDtoBody("ticket"), 404)
			
			dto.price!!.isNaN() -> throw UserInputValidationException(ExceptionMessages.missingRequiredField("price"))
			dto.seat.isNullOrEmpty() -> throw UserInputValidationException(ExceptionMessages.missingRequiredField("seat"))
			
			else -> {
				
				val validatedSeat = ValidationHandler.validateSeatFormat(dto.seat!!)
				
				val id = repository.createTicket(dto.price!!, dto.seat!!)
				
				return id.toString()
				
			}
		}
	}
	
	fun delete(paramId: String): ResponseEntity<WrappedResponse<TicketDto>> {
		
		val id = try { paramId.toLong() }
		
		catch (e: Exception) {
			return ResponseEntity.status(400).body(
				ResponseDto<TicketDto>(
					code = 400,
					message = "Invalid id: $paramId"
				).validated()
			)
		}
		
		//if the given is is not registred in the DB
		if (!repository.existsById(id)) {
			return ResponseEntity.status(404).body(
				ResponseDto<TicketDto>(
					code = 404,
					message = "Could not find coupon with id: $id"
				).validated()
			)
		}
		
		repository.deleteById(id)
		return ResponseEntity.status(204).body(
			ResponseDto<TicketDto>(
				code = 204,
				message = "Coupon with id: $id successfully deleted"
			).validated()
		)
	}
	
	fun patchSeat(paramId: String, jsonPatch: String): ResponseEntity<WrappedResponse<TicketDto>> {
		
		val id = try { paramId.toLong() }
		
		catch (e: Exception) {
			return ResponseEntity.status(400).body(
				ResponseDto<TicketDto>(
					code = 400,
					message = "Invalid id: $paramId"
				).validated()
			)
		}
		
		if (!repository.existsById(id)) {
			return ResponseEntity.status(404).body(
				ResponseDto<TicketDto>(
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
				ResponseDto<TicketDto>(
					code = 409,
					message = "Invalid JSON data"
				).validated()
			)
		}
		
		// Updating the id is not allowed
		if (jsonNode.has("id")) {
			return ResponseEntity.status(400).body(
				ResponseDto<TicketDto>(
					code = 400,
					message = "Updating the id is not allowed"
				).validated()
			)
		}
		
		if (!jsonNode.has("seat")) {
			return ResponseEntity.status(400).body(
				ResponseDto<TicketDto>(
					code = 400,
					message = "You need to specify a seat in the jsonPatch data"
				).validated()
			)
		}
		
		val seatNodeValue = jsonNode.get("seat").asText()
		
		if(!checkSeatRegex(seatNodeValue)){
			return ResponseEntity.status(400).body(
				ResponseDto<TicketDto>(
					code = 400,
					message = "Wrong formatting of seat, please see documentation for RegEx"
				).validated()
			)
		}
		
		repository.updateSeat(id, seatNodeValue)
		
		return ResponseEntity.status(204).body(
			ResponseDto<TicketDto>(
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