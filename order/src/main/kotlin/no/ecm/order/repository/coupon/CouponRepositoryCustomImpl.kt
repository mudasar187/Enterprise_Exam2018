package no.ecm.order.repository.coupon

import no.ecm.order.model.entity.Coupon
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime
import javax.persistence.EntityManager

@Repository
@Transactional
class CouponRepositoryCustomImpl : CouponRepositoryCustom {
	@Autowired
	private lateinit var em: EntityManager
	
	override fun createCoupon(code: String, description: String, expireAt: ZonedDateTime): Long {
		
		val entity = Coupon(null, code, description, expireAt)
		
		em.persist(entity)
		return entity.id!!
	}
	
}