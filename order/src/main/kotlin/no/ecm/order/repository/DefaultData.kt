package no.ecm.order.repository

import no.ecm.order.model.entity.Coupon
import no.ecm.order.model.entity.Invoice
import no.ecm.order.model.entity.Ticket
import no.ecm.order.repository.coupon.CouponRepository
import no.ecm.order.repository.ticket.TicketRepository
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

        val coupon1 = Coupon(code = "halloween2018", description = "Halloween cupon 20% discount!", expireAt = ZonedDateTime.now())
        val coupon2 = Coupon(code = "lastexam2018", description = "Cool discount for students with 50% !", expireAt = ZonedDateTime.now())
        val coupon3 = Coupon(code = "christmas2018", description = "Christmas discount gives you 80% discount on 4 tickets!", expireAt = ZonedDateTime.now())

        couponRepository.saveAll(mutableListOf(coupon1, coupon2, coupon3))
        
        val invoice1 = Invoice(
                username = "jondoe",
                orderDate = ZonedDateTime.now(),
                coupon = coupon1,
                nowPlayingId = 67898765)

        val invoice2 = Invoice(
                username = "foobar",
                orderDate = ZonedDateTime.now(),
                coupon = coupon2,
                nowPlayingId = 47685675)

        val invoice3 = Invoice(
                username = "farcar",
                orderDate = ZonedDateTime.now(),
                coupon = coupon3,
                nowPlayingId = 98765431)

        invoiceRepository.saveAll(mutableListOf(invoice1, invoice2, invoice3))



        val ticket1 = Ticket(price = 200.00, seat = "A1")
        val ticket2 = Ticket(price = 200.00, seat = "A2")
        val ticket3 = Ticket(price = 300.00, seat = "B6")
        val ticket4 = Ticket(price = 100.00, seat = "C9")
        ticketRepository.saveAll(mutableListOf(ticket1, ticket2, ticket3, ticket4))


        val invoiceRes1 = invoiceRepository.findAllByNowPlayingId(67898765).first()
        invoiceRes1.tickets = mutableSetOf(ticket1, ticket2)
        invoiceRepository.save(invoiceRes1)

        val invoiceRes2 = invoiceRepository.findAllByNowPlayingId(47685675).first()
        invoiceRes2.tickets = mutableSetOf(ticket3)
        invoiceRepository.save(invoiceRes2)

        val invoiceRes3 = invoiceRepository.findAllByNowPlayingId(98765431).first()
        invoiceRes3.tickets = mutableSetOf(ticket4)
        invoiceRepository.save(invoiceRes3)






    }
}