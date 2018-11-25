package no.ecm.order.repository.ticket

import org.springframework.transaction.annotation.Transactional

@Transactional
interface TicketRepositoryCustom {
	
	fun createTicket(price: Double, seat: String): Long
	
	fun updateSeat(paramId: Long, newSeat: String): Boolean
}