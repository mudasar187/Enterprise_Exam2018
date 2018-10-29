package no.ecm.order.repository

import no.ecm.order.model.entity.Ticket
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TicketRepository: CrudRepository<Ticket, Long> {

    fun findBySeatnumber(seatNumber: String): Ticket
}