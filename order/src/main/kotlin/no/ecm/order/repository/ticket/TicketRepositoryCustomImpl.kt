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
	
	override fun createTicket(price: Double, seat: String): Long {
		
		val entity = Ticket(null, price, seat)
		
		em.persist(entity)
		return entity.id!!
	}
	
	override fun updateSeat(paramId: Long, newSeat: String): Boolean {
		
		val entity = em.find(Ticket::class.java, paramId) ?: return false
		
		entity.seat = newSeat
		
		return true
	}

}