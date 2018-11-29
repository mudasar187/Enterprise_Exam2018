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
	
	fun delete(paramId: String): String {
		
		val id = ValidationHandler.validateId(paramId)
		
		if (!repository.existsById(id)) {
			throw NotFoundException(ExceptionMessages.notFoundMessage("coupon", "id", paramId), 404)
		}
		
		repository.deleteById(id)
		
		return id.toString()
	}
	
	fun put(paramId: String, updatedTicketDto: TicketDto): String {
		
		val id = ValidationHandler.validateId(paramId)
		
		when {
			
			updatedTicketDto.id.isNullOrEmpty() -> throw UserInputValidationException(ExceptionMessages.missingRequiredField("id"))
			updatedTicketDto.seat.isNullOrEmpty() -> throw UserInputValidationException(ExceptionMessages.missingRequiredField("seat"))
			updatedTicketDto.price!!.isNaN() -> throw UserInputValidationException(ExceptionMessages.missingRequiredField("price"))
			
			!updatedTicketDto.id.equals(paramId) -> throw UserInputValidationException(ExceptionMessages.notMachingIds(), 409)
			!repository.existsById(id) -> throw NotFoundException(ExceptionMessages.notFoundMessage("ticket", "id", paramId), 404)
			
			else -> {
				val validatedSeat = ValidationHandler.validateSeatFormat(updatedTicketDto.seat!!)
				
				repository.updateTicket(id, updatedTicketDto.price!!, updatedTicketDto.seat!!)
				
				return id.toString()
			}
		}
	}
	
	fun patchSeat(paramId: String, jsonPatch: String): String {
		
		val id = ValidationHandler.validateId(paramId)
		
		//if the given is is not registred in the DB
		if (!repository.existsById(id)) {
			throw NotFoundException(ExceptionMessages.notFoundMessage("ticket", "id", paramId), 404)
		}
		
		val jacksonObjectMapper = ObjectMapper()
		
		val jsonNode = try { jacksonObjectMapper.readValue(jsonPatch, JsonNode::class.java) }
		
		catch (e: Exception) {
			throw UserInputValidationException(ExceptionMessages.invalidJsonFormat(), 409)
		}
		
		if (jsonNode.has("id")) {
			throw UserInputValidationException(ExceptionMessages.idInPatchDtoBody(), 400)
		}
		
		if (!jsonNode.has("seat")) {
			
			throw UserInputValidationException(ExceptionMessages.missingRequiredField("seat"), 400)
		}
		
		val seatNodeValue = jsonNode.get("seat").asText()
		val validatedSeatValue = ValidationHandler.validateSeatFormat(seatNodeValue)
		
		repository.updateSeat(id, validatedSeatValue)
		
		return id.toString()
	}
}