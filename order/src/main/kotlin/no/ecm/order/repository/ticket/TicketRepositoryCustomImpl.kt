package no.ecm.order.repository.ticket

import no.ecm.order.model.entity.Ticket
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager

@Repository
@Transactional
class TicketRepositoryCustomImpl : TicketRepositoryCustom {
	
	@Autowired
	private lateinit var em: EntityManager
	
	override fun createTicket(price: Double, seat: String, invoiceId: Long): Long {
		
		val entity = Ticket(null, price, seat, invoiceId)
		
		em.persist(entity)
		return entity.id!!
	}
	
	override fun updateTicket(id: Long, price: Double, seat: String): Boolean {
		
		val entity = em.find(Ticket::class.java, id) ?: return false
		
		entity.price = price
		entity.seat = seat
		
		return true
		
	}
	
	override fun updateSeat(paramId: Long, newSeat: String): Boolean {
		
		val entity = em.find(Ticket::class.java, paramId) ?: return false
		
		entity.seat = newSeat
		
		return true
	}

}