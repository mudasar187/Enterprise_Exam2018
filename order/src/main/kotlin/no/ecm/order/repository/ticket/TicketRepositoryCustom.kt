package no.ecm.order.repository.ticket

import org.springframework.transaction.annotation.Transactional

@Transactional
interface TicketRepositoryCustom {
	
	fun createTicket(price: Double, seat: String, invoiceId: Long): Long
	fun updateTicket(id: Long, price: Double, seat: String): Boolean
	fun updateSeat(paramId: Long, newSeat: String): Boolean
	
}