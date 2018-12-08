package no.ecm.order.ticket

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import junit.framework.Assert.assertEquals
import no.ecm.order.TestBase
import no.ecm.utils.dto.order.TicketDto
import no.ecm.utils.response.TicketResponseDto
import org.hamcrest.CoreMatchers
import org.junit.Test

class TicketTest : TestBase() {
	
	@Test
	fun getAllTicketsTest() {
		assertEquals(getDbCount(ticketURL), 0)
	}
	
	@Test
	fun createTicketAndGetByIdTest() {
		
		val id = createDefaultTicket()
		
		val responseDto = given()
			.get("$ticketURL/$id")
			.then()
			.statusCode(200)
			.extract()
			.`as`(TicketResponseDto::class.java).data!!.list.first()
			
		checkDefaultTicketDto(responseDto, id)
		
	}
	
	@Test
	fun cachingTest() {
		
		val etag =
			given()
				.accept(ContentType.JSON)
				.get(ticketURL)
				.then()
				.statusCode(200)
				.header("ETag", CoreMatchers.notNullValue())
				.extract().header("ETag")
		
		given()
			.accept(ContentType.JSON)
			.header("If-None-Match", etag)
			.get(ticketURL)
			.then()
			.statusCode(304)
			.content(CoreMatchers.equalTo(""))
	}
	
	@Test
	fun cachingGetByIdTest() {
		val id = createDefaultTicket()
		
		val etag = given()
			.accept(ContentType.JSON)
			.get("$ticketURL/$id")
			.then()
			.statusCode(200)
			.header("ETag", CoreMatchers.notNullValue())
			.extract().header("ETag")
		
		given().accept(ContentType.JSON)
			.header("If-None-Match", etag)
			.get("$ticketURL/$id")
			.then()
			.statusCode(304)
			.content(CoreMatchers.equalTo(""))
	}
	
	@Test
	fun createTicketWithInvalidSeatTest() {
		
		val list: MutableList<TicketDto> = mutableListOf()
		
		list.add(TicketDto(null, 200.1, "AA"))
		list.add(TicketDto(null, 200.1, "*5"))
		list.add(TicketDto(null, 200.1, "abc123"))
		list.add(TicketDto(null, 200.1, "A!"))
		list.add(TicketDto(null, 200.1, "A123"))
		list.add(TicketDto(null, 200.1, "AA12"))
		
		list.forEach {
			given().contentType(ContentType.JSON).body(it).post(ticketURL).then().statusCode(400)
		}
	}
	
	@Test
	fun createTicketWitInvalidDataTest() {
		
		val price = 123.4
		val seat = "A1"
		val invoiceId = "1"
		
		//create ticket with given id
		given()
			.contentType(ContentType.JSON)
			.body(TicketDto("123", price, seat, invoiceId))
			.post(ticketURL)
			.then()
			.statusCode(400)
		
		createInvalidTicket(null, seat, invoiceId)
		assertEquals(getDbCount(ticketURL), 0)
		
		createInvalidTicket(price, null, invoiceId)
		assertEquals(getDbCount(ticketURL), 0)
		
		createInvalidTicket(price, seat, null)
		assertEquals(getDbCount(ticketURL), 0)
	}
	
	@Test
	fun deleteTicketTest() {
		
		val id = createDefaultTicket()
		
		given()
			.delete("$ticketURL/$id")
			.then()
			.statusCode(200)
		
		given()
			.get("$ticketURL/$id")
			.then()
			.statusCode(404)
	}
	
	@Test
	fun deleteInvalidTicketTest() {
		given()
			.delete("$ticketURL/12345")
			.then()
			.statusCode(404)
	}
	
	@Test
	fun deleteNonExistingTicketTest() {
		
		createDefaultTicket()
		
		given()
			.delete("tickets/1234567")
			.then()
			.statusCode(404)
	}
	
	@Test
	fun updateSeatNumber() {
		
		val updatedSeat = "C3"
		
		val id = createDefaultTicket()
		val etag = getEtagFromId(id.toString())
		
		given().contentType("application/merge-patch+json")
			.header("If-Match", etag)
			.body("{\"seat\": \"$updatedSeat\"}")
			.patch("$ticketURL/$id")
			.then()
			.statusCode(204)
		
		given()
			.get("$ticketURL/$id")
			.then()
			.statusCode(200)
			.body("data.list[0].seat", CoreMatchers.equalTo(updatedSeat))
	}
	
	@Test
	fun updateSeatNumberWithInvalidInformation() {
		
		val updatedSeat = "C3"
		
		val id = createDefaultTicket()
		val etag = getEtagFromId(id.toString())
		
		//Invalid JSON Merge Patch syntax
		given().contentType("application/merge-patch+json")
			.header("If-Match", etag)
			.body("{seat: \"$updatedSeat\"}")
			.patch("$ticketURL/$id")
			.then()
			.statusCode(409)
		
		//Update with id in JSON Merge Patch body
		given().contentType("application/merge-patch+json")
			.header("If-Match", etag)
			.body("{\"id\": $id,\"seat\": \"$updatedSeat\"}")
			.patch("$ticketURL/$id")
			.then()
			.statusCode(400)
		
		//Update with invalid update value
		given().contentType("application/merge-patch+json")
			.header("If-Match", etag)
			.body("{\"abc\": 123}")
			.patch("$ticketURL/$id")
			.then()
			.statusCode(400)
		
		//Update non existing ticket
		given().contentType("application/merge-patch+json")
			.header("If-Match", etag)
			.body("{\"abc\": 123}")
			.patch("$ticketURL/666")
			.then()
			.statusCode(404)
	}
	
	@Test
	fun updateTicketTest() {
		
		val id = createDefaultTicket()
		val etag = getEtagFromId(id.toString())
		
		val updatedPrice = 100.5
		val updatedSeat = "C12"
		val updatedInvoiceId = "2"
		
		given()
			.contentType(ContentType.JSON)
			.pathParam("id", id)
			.header("If-Match", etag)
			.body(TicketDto(id.toString(), updatedPrice, updatedSeat, updatedInvoiceId))
			.put("$ticketURL/{id}")
			.then()
			.statusCode(204)
		
		given()
			.get("$ticketURL/$id")
			.then()
			.statusCode(200)
			.body("data.list[0].id", CoreMatchers.equalTo(id.toString()))
			//.body("data.list[0].price", CoreMatchers.equalTo(updatedPrice.toString()))
			.body("data.list[0].seat", CoreMatchers.equalTo(updatedSeat))
	}
	
	@Test
	fun updateTicketWithNonMatchingIdInPathAndBody() {
		
		val id = createDefaultTicket()
		val etag = getEtagFromId(id.toString())
		
		val updatedPrice = 100.5
		val updatedSeat = "C12"
		
		given()
			.contentType(ContentType.JSON)
			.pathParam("id", 8765)
			.header("If-Match", etag)
			.body(TicketDto(id.toString(), updatedPrice, updatedSeat))
			.put("$ticketURL/{id}")
			.then()
			.statusCode(404)
	}
	
	@Test
	fun updateTicketWithInvalidData() {
		
		val id = createDefaultTicket()
		val etag = getEtagFromId(id.toString())
		
		val updatedPrice = 100.5
		val updatedSeat = "C1"
		val updatedInvoiceId = "2"
		
		given().auth().basic("admin", "admin")
			.contentType(ContentType.JSON)
			.pathParam("id", id)
			.header("If-Match", etag)
			.body(TicketDto(null, updatedPrice, updatedSeat, updatedInvoiceId))
			.put("$ticketURL/{id}")
			.then()
			.statusCode(400)
		
		given().auth().basic("admin", "admin")
			.contentType(ContentType.JSON)
			.pathParam("id", id)
			.header("If-Match", etag)
			.body(TicketDto(id.toString(), null, updatedSeat, updatedInvoiceId))
			.put("$ticketURL/{id}")
			.then()
			.statusCode(400)
		
		given().auth().basic("admin", "admin")
			.contentType(ContentType.JSON)
			.pathParam("id", id)
			.header("If-Match", etag)
			.body(TicketDto(id.toString(), updatedPrice, null, updatedInvoiceId))
			.put("$ticketURL/{id}")
			.then()
			.statusCode(400)
		
		given().auth().basic("admin", "admin")
			.contentType(ContentType.JSON)
			.pathParam("id", id)
			.header("If-Match", etag)
			.body(TicketDto(id.toString(), updatedPrice, updatedSeat, null))
			.put("$ticketURL/{id}")
			.then()
			.statusCode(400)
		
		given().auth().basic("admin", "admin")
			.contentType(ContentType.JSON)
			.pathParam("id", 666)
			.header("If-Match", etag)
			.body(TicketDto(id.toString(), updatedPrice, updatedSeat, updatedInvoiceId))
			.put("$ticketURL/{id}")
			.then()
			.statusCode(404)
		
		given().auth().basic("admin", "admin")
			.contentType(ContentType.JSON)
			.pathParam("id", id)
			.header("If-Match", etag)
			.body(TicketDto("666", updatedPrice, updatedSeat, updatedInvoiceId))
			.put("$ticketURL/{id}")
			.then()
			.statusCode(409)
	}
	
	private fun createDefaultTicket(): Long {
		return given().auth().basic("admin", "admin")
			.contentType(ContentType.JSON)
			.body(TicketDto(null, 200.5, "A1", "1"))
			.post(ticketURL)
			.then()
			.statusCode(201)
			.extract()
			.jsonPath().getLong("data.list[0].id")
	}
	
	private fun createInvalidTicket(price: Double?, seat: String?, invoiceId: String?) {
		given().auth().basic("admin", "admin")
			.contentType(ContentType.JSON)
			.body(TicketDto(null, price, seat, invoiceId))
			.post(ticketURL)
			.then()
			.statusCode(400)
		
	}
	
	private fun checkDefaultTicketDto(dto: TicketDto, id: Long) {
		assertEquals(dto.id, id.toString())
		assertEquals(dto.seat, "A1")
		assertEquals(dto.price, 200.5)
	}
	
	fun getEtagFromId(id: String): String {
		return given().auth().basic("admin", "admin").accept(ContentType.JSON)
			.get("$ticketURL/$id")
			.then()
			.extract().header("ETag")
	}
}