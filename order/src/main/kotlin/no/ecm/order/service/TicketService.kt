package no.ecm.order.service

import no.ecm.order.repository.ticket.TicketRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TicketService {

	@Autowired
	private lateinit var repository: TicketRepository

}