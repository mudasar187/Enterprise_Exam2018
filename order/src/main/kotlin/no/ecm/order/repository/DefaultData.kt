package no.ecm.order.repository

import no.ecm.order.model.entity.Coupon
import no.ecm.order.model.entity.Invoice
import no.ecm.order.model.entity.Ticket
import org.springframework.stereotype.Component
import java.time.ZonedDateTime
import javax.annotation.PostConstruct

@Component
class DefaultData(
        private var invoiceRepository: InvoiceRepository,
        private var ticketRepository: TicketRepository,
        private var couponRepository: CouponRepository
) {

    @PostConstruct
    fun createData(){

        val coupon = Coupon(code = "summer2019", description = "For the summer of 2019", expireAt = ZonedDateTime.now())
        couponRepository.save(coupon)

        val couponRes = couponRepository.findByCode(code = "summer2019")
        println(couponRes.description)

        val invoice = Invoice(
                username = "me",
                orderDate = ZonedDateTime.now(),
                coupon = coupon,
                nowPlayingId = 67898765)
        invoiceRepository.save(invoice)

        val ticket = Ticket(price = 200, seatnumber = "a1")
        ticketRepository.save(ticket)

        val invoiceRes = invoiceRepository.findByNowPlayingId(67898765)
        invoiceRes.tickets = mutableSetOf(ticket)
        invoiceRepository.save(invoiceRes)

        val ticketRes = ticketRepository.findBySeatnumber(seatNumber = "a1")
        println(ticketRes.price)
    }
}