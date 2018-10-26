package no.ecm.order.repository

import no.ecm.order.model.entity.Coupon
import no.ecm.order.model.entity.Order
import no.ecm.order.model.entity.Ticket
import org.springframework.stereotype.Component
import java.time.ZonedDateTime
import javax.annotation.PostConstruct

@Component
class DefaultData(
        private var orderRepository: OrderRepository,
        private var ticketRepository: TicketRepository,
        private var couponRepository: CouponRepository
) {

    @PostConstruct
    fun createData(){

        val coupon = Coupon(code = "summer2019", description = "For the summer of 2019", expireAt = ZonedDateTime.now())
        couponRepository.save(coupon)

//        val order = Order(
//                tickets = mutableSetOf(Ticket(
//                        price = 200,
//                        seatnumber = "A1"
//                )),
//                        username = "me",
//                orderDate = ZonedDateTime.now(),
//                coupon = coupon,
//                nowPlayingId = 67898765)
//        orderRepository.save(order)
//
//        val ticket = Ticket(price = 200, seatnumber = "a1")
//        ticketRepository.save(ticket)

//
//
//        val orderRes = orderRepository.findByNowPlayingId(67898765)
//
//        val ticketRes = ticketRepository.findBySeatnumber(seatNumber = "a1")
//        ticketRes.order = orderRes
//        ticketRepository.save(ticketRes)
//
//
//
//        print(orderRes.username)

    }
}