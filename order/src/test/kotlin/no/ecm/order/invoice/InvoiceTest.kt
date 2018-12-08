package no.ecm.order.invoice

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.builder.RequestSpecBuilder
import io.restassured.http.ContentType
import no.ecm.order.TestBase
import no.ecm.utils.dto.order.CouponDto
import no.ecm.utils.dto.order.InvoiceDto
import no.ecm.utils.dto.order.TicketDto
import org.hamcrest.CoreMatchers
import org.junit.Test

class InvoiceTest : TestBase() {

    @Test
    fun stubResponsesTest() {
    
        val couponId = createDefaultCoupon()
        val nowPlayingId = "11"
        val seat = "A1"
    
        val json = getAMockNowPlayingResponse(nowPlayingId, seat)
        stubNowPlayingResponse(json)
    
        val nowPlayingSpec = RequestSpecBuilder().setBaseUri("http://localhost").setPort(8083).setBasePath("/").build()
        
        given().auth().basic("admin", "admin")
            .spec(nowPlayingSpec)
            .get("/now-playings/11")
            .then()
            .statusCode(200)
        
        given().auth().basic("admin", "admin")
            .spec(nowPlayingSpec)
            .body("""
				{
				    "seats": ["A1", "A2"]
				}
			""".trimIndent())
            .patch("/now-playings/11")
            .then()
            .statusCode(204)
    }

    @Test
    fun createInvoiceTest() {
        val couponId = createDefaultCoupon()
        val nowPlayingId = "11"
        val seat = "A1"
    
        val json = getAMockNowPlayingResponse(nowPlayingId, seat)
        stubNowPlayingResponse(json)

        given().auth().basic("admin", "admin")
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON)
            .body(createDefaultInvoiceDto(couponId, nowPlayingId, seat))
            .post(invoiceUrl)
            .then()
            .statusCode(201)
            .extract().response().jsonPath().prettyPrint()
    }
    
    @Test
    fun testFindInvoiceWithQueryParameter() {

        val couponId = createDefaultCoupon()
        val nowPlayingId = "11"
        val seat = "A1"

        val json = getAMockNowPlayingResponse(nowPlayingId, seat)
        stubNowPlayingResponse(json)

        // create invoice
        given().auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(createDefaultInvoiceDto(couponId, nowPlayingId, seat))
                .post(invoiceUrl)
                .then()
                .statusCode(201)

        // find by username and nowplayingId
        given().auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .queryParam("username", "jondoe")
                .queryParam("nowPlayingId", nowPlayingId)
                .get(invoiceUrl)
                .then()
                .statusCode(200)
                .body("data.totalSize", CoreMatchers.equalTo(1))

        // find by username and isPaid
        given().auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .queryParam("username", "jondoe")
                .queryParam("paid", false)
                .get(invoiceUrl)
                .then()
                .statusCode(200)
                .body("data.totalSize", CoreMatchers.equalTo(1))

        // find invoice by isPaid and nowPlayingId
        given().auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .queryParam("paid", false)
                .queryParam("nowPlayingId", nowPlayingId)
                .get(invoiceUrl)
                .then()
                .statusCode(200)
                .body("data.totalSize", CoreMatchers.equalTo(1))

        given().auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .queryParam("username", "jondoe")
                .queryParam("nowPlayingId", 1)
                .queryParam("paid", false)
                .get(invoiceUrl)
                .then()
                .statusCode(400)

    }
    
    // HELP METHODS

    private fun createDefaultInvoiceDto(couponId: Long, nowPlayingId: String, seat: String): InvoiceDto {
        return InvoiceDto(
                username = "jondoe",
                orderDate = "2018-12-24 20:04:15",
                couponCode = CouponDto(id = couponId.toString()),
                nowPlayingId = nowPlayingId,
                tickets = listOf(TicketDto(seat = seat, price = 20.0))
        )
    }

    private fun createDefaultCoupon() : Long{

        val code = "1234559221"
        val description = "DefaultDescription"
        val expireAt = "2019-01-01 01:00:00"
        val percentage = 10
        val dto = CouponDto(null, code, description, expireAt, percentage)

        return RestAssured.given().auth().basic("admin", "admin")
                .contentType(ContentType.JSON)
                .body(dto)
                .post(couponURL)
                .then()
                .statusCode(201)
                .header("Location", CoreMatchers.containsString("/coupons/"))
                .extract()
                .jsonPath().getLong("data.list[0].id")
    }
}