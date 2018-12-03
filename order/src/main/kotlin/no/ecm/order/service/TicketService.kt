package no.ecm.order.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import no.ecm.order.model.converter.TicketConverter
import no.ecm.order.repository.ticket.TicketRepository
import no.ecm.utils.dto.order.TicketDto
import no.ecm.utils.exception.NotFoundException
import no.ecm.utils.exception.UserInputValidationException
import no.ecm.utils.logger
import no.ecm.utils.messages.ExceptionMessages
import no.ecm.utils.messages.InfoMessages
import no.ecm.utils.validation.ValidationHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TicketService {
	
	@Autowired
	private lateinit var repository: TicketRepository
	
	val logger = logger<TicketService>()
	
	fun get(paramId: String?, offset: Int, limit: Int): MutableList<TicketDto> {
		
		ValidationHandler.validateLimitAndOffset(offset, limit)
		
		val ticketResultList: MutableList<TicketDto>
		
		if (paramId.isNullOrBlank()) {
			
			ticketResultList = TicketConverter.entityListToDtoList(repository.findAll())
			
		} else {
			
			val id = ValidationHandler.validateId(paramId, "id")
			
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
			dto.id != null -> {
				val errorMsg = ExceptionMessages.missingRequiredField("id")
				logger.warn(errorMsg)
				throw UserInputValidationException(errorMsg, 400)
			}
			dto.price!!.isNaN() -> {
				val errorMsg = ExceptionMessages.missingRequiredField("price")
				logger.warn(errorMsg)
				throw UserInputValidationException(errorMsg, 400)
			}
			dto.seat.isNullOrEmpty() -> {
				val errorMsg = ExceptionMessages.missingRequiredField("seat")
				logger.warn(errorMsg)
				throw UserInputValidationException(errorMsg, 400)
			}
			
			else -> {
				
				val validatedSeat = ValidationHandler.validateSeatFormat(dto.seat!!)
				
				val id = repository.createTicket(dto.price!!, validatedSeat)
				logger.info(InfoMessages.entityCreatedSuccessfully("ticket", id.toString()))
				
				return id.toString()
			}
		}
	}
	
	fun delete(paramId: String): String {
		
		val id = ValidationHandler.validateId(paramId, "id")
		
		if (!repository.existsById(id)) {
			val errorMsg = ExceptionMessages.notFoundMessage("ticket", "id", paramId)
			logger.warn(errorMsg)
			throw NotFoundException(errorMsg, 404)
		}
		
		try {
			repository.deleteById(id)
		} finally {
			logger.info(InfoMessages.entitySuccessfullyDeleted("ticket", paramId))
		}
		
		
		return id.toString()
	}
	
	fun put(paramId: String, updatedTicketDto: TicketDto): String {
		
		val id = ValidationHandler.validateId(paramId, "id")
		
		when {
			updatedTicketDto.id.isNullOrEmpty() -> {
				val errorMsg = ExceptionMessages.missingRequiredField("id")
				logger.warn(errorMsg)
				throw UserInputValidationException(errorMsg, 400)
			}
			updatedTicketDto.seat.isNullOrEmpty() -> {
				val errorMsg = ExceptionMessages.missingRequiredField("seat")
				logger.warn(errorMsg)
				throw UserInputValidationException(errorMsg, 400)
			}
			updatedTicketDto.price!!.isNaN() -> {
				val errorMsg = ExceptionMessages.missingRequiredField("price")
				logger.warn(errorMsg)
				throw UserInputValidationException(errorMsg, 400)
			}
			
			!updatedTicketDto.id.equals(paramId) -> {
				val errorMsg = ExceptionMessages.notMachingIds("id")
				logger.warn(errorMsg)
				throw UserInputValidationException(errorMsg, 409)
			}
			!repository.existsById(id) -> {
				val errorMsg = ExceptionMessages.notFoundMessage("ticket", "id", paramId)
				logger.warn(errorMsg)
				throw NotFoundException(errorMsg, 404)
			}
			
			else -> {
				val validatedSeat = ValidationHandler.validateSeatFormat(updatedTicketDto.seat!!)
				
				try {
					repository.updateTicket(id, updatedTicketDto.price!!, validatedSeat)
				} finally {
					//logger.info(InfoMessages.entitySuccessfullyUpdated("ticket"))
				}
				
				return id.toString()
			}
		}
	}
	
	fun patchSeat(paramId: String, jsonPatch: String): String {
		
		val id = ValidationHandler.validateId(paramId, "id")
		
		//if the given is is not registred in the DB
		if (!repository.existsById(id)) {
			val errorMsg = ExceptionMessages.notFoundMessage("ticket", "id", paramId)
			logger.warn(errorMsg)
			throw NotFoundException(errorMsg, 404)
		}
		
		val jacksonObjectMapper = ObjectMapper()
		
		val jsonNode = try {
			
			jacksonObjectMapper.readValue(jsonPatch, JsonNode::class.java)
			
		} catch (e: Exception) {
			
			val errorMsg = ExceptionMessages.invalidJsonFormat()
			logger.warn(errorMsg)
			throw UserInputValidationException(ExceptionMessages.invalidJsonFormat(), 409)
		}
		
		if (jsonNode.has("id")) {
			val errorMsg = ExceptionMessages.illegalParameter("id")
			logger.warn(errorMsg)
			throw UserInputValidationException(errorMsg, 400)
		}
		
		if (!jsonNode.has("seat")) {
			val errorMsg = ExceptionMessages.missingRequiredField("seat")
			logger.warn(errorMsg)
			throw UserInputValidationException(errorMsg, 400)
		}
		
		val seatNodeValue = jsonNode.get("seat").asText()
		val validatedSeatValue = ValidationHandler.validateSeatFormat(seatNodeValue)
		
		try {
			repository.updateSeat(id, validatedSeatValue)
		} finally {
			logger.info(InfoMessages.entityFieldUpdatedSuccessfully("ticket", paramId, "seat"))
		}
		
		return id.toString()
	}
}