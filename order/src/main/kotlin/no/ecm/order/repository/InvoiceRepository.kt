package no.ecm.order.repository

import no.ecm.order.model.entity.Invoice
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface InvoiceRepository : CrudRepository<Invoice, Long>{

    fun findAllByUsernameIgnoreCase(username: String): Iterable<Invoice>

    fun findAllByNowPlayingId(nowPlayingId: Long): Iterable<Invoice>

    fun findAllByPaid(paid: Boolean): Iterable<Invoice>

    fun findAllByUsernameIgnoreCaseAndPaid(username: String, paid: Boolean): Iterable<Invoice>

    fun findAllByUsernameIgnoreCaseAndNowPlayingId(username: String, nowPlayingId: Long): Iterable<Invoice>

    fun findAllByPaidAndNowPlayingId(paid: Boolean, nowPlayingId: Long): Iterable<Invoice>

}