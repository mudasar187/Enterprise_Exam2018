package no.ecm.order.repository.ticket

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager

@Repository
@Transactional
class TicketRepositoryCustomImpl : TicketRepositoryCustom {
	
	@Autowired
	private lateinit var em: EntityManager

}