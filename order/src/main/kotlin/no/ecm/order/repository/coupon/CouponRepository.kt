package no.ecm.order.repository.coupon

import no.ecm.order.model.entity.Coupon
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CouponRepository : CrudRepository<Coupon, Long>, CouponRepositoryCustom {

    fun findByCode(code: String): Coupon
}