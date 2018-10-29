package no.ecm.order.repository

import no.ecm.order.model.entity.Invoice
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface InvoiceRepository : CrudRepository<Invoice, Long>{

    fun findAllByUsername(username: String): Iterable<Invoice>

    fun findByNowPlayingId(nowPlayingId: Long): Invoice
}