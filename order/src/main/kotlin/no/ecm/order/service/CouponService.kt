package no.ecm.order.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.base.Throwables
import jdk.nashorn.internal.runtime.regexp.joni.exception.ErrorMessages
import no.ecm.order.model.converter.CouponConverter
import no.ecm.order.model.entity.Coupon
import no.ecm.order.repository.coupon.CouponRepository
import no.ecm.utils.converter.ConvertionHandler
import no.ecm.utils.dto.order.CouponDto
import no.ecm.utils.dto.order.TicketDto
import no.ecm.utils.exception.ExceptionMessages
import no.ecm.utils.exception.NotFoundException
import no.ecm.utils.exception.UserInputValidationException
import no.ecm.utils.hal.HalLink
import no.ecm.utils.hal.PageDto
import no.ecm.utils.logger
import no.ecm.utils.response.CouponResponseDto
import no.ecm.utils.response.ResponseDto
import no.ecm.utils.response.WrappedResponse
import no.ecm.utils.validation.ValidationHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.validation.ConstraintViolationException

@Service
class CouponService {
	
	@Autowired
	private lateinit var repository: CouponRepository
	
	val logger = logger<CouponService>()
	
	fun get(paramCode: String?, paramId: String?, offset: Int, limit: Int) : MutableList<CouponDto> {
		
		ValidationHandler.validateLimitAndOffset(offset, limit)
		
		val couponResultList: MutableList<CouponDto>
		//val builder = UriComponentsBuilder.fromPath("/coupons")
		
		//If NOT paramCode or paramId are present, return all coupons in DB
		if (paramCode.isNullOrBlank() && paramId.isNullOrBlank()) {
			
			couponResultList = CouponConverter.entityListToDtoList(repository.findAll())
			
		}
		
		//If only paramCode are present, return coupon with given code
		else if (!paramCode.isNullOrBlank() && paramId.isNullOrBlank()){
			
			couponResultList = try { mutableListOf(CouponConverter.entityToDto(repository.findByCode(paramCode!!))) }
			
			catch (e: Exception) {
				
				val errorMsg = ExceptionMessages.notFoundMessage("coupon", "code", paramCode!!)
				logger.warn(errorMsg)
				throw NotFoundException(errorMsg, 404)
			}
		}
		
		//If only paramId are present, return coupon with given id
		else {
			val id = ValidationHandler.validateId(paramId)
			
			couponResultList = try { mutableListOf(CouponConverter.entityToDto(repository.findById(id).get())) }
			
			catch (e: Exception) {
				val errorMsg = ExceptionMessages.notFoundMessage("coupon", "id", paramId!!)
				logger.warn(errorMsg)
				throw NotFoundException(errorMsg, 404)
			}
		}
		
		return couponResultList
	}
	
	fun create(dto: CouponDto): String {
		
		
		if (dto.id != null) {
			throw UserInputValidationException(ExceptionMessages.idInCreationDtoBody("coupon"), 404)
		}
		
		when {
			dto.code.isNullOrEmpty() -> throw UserInputValidationException(ExceptionMessages.missingRequiredField("code"))
			dto.description.isNullOrEmpty() -> throw UserInputValidationException(ExceptionMessages.missingRequiredField("description"))
			dto.expireAt == null -> throw UserInputValidationException(ExceptionMessages.missingRequiredField("expireAt"))
			
			// New format for input = yyyy-MM-dd HH:mm:ss
			
			//val updated = Coupon(null, dto.code!!, dto.description!!, parsedDateTime!!)
			//return repository.save(updated).id.toString()
			else -> {
				val formattedTime = "${dto.expireAt!!}.000000"
				val validatedTimeStamp: String = ValidationHandler.validateTimeFormat(formattedTime)
				val parsedDateTime = ConvertionHandler.convertTimeStampToZonedTimeDate(validatedTimeStamp)
				
				//val updated = Coupon(null, dto.code!!, dto.description!!, parsedDateTime!!)
				//return repository.save(updated).id.toString()
				
				
				val id = try {
					repository.createCoupon(dto.code!!, dto.description!!, parsedDateTime!!)
				} catch (e: Exception) {
					UserInputValidationException(ExceptionMessages.createEntity("coupon"))
				}
				
				return id.toString()
				
			}
		}
		
		
	}
	
	fun delete(paramId: String): String {
		
		val id= ValidationHandler.validateId(paramId)
		
		//if the given is is not registred in the DB
		if (!repository.existsById(id)) {
			throw NotFoundException(ExceptionMessages.notFoundMessage("coupon", "id", paramId), 404)
		}
		
		try {
			repository.deleteById(id)
		}
		catch (e: Exception) {
			throw UserInputValidationException(ExceptionMessages.deleteEntity("coupon"))
		}
		
		return id.toString()
	}
	
	fun put(paramId: String, updatedCouponDto: CouponDto): String {
		
		val id = ValidationHandler.validateId(paramId)
		
		when {
			
			updatedCouponDto.id.isNullOrEmpty() -> throw UserInputValidationException(ExceptionMessages.missingRequiredField("id"))
			updatedCouponDto.code.isNullOrEmpty() -> throw UserInputValidationException(ExceptionMessages.missingRequiredField("code"))
			updatedCouponDto.description.isNullOrEmpty() -> throw UserInputValidationException(ExceptionMessages.missingRequiredField("description"))
			updatedCouponDto.expireAt.isNullOrEmpty() -> throw UserInputValidationException(ExceptionMessages.missingRequiredField("expireAt"))
			
			!updatedCouponDto.id.equals(id.toString()) -> throw UserInputValidationException(ExceptionMessages.notMachingIds(), 409)
			!repository.existsById(id) -> throw NotFoundException(ExceptionMessages.notFoundMessage("coupon", "id", paramId), 404)
			
			else -> {
				val formattedTime = "${updatedCouponDto.expireAt!!}.000000"
				val validatedTimeStamp: String = ValidationHandler.validateTimeFormat(formattedTime)
				val parsedDateTime = ConvertionHandler.convertTimeStampToZonedTimeDate(validatedTimeStamp)
				
				try {
					assert(repository.updateCoupon(id, updatedCouponDto.code!!, updatedCouponDto.description!!, parsedDateTime!!))
				} catch (e: Exception) {
					throw UserInputValidationException(ExceptionMessages.updateEntity("coupon"))
				}
				
				return id.toString()
			}
		}
		
	}
	
	fun patchDescription(paramId: String, jsonPatch: String): String {
		
		val id = ValidationHandler.validateId(paramId)
		
		//if the given is is not registred in the DB
		if (!repository.existsById(id)) {
			throw NotFoundException(ExceptionMessages.notFoundMessage("coupon", "id", paramId), 404)
		}
		
		val jacksonObjectMapper = ObjectMapper()
		
		val jsonNode = try { jacksonObjectMapper.readValue(jsonPatch, JsonNode::class.java) }
		
		catch (e: Exception) {
			throw UserInputValidationException(ExceptionMessages.invalidJsonFormat(), 409)
		}
		
		if (jsonNode.has("id")) {
			throw UserInputValidationException(ExceptionMessages.idInPatchDtoBody(), 400)
		}
		
		if (!jsonNode.has("description")) {
			throw UserInputValidationException(ExceptionMessages.missingRequiredField("description"), 400)
		}
		
		val descNodeValue = jsonNode.get("description").asText()
		
		try {
			repository.updateDescription(id, descNodeValue)
		} catch (e: Exception) {
			throw UserInputValidationException(ExceptionMessages.updateEntity("coupon"), 400)
		}
		
		return id.toString()
		
	}
}