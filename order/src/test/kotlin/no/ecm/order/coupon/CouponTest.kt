package no.ecm.order.coupon

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import no.ecm.utils.dto.order.CouponDto
import no.ecm.utils.response.CouponResponseDto
import org.hamcrest.CoreMatchers
import org.junit.Test

class CouponTest : CouponTestBase() {
	
	//TODO Fix all sizes stuff after DELETE is done
	/*
	@Test
	fun testCleanDb() {
		assertResultSize(0)
	}
	*/
	
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
		
		val code = "1234554321"
		val description = "DefaultDescription"
		val expireAt = "2019-01-01 01:00:00"
		
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
	fun createWithInvalidDataTest() {
		val size = given().accept(ContentType.JSON).get()
			.then()
			.statusCode(200)
			.extract()
			.`as`(CouponResponseDto::class.java).data!!.list.size
		val code = "1234554321"
		val description = "DefaultDescription"
		val expireAt = "2019-01-01 01:00:00"
		
		createInvalidCoupon("", description, expireAt, 400)
		assertResultSize(size)
		createInvalidCoupon(code, "", expireAt, 400)
		assertResultSize(size)
		createInvalidCoupon(code, description, "", 400)
		assertResultSize(size)
		
	}
	
	@Test
	fun getWithInvalidIdTest() {
		
		val code = "1234567899"
		val description = "DefaultDescription"
		val expireAt = "2019-01-01 01:00:00"
		
		createCoupon(code, description, expireAt)
		
		given()
			.get("/x")
			.then()
			.statusCode(400)
			.body("message", CoreMatchers.notNullValue())
			.body("status", CoreMatchers.equalTo("ERROR"))
			.body("page", CoreMatchers.nullValue())
	}
	
	@Test
	fun getByCodeTest() {
		
		val code = "1209348756"
		val description = "DefaultDescription"
		val expireAt = "2019-01-01 01:00:00"
		
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
	fun updateCouponTest() {
		
		val code = "0987654321"
		val description = "DefaultDescription"
		val expireAt = "2019-01-01 01:00:00"
		
		val id = createCoupon(code, description, expireAt)
		
		val updatedCode = "0987654321"
		val updatedDescription = "UpdatedDescription"
		val updatedExpireAt = "2018-12-24 20:30:30"
		
		given()
			.contentType(ContentType.JSON)
			.pathParam("id", id)
			.body(CouponDto(id.toString(), updatedCode, updatedDescription, updatedExpireAt))
			.put("/{id}")
			.then()
			.statusCode(204)
		
		given()
			.get("/$id")
			.then()
			.statusCode(200)
			.body("data.list[0].id", CoreMatchers.equalTo(id.toString()))
			.body("data.list[0].code", CoreMatchers.equalTo(updatedCode))
			.body("data.list[0].description", CoreMatchers.equalTo(updatedDescription))
	}
	
	@Test
	fun updateNonExistingCoupon() {
		val id = 22222
		val updatedCode = "0987654321"
		val updatedDescription = "UpdatedDescription"
		val updatedExpireAt = "2018-12-24 20:30:30"
		
		given()
			.contentType(ContentType.JSON)
			.pathParam("id", id)
			.body(CouponDto(id.toString(), updatedCode, updatedDescription, updatedExpireAt))
			.put("/{id}")
			.then()
			.statusCode(404)
	}
	
	@Test
	fun updateCouponWithNonMatchingIdInPathAndBody() {
		
		val code = "0987654321"
		val description = "DefaultDescription"
		val expireAt = "2019-01-01 01:00:00"
		
		val id = createCoupon(code, description, expireAt)
		
		val updatedCode = "0987654321"
		val updatedDescription = "UpdatedDescription"
		val updatedExpireAt = "2018-12-24 20:30:30"
		
		given()
			.contentType(ContentType.JSON)
			.pathParam("id", 12345)
			.body(CouponDto(id.toString(), updatedCode, updatedDescription, updatedExpireAt))
			.put("/{id}")
			.then()
			.statusCode(409)
	}
	
	@Test
	fun createCouponWithGivenIdTest() {
		
		//TODO Expand to more test cases
		
		val code = "45678123"
		val description = "DefaultDescription"
		val expireAt = "2019-01-01 01:00:00"
		
		val dto = CouponDto("1234", code, description, expireAt)
		
		given().contentType(ContentType.JSON)
			.body(dto)
			.post()
			.then()
			.statusCode(400)
			.body("message", CoreMatchers.notNullValue())
			.body("status", CoreMatchers.equalTo("ERROR"))
			.body("page", CoreMatchers.nullValue())
	}
	
	@Test
	fun deleteUnusedCouponTest() {
		
		// This test covers deletion of a coupon that is unused.
		// Which means that no orders have been made using this coupon
		// This will be tested in another test
		
		val code = "123412345"
		val description = "DefaultDescription"
		val expireAt = "2019-01-01 01:00:00"
		
		val id = createCoupon(code, description, expireAt)
		
		given()
			.delete("/$id")
			.then()
			.statusCode(200)
		
		given()
			.get("/$id")
			.then()
			.statusCode(404)
		
	}
	
	@Test
	fun cachingGetAllTest() {
		
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
	
	@Test
	fun cachingGetByIdTest() {
		
		val code = "123412345"
		val description = "DefaultDescription"
		val expireAt = "2019-01-01 01:00:00"
		
		val id = createCoupon(code, description, expireAt)
		
		val etag = RestAssured.given().accept(ContentType.JSON)
			.get("/$id")
			.then()
			.statusCode(200)
			.header("ETag", CoreMatchers.notNullValue())
			.extract().header("ETag")
		
		given().accept(ContentType.JSON)
			.header("If-None-Match", etag)
			.get("/$id")
			.then()
			.statusCode(304)
			.content(CoreMatchers.equalTo(""))
	}
	
	@Test
	fun updateDescriptionTest() {
		
		val code = "6743903212"
		val description = "DefaultDescription"
		val expireAt = "2019-01-01 01:00:00"
		val updatedDescription = "UpdatedDescription"
		
		val id = createCoupon(code, description, expireAt)
		
		given().contentType("application/merge-patch+json")
			.body("{\"description\": \"$updatedDescription\"}")
			.patch("/$id")
			.then()
			.statusCode(204)
		
		given()
			.get("/$id")
			.then()
			.statusCode(200)
			.body("data.list[0].description", CoreMatchers.equalTo(updatedDescription))
	}
	
	@Test
	fun updateDescriptionNumberWithInvalidInformation() {
		
		val code = "98235610362"
		val description = "DefaultDescription"
		val expireAt = "2019-01-01 01:00:00"
		val updatedDescription = "UpdatedDescription"
		
		val id = createCoupon(code, description, expireAt)
		
		//Invalid JSON Merge Patch syntax
		given().contentType("application/merge-patch+json")
			.body("{seat: \"$updatedDescription\"}")
			.patch("/$id")
			.then()
			.statusCode(409)
		
		//Update with id in JSON Merge Patch body
		given().contentType("application/merge-patch+json")
			.body("{\"id\": \"$id\",\"seat\": \"$updatedDescription\"}")
			.patch("/$id")
			.then()
			.statusCode(400)
		
		//Update with invalid update value
		given().contentType("application/merge-patch+json")
			.body("{\"abc\": 123}")
			.patch("/$id")
			.then()
			.statusCode(400)
		
		//Update non existing ticket
		given().contentType("application/merge-patch+json")
			.body("{\"abc\": 123}")
			.patch("/7777")
			.then()
			.statusCode(404)
	}
	
}