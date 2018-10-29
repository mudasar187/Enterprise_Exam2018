package no.ecm.order.repository

import no.ecm.order.model.entity.Coupon
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CouponRepository : CrudRepository<Coupon, Long> {

    fun findByCode(code: String): Coupon
}