package no.ecm.order.repository

import no.ecm.order.model.entity.Order
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository : CrudRepository<Order, Long>{

    fun findAllByUsername(username: String): Iterable<Order>

    fun findByNowPlayingId(nowPlayingId: Long): Order
}