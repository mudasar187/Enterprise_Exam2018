package no.ecm.order.service

import com.google.common.base.Throwables
import no.ecm.order.model.converter.CouponConverter
import no.ecm.order.repository.coupon.CouponRepository
import no.ecm.utils.dto.order.CouponDto
import no.ecm.utils.hal.HalLink
import no.ecm.utils.hal.PageDto
import no.ecm.utils.response.CouponResponseDto
import no.ecm.utils.response.WrappedResponse
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
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
				CouponResponseDto(
					code = HttpStatus.BAD_REQUEST.value(),
					message = "Invalid offset or limit.	 Rules: Offset > 0 && limit >= 1"
				).validated()
			)
		}
		
		val couponResultList: List<CouponDto>
		val builder = UriComponentsBuilder.fromPath("/coupon")
		
		//If NOT paramCode or paramId are present, return all coupons in DB
		if (paramCode.isNullOrBlank() && paramId.isNullOrBlank()) {
			
			couponResultList = CouponConverter.entityListToDtoList(repository.findAll())
			
		}
		
		//If only paramCode are present, return coupon with given code
		else if (!paramCode.isNullOrBlank() && paramId.isNullOrBlank()){
			
			couponResultList = try { listOf(CouponConverter.entityToDto(repository.findByCode(paramCode!!))) }
			
			catch (e: java.lang.Exception) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					CouponResponseDto(
						code = HttpStatus.NOT_FOUND.value(),
						message = "Could now find coupon with code: $paramCode"
					).validated()
				)
			}
			
			builder.queryParam("code", paramCode)
		}
		
		//If only paramId are present, return coupon with given id
		else {
			
			val id = try {
				paramId!!.toLong()
			} catch (e: Exception) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					CouponResponseDto(
						code = HttpStatus.NOT_FOUND.value(),
						message = "Invalid id: $paramId"
					).validated()
				)
			}
			
			val entity = repository.findById(id).orElse(null)
				?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					CouponResponseDto(
						code = HttpStatus.NOT_FOUND.value(),
						message = "could not find coupon with ID: $id"
					).validated()
				)
			
			couponResultList = listOf(CouponConverter.entityToDto(entity))
			
			builder.queryParam("id", paramId)
			
		}
		
		if (offset != 0 && offset >= couponResultList.size) {
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
				CouponResponseDto(
					code = HttpStatus.BAD_REQUEST.value(),
					message = "Too large offset, size of result is ${couponResultList.size}"
				).validated()
			)
			
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
				CouponResponseDto(
					code = HttpStatus.OK.value(),
					page = dto
				).validated()
			)
	}
	
	fun create(dto: CouponDto): ResponseEntity<WrappedResponse<CouponDto>> {
		
		if (dto.id != null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
				CouponResponseDto(
					code = HttpStatus.NOT_FOUND.value(),
					message = "id != null, you cannot create a coupon with predefined id"
				).validated()
			)
		}
		
		if (dto.code.isNullOrEmpty() || dto.description.isNullOrEmpty() || dto.expireAt == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
				CouponResponseDto(
					code = HttpStatus.BAD_REQUEST.value(),
					message = "You need to specify a code, description and expireAt when creating a Coupon, " +
						"please check documentation for more info"
				).validated()
			)
		}
		
		println("Unparsed date: ${dto.expireAt}")
		
		
		//Converting string to ZonedDateTime
		val pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS"
		val parser: DateTimeFormatter = DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.systemDefault())
		
		val parsedDateTime = try {
			
			ZonedDateTime.parse(dto.expireAt, parser)
			
		} catch (e: Exception) {
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
				CouponResponseDto(
					code = HttpStatus.BAD_REQUEST.value(),
					message = "Bad expireAt format!, This is of datatype ZonedDateTime and follows following formatting: \"yyyy-MM-dd HH:mm:ss.SSSSSS\""
				).validated()
			)
		}
		
		println("Parsed ZonedDateTime: $parsedDateTime")
		
		val id = try {
			repository.createCoupon(dto.code!!, dto.description!!, parsedDateTime)
		} catch (e: Exception) {
			
			if (Throwables.getRootCause(e) is ConstraintViolationException) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
					CouponResponseDto(
						code = HttpStatus.BAD_REQUEST.value(),
						message = "Error while creating a pokemon, contact sys-adm"
					).validated()
				)
			}
			throw e
		}
		
		return ResponseEntity.status(HttpStatus.CREATED).body(
			CouponResponseDto(
				code = HttpStatus.CREATED.value(),
				page = PageDto(list = mutableListOf(CouponDto(id = id.toString()))),
				message = "Coupon with id: $id was created"
			).validated()
		)
	}
	
	fun delete(paramId: String) : ResponseEntity<WrappedResponse<CouponDto>> {
		
		val id= try { paramId.toLong() }
		
		catch (e: Exception) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
				CouponResponseDto(
					code = HttpStatus.BAD_REQUEST.value(),
					message = "Invalid id: $paramId"
				).validated()
			)
		}
		
		//if the given is is not registred in the DB
		if (!repository.existsById(id)) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
				CouponResponseDto(
					code = HttpStatus.NOT_FOUND.value(),
					message = "Could not find coupon with id: $id"
				).validated()
			)
		}
		
		repository.deleteById(id)
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
			CouponResponseDto(
				code = HttpStatus.NO_CONTENT.value(),
				message = "Coupon with id: $id successfully deleted"
			).validated()
		)
	}
}