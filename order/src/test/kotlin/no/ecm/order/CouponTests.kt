package no.ecm.order

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import no.ecm.utils.dto.order.CouponDto
import org.junit.Test

class CouponTests : TestBase() {
	
	@Test
	fun getAllCoupons() {
		
		given().get().then().statusCode(200).and().extract().body().jsonPath().prettyPrint()
		
	}
	
	@Test
	fun createCoupon() {
		val code = "12345678"
		val description = "Test Destiption"
		val expireAt = "2019-01-01 01:00:00.000000"
		
		val dto = CouponDto(null, code, description, expireAt)
		
		given().contentType(ContentType.JSON)
			.body(dto)
			.post()
			.then()
			.statusCode(201)
		
		given().get().then().statusCode(200).and().extract().body().jsonPath().prettyPrint()
	}
	
}