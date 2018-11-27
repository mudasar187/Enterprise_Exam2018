package no.ecm.order.service

import com.google.common.base.Throwables
import jdk.nashorn.internal.runtime.regexp.joni.exception.ErrorMessages
import no.ecm.order.model.converter.CouponConverter
import no.ecm.order.model.entity.Coupon
import no.ecm.order.repository.coupon.CouponRepository
import no.ecm.utils.converter.ConvertionHandler
import no.ecm.utils.dto.order.CouponDto
import no.ecm.utils.exception.ExceptionMessages
import no.ecm.utils.exception.NotFoundException
import no.ecm.utils.exception.UserInputValidationException
import no.ecm.utils.hal.HalLink
import no.ecm.utils.hal.PageDto
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
	
	fun get(paramCode: String?, paramId: String?, offset: Int, limit: Int) : ResponseEntity<WrappedResponse<CouponDto>> {
		
		if(offset < 0 || limit < 1) {
			
			throw UserInputValidationException(ExceptionMessages.offsetAndLimitInvalid(), 400)
			
		}
		
		val couponResultList: List<CouponDto>
		val builder = UriComponentsBuilder.fromPath("/coupons")
		
		//If NOT paramCode or paramId are present, return all coupons in DB
		if (paramCode.isNullOrBlank() && paramId.isNullOrBlank()) {
			
			couponResultList = CouponConverter.entityListToDtoList(repository.findAll())
			
		}
		
		//If only paramCode are present, return coupon with given code
		else if (!paramCode.isNullOrBlank() && paramId.isNullOrBlank()){
			
			couponResultList = try { listOf(CouponConverter.entityToDto(repository.findByCode(paramCode!!))) }
			
			catch (e: Exception) {
				throw NotFoundException(ExceptionMessages.notFoundMessage("coupon", "code", paramCode!!), 404)
			}
			
			builder.queryParam("code", paramCode)
		}
		
		//If only paramId are present, return coupon with given id
		else {
			
			val id = ValidationHandler.validateId(paramId)
			
			couponResultList = try { listOf(CouponConverter.entityToDto(repository.findById(id).get())) }
			
			catch (e: Exception) {
				throw NotFoundException(ExceptionMessages.notFoundMessage("coupon", "id", "$paramId"), 404)
			}
			
			builder.queryParam("id", paramId)
			
		}
		
		if (offset != 0 && offset >= couponResultList.size) {
			
			throw UserInputValidationException(ExceptionMessages.tooLargeOffset(couponResultList.size))
			
		}
		
		builder.queryParam("limit", limit)
		
		val dto = CouponConverter.dtoListToPageDto(couponResultList, offset, limit)
		
		// Build HalLinks
		dto._self = HalLink(builder.cloneBuilder()
			.queryParam("offset", offset)
			.build().toString()
		)
		
		if (!couponResultList.isEmpty() && offset > 0) {
			dto.previous = HalLink(builder.cloneBuilder()
				.queryParam("offset", Math.max(offset - limit, 0))
				.build().toString()
			)
		}
		
		if (offset + limit < couponResultList.size) {
			dto.next = HalLink(builder.cloneBuilder()
				.queryParam("offset", (offset + limit))
				.build().toString()
			)
		}
		
		val etag = couponResultList.hashCode().toString()
		
		return ResponseEntity.status(HttpStatus.OK)
			.eTag(etag)
			.body(
				ResponseDto<CouponDto>(
					code = HttpStatus.OK.value(),
					page = dto
				).validated()
			)
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
		
		val id= ValidationHandler.validateId(paramId)
		
		//if the given is is not registred in the DB
		if (!repository.existsById(id)) {
			throw NotFoundException(ExceptionMessages.notFoundMessage("coupon", "id", paramId), 404)
		}
		
		try { repository.deleteById(id) }
		catch (e: Exception) {
			throw UserInputValidationException(ExceptionMessages.deleteEntity("coupon"))
		}
		
		return id.toString()
	}
}