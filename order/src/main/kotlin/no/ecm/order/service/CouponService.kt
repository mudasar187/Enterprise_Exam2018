package no.ecm.order.service

import no.ecm.order.model.converter.CouponConverter
import no.ecm.order.repository.coupon.CouponRepository
import no.ecm.utils.converter.ConvertionHandler
import no.ecm.utils.dto.order.CouponDto
import no.ecm.utils.messages.ExceptionMessages
import no.ecm.utils.exception.NotFoundException
import no.ecm.utils.exception.UserInputValidationException
import no.ecm.utils.logger
import no.ecm.utils.validation.ValidationHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

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
			val id = ValidationHandler.validateId(paramId, "id")
			
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
		
		if (dto.code.isNullOrEmpty()) {
			throw UserInputValidationException(ExceptionMessages.missingRequiredField("code"))
		} else if (dto.description.isNullOrEmpty()) {
			throw UserInputValidationException(ExceptionMessages.missingRequiredField("description"))
		} else if (dto.expireAt == null) {
			throw UserInputValidationException(ExceptionMessages.missingRequiredField("expireAt"))
		}
		
		// New format for input = yyyy-MM-dd HH:mm:ss
		val formattedTime = "${dto.expireAt!!}.000000"
		val validatedTimeStamp: String = ValidationHandler.validateTimeFormat(formattedTime)
		val parsedDateTime = ConvertionHandler.convertTimeStampToZonedTimeDate(validatedTimeStamp)
		
		//val updated = Coupon(null, dto.code!!, dto.description!!, parsedDateTime!!)
		//return repository.save(updated).id.toString()
		
		
		val id = try { repository.createCoupon(dto.code!!, dto.description!!, parsedDateTime!!) }
		
		catch (e: Exception) {
			UserInputValidationException(ExceptionMessages.createEntity("coupon"))
		}
		
		return id.toString()
		
	}
	
	fun delete(paramId: String): String {
		
		val id= ValidationHandler.validateId(paramId, "id")
		
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
		
		val id = ValidationHandler.validateId(paramId, "id")
		
		if (!updatedCouponDto.id.equals(id.toString())) {
			throw UserInputValidationException(ExceptionMessages.notMachingIds(), 409)
		}
		
		if (!repository.existsById(id)) {
			throw NotFoundException(ExceptionMessages.notFoundMessage("coupon", "id", paramId), 404)
		}
		
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