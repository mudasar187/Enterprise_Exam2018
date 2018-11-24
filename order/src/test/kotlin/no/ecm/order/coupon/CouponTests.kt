package no.ecm.order.coupon

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import no.ecm.utils.dto.order.CouponDto
import no.ecm.utils.response.CouponResponseDto
import org.hamcrest.CoreMatchers
import org.junit.Test
import org.springframework.http.HttpStatus

class CouponTests : TestBase() {
	
	//TODO Fix all sizes stuff after DELETE is done
	
	@Test
	fun getAllCouponsTest() {
		val size = given().accept(ContentType.JSON).get()
			.then()
			.statusCode(200)
			.extract()
			.`as`(CouponResponseDto::class.java).data!!.list.size
		
		assertResultSize(size)
	}
	
	@Test
	fun createCouponAndGetByIdTest() {
		val size = given().accept(ContentType.JSON).get()
			.then()
			.statusCode(200)
			.extract()
			.`as`(CouponResponseDto::class.java).data!!.list.size
		
		val code = "1234567899"
		val description = "DefaultDescription"
		val expireAt = "2019-01-01 01:00:00.000000"
		
		val id = createCoupon(code, description, expireAt)
		
		assertResultSize(size + 1)
		
		given()
			.get("/$id")
			.then()
			.statusCode(200)
			.body("data.list[0].id", CoreMatchers.equalTo(id.toString()))
			.body("data.list[0].code", CoreMatchers.equalTo(code))
			.body("data.list[0].description", CoreMatchers.equalTo(description))
	}
	
	@Test
	fun getWithInvalidIdTest() {
		
		val code = "1234567899"
		val description = "DefaultDescription"
		val expireAt = "2019-01-01 01:00:00.000000"
		
		createCoupon(code, description, expireAt)
		
		given()
			.get("/x")
			.then()
			.statusCode(404)
			.body("message", CoreMatchers.notNullValue())
			.body("status", CoreMatchers.equalTo("ERROR"))
			.body("page", CoreMatchers.nullValue())
	}
	
	@Test
	fun getByCodeTest() {
		
		val code = "1234567899"
		val description = "DefaultDescription"
		val expireAt = "2019-01-01 01:00:00.000000"
		
		val id = createCoupon(code, description, expireAt)
		
		given()
			.param("code", code)
			.get()
			.then()
			.statusCode(200)
			.body("data.list[0].id", CoreMatchers.equalTo(id.toString()))
			.body("data.list[0].code", CoreMatchers.equalTo(code))
			.body("data.list[0].description", CoreMatchers.equalTo(description))
	}
	
	@Test
	fun createCouponWithGivenIdTest() {
		
		//TODO Expand to more test cases
		
		val code = "1234567899"
		val description = "DefaultDescription"
		val expireAt = "2019-01-01 01:00:00.000000"
		
		val dto = CouponDto("1234", code, description, expireAt)
		
		given().contentType(ContentType.JSON)
			.body(dto)
			.post()
			.then()
			.statusCode(404)
			.body("message", CoreMatchers.notNullValue())
			.body("status", CoreMatchers.equalTo("ERROR"))
			.body("page", CoreMatchers.nullValue())
	}
	
	@Test
	fun cachingTest() {
		
		val etag = RestAssured.given().accept(ContentType.JSON)
			.get()
			.then()
			.statusCode(200)
			.header("ETag", CoreMatchers.notNullValue())
			.extract().header("ETag")
		
		given().accept(ContentType.JSON)
			.header("If-None-Match", etag)
			.get()
			.then()
			.statusCode(304)
			.content(CoreMatchers.equalTo(""))
	}
	
}