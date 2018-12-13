package no.ecm.order.model.converter

import no.ecm.order.model.entity.Coupon
import no.ecm.utils.dto.order.CouponDto

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