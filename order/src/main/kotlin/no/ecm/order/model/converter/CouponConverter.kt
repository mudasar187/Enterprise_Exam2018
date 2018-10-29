package no.ecm.order.model.converter

import no.ecm.order.model.entity.Coupon
import no.ecm.utils.dto.order.CouponDto

object CouponConverter {
	
	fun entityToDto(entity: Coupon): CouponDto {
		
		return CouponDto(
			id = entity.id.toString(),
			code = entity.code,
			description = entity.description,
			expireAt = entity.expireAt
		)
	}
	
	fun dtoToEntity(dto: CouponDto) : Coupon {
		return Coupon(
			id = dto.id!!.toLong(),
			code = dto.code!!,
			description = dto.description!!,
			expireAt = dto.expireAt!!
		)
	}
	
	fun entityListToDtoList(entities: Iterable<Coupon>): List<CouponDto> {
		return entities.map { entityToDto(it) }
	}
	
	fun dtoListToEntityList(dto: Iterable<CouponDto>): List<Coupon> {
		return dto.map { dtoToEntity(it) }
	}
	
}