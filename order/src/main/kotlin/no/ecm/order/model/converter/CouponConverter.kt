package no.ecm.order.model.converter

import no.ecm.order.model.entity.Coupon
import no.ecm.utils.converter.ConvertionHandler
import no.ecm.utils.dto.order.CouponDto
import no.ecm.utils.hal.PageDto
import no.ecm.utils.response.CouponResponseDto
import no.ecm.utils.validation.ValidationHandler
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.streams.toList

object CouponConverter {
	
	fun entityToDto(entity: Coupon): CouponDto {
		
		return CouponDto(
			id = entity.id.toString(),
			code = entity.code,
			description = entity.description,
			expireAt = entity.expireAt.toString(),
			percentage = entity.percentage
		)
	}
	
	fun dtoToEntity(dto: CouponDto) : Coupon {
		
		val formattedTime = "${dto.expireAt!!}.000000"
		val validatedTimeStamp: String = ValidationHandler.validateTimeFormat(formattedTime)
		val parsedDateTime = ConvertionHandler.convertTimeStampToZonedTimeDate(validatedTimeStamp)
		
		/*
		//Converting string to ZonedDateTime
		val pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS"
		val parser: DateTimeFormatter = DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.systemDefault())
		
		val parsedDateTime = ZonedDateTime.parse(dto.expireAt, parser) ?: null
		*/
		
		return Coupon(
			id = dto.id!!.toLong(),
			code = dto.code!!,
			description = dto.description!!,
			expireAt = parsedDateTime!!,
			percentage = dto.percentage!!
			
		)
	}
	
	fun entityListToDtoList(entities: Iterable<Coupon>): MutableList<CouponDto> {
		return entities.map { entityToDto(it) }.toMutableList()
	}
	
	fun dtoListToEntityList(dto: Iterable<CouponDto>): MutableList<Coupon> {
		return dto.map { dtoToEntity(it) }.toMutableList()
	}
	
	fun dtoListToPageDto(couponDtoList: List<CouponDto>,
						 offset: Int,
						 limit: Int): PageDto<CouponDto> {
		
		val dtoList: MutableList<CouponDto> =
			couponDtoList.stream()
				.skip(offset.toLong())
				.limit(limit.toLong())
				.toList().toMutableList()
		
		return PageDto(
			list = dtoList,
			rangeMin = offset,
			rangeMax = offset + dtoList.size - 1,
			totalSize = couponDtoList.size
		)
		
	}
	
}