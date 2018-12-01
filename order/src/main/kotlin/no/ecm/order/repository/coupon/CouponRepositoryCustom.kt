package no.ecm.order.repository.coupon

import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Transactional
interface CouponRepositoryCustom {
	
	fun createCoupon(code: String, description: String, expireAt: ZonedDateTime): Long
	
	fun updateCoupon(id: Long, code: String, description: String, expireAt: ZonedDateTime): Boolean
	
	fun updateDescription(id: Long, description: String): Boolean
	
}
