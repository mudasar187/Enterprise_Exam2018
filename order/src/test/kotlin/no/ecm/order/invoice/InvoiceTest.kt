package no.ecm.order.invoice

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import junit.framework.Assert.assertTrue
import no.ecm.order.TestBase
import no.ecm.utils.dto.order.CouponDto
import no.ecm.utils.dto.order.InvoiceDto
import no.ecm.utils.dto.order.TicketDto
import org.hamcrest.CoreMatchers
import org.junit.Test

class InvoiceTest : TestBase() {

    @Test
    fun testok() {
        assertTrue(true)
    }

    @Test
    fun testCreateInvoice() {
        val couponId = createDefaultCoupon()
        val nowPLayingId = "11"
        val seat = "A1"

        val json = getAMockNowPlayingResponse(nowPLayingId, seat)
        //stubJsonResponse(json)

        given()
            .body(createDefaultInvoiceDto(couponId, nowPLayingId, seat))
            .post(nowPlayingURL)
            .then()
            .statusCode(201)
                //.body("data.list[0].id", CoreMatchers.equalTo(newNowPlayingId))

    }

    private fun createDefaultInvoiceDto(couponId: Long, nowPLayingId: String, seat: String): InvoiceDto {
        return InvoiceDto(
                username = "jondoe",
                orderDate = "2018-12-24 20:04:15",
                couponCode = CouponDto(id = couponId.toString()),
                nowPlayingId = nowPLayingId,
                tickets = listOf(TicketDto(seat = seat))
        )
    }

    private fun createDefaultCoupon() : Long{

        val code = "1234559221"
        val description = "DefaultDescription"
        val expireAt = "2019-01-01 01:00:00"
        val percentage = 10
        val dto = CouponDto(null, code, description, expireAt, percentage)

        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(dto)
                .post(invoiceUrl)
                .then()
                .statusCode(201)
                .header("Location", CoreMatchers.containsString("/coupons/"))
                .extract()
                .jsonPath().getLong("data.list[0].id")
    }
}