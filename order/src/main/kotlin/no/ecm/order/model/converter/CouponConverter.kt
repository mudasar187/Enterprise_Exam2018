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
	
	fun entityListToDtoList(entities: Iterable<Coupon>): MutableList<CouponDto> {
		return entities.map { entityToDto(it) }.toMutableList()
	}
	
}