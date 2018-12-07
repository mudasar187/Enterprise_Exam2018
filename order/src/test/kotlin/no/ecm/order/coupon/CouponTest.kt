package no.ecm.order.coupon

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import junit.framework.Assert.assertEquals
import no.ecm.order.TestBase
import no.ecm.utils.converter.ConvertionHandler.Companion.convertTimeStampToZonedTimeDate
import no.ecm.utils.dto.order.CouponDto
import no.ecm.utils.response.CouponResponseDto
import org.hamcrest.CoreMatchers
import org.junit.Test

class CouponTest : TestBase() {
	
	@Test
	fun testCleanDb() {
		assertEquals(getDbCount(couponURL), 0)
	}
	
	@Test
	fun createCouponAndGetByIdTest() {
		
		val id = createDefaultCoupon()
		
		val result = given()
			.get("$couponURL/$id")
			.then()
			.statusCode(200)
			.extract()
			.`as`(CouponResponseDto::class.java).data!!.list.first()
		
		checkDefaultCouponDto(result, id)
	}
	
	@Test
	fun createWithInvalidDataTest() {
		val code = "1234554321"
		val description = "DefaultDescription"
		val expireAt = "2019-01-01 01:00:00"
		val percentage = 10
		
		createInvalidCoupon(null, description, expireAt, percentage, 400)
		assertEquals(getDbCount(couponURL), 0)

		createInvalidCoupon(code, null, expireAt, percentage, 400)
		assertEquals(getDbCount(couponURL), 0)
		
		createInvalidCoupon(code, description, null, percentage, 400)
		assertEquals(getDbCount(couponURL), 0)
		
		createInvalidCoupon(code, description, expireAt, null, 400)
		assertEquals(getDbCount(couponURL), 0)
	}
	
	@Test
	fun updateWithInvalidDataTest() {
		
		val updatedCode = "0987654321"
		val updatedDescription = "UpdatedDescription"
		val updatedExpireAt = "2018-12-24 20:30:30"
		val updatedPercentage = 20
		
		val id = createDefaultCoupon()
		val etag = getEtagFromId(id.toString())
		
		given()
			.contentType(ContentType.JSON)
			.pathParam("id", id)
			.header("If-Match", etag)
			.body(CouponDto(null, updatedCode, updatedDescription, updatedExpireAt, updatedPercentage))
			.put("$couponURL/{id}")
			.then()
			.statusCode(400)
		
		updateInvalidCoupon(id, null, updatedDescription, updatedExpireAt, updatedPercentage, etag)
		updateInvalidCoupon(id, updatedCode, null, updatedExpireAt, updatedPercentage, etag)
		updateInvalidCoupon(id, updatedCode, updatedDescription, null, updatedPercentage, etag)
		updateInvalidCoupon(id, updatedCode, updatedDescription, updatedExpireAt, null, etag)
	}
	
	@Test
	fun getWithInvalidIdTest() {
		
		createDefaultCoupon()
		
		given()
			.get("$couponURL/1000")
			.then()
			.statusCode(404)
			.body("message", CoreMatchers.notNullValue())
			.body("code", CoreMatchers.equalTo(404))
			.body("data", CoreMatchers.nullValue())
			.body("status", CoreMatchers.equalTo("ERROR"))
		
		given()
			.get("$couponURL/x")
			.then()
			.statusCode(400)
			.body("message", CoreMatchers.notNullValue())
			.body("code", CoreMatchers.equalTo(400))
			.body("data", CoreMatchers.nullValue())
			.body("status", CoreMatchers.equalTo("ERROR"))
	
	}
	
	@Test
	fun getByCodeTest() {
		
		val code = "1234567899"
		
		val id = createDefaultCoupon()
		
		val resultDto = given()
			.param("code", code)
			.get(couponURL)
			.then()
			.statusCode(200)
			.extract().`as`(CouponResponseDto::class.java).data!!.list.first()
		
		checkDefaultCouponDto(resultDto, id)
	}
	
	@Test
	fun updateCouponTest() {
		
		val id = createDefaultCoupon()
		
		val updatedCode = "0987654321"
		val updatedDescription = "UpdatedDescription"
		val updatedExpireAt = "2018-12-24 20:30:30"
		val updatedPercentage = 20
		
		val etag = getEtagFromId(id.toString())
		
		given()
			.contentType(ContentType.JSON)
			.pathParam("id", id)
			.header("If-Match", etag)
			.body(CouponDto(id.toString(), updatedCode, updatedDescription, updatedExpireAt, updatedPercentage))
			.put("$couponURL/{id}")
			.then()
			.statusCode(204)
		
		given()
			.get("$couponURL/$id")
			.then()
			.statusCode(200)
			.body("data.list[0].id", CoreMatchers.equalTo(id.toString()))
			.body("data.list[0].code", CoreMatchers.equalTo(updatedCode))
			.body("data.list[0].description", CoreMatchers.equalTo(updatedDescription))
	}
	
	@Test
	fun updateCouponWithNonMatchingIdInPathAndBody() {
		
		val id = createDefaultCoupon()
		
		val updatedCode = "0987654321"
		val updatedDescription = "UpdatedDescription"
		val updatedExpireAt = "2018-12-24 20:30:30"
		val updatedPercentage = 20
		
		val etag = getEtagFromId(id.toString())
		
		given()
			.contentType(ContentType.JSON)
			.pathParam("id", 12345)
			.header("If-Match", etag)
			.body(CouponDto(id.toString(), updatedCode, updatedDescription, updatedExpireAt, updatedPercentage))
			.put("/{id}")
			.then()
			.statusCode(404)
	}
	
	@Test
	fun createCouponWithGivenIdTest() {
		
		val code = "45678123"
		val description = "DefaultDescription"
		val expireAt = "2019-01-01 01:00:00"
		val percentage = 10
		
		val dto = CouponDto("1234", code, description, expireAt, percentage)
		
		given().contentType(ContentType.JSON)
			.body(dto)
			.post(couponURL)
			.then()
			.statusCode(400)
			.body("message", CoreMatchers.notNullValue())
			.body("code", CoreMatchers.equalTo(400))
			.body("data", CoreMatchers.nullValue())
			.body("status", CoreMatchers.equalTo("ERROR"))
	}
	
	@Test
	fun deleteUnusedCouponTest() {
		
		val id = createDefaultCoupon()
		
		given()
			.delete("$couponURL/$id")
			.then()
			.statusCode(200)
		
		given()
			.get("$couponURL/$id")
			.then()
			.statusCode(404)
	}
	
	@Test
	fun cachingGetAllTest() {
		
		val etag = RestAssured.given().accept(ContentType.JSON)
			.get(couponURL)
			.then()
			.statusCode(200)
			.header("ETag", CoreMatchers.notNullValue())
			.extract().header("ETag")
		
		given().accept(ContentType.JSON)
			.header("If-None-Match", etag)
			.get(couponURL)
			.then()
			.statusCode(304)
			.content(CoreMatchers.equalTo(""))
	}
	
	@Test
	fun cachingGetByIdTest() {
		
		val id = createDefaultCoupon()
		
		val etag = RestAssured.given().accept(ContentType.JSON)
			.get("$couponURL/$id")
			.then()
			.statusCode(200)
			.header("ETag", CoreMatchers.notNullValue())
			.extract().header("ETag")
		
		given().accept(ContentType.JSON)
			.header("If-None-Match", etag)
			.get("$couponURL/$id")
			.then()
			.statusCode(304)
			.content(CoreMatchers.equalTo(""))
	}
	
	@Test
	fun updateDescriptionTest() {
		
		val updatedDescription = "UpdatedDescription"
		
		val id = createDefaultCoupon()
		val etag = getEtagFromId(id.toString())
		
		given().contentType("application/merge-patch+json")
			.header("If-Match", etag)
			.body("{\"description\": \"$updatedDescription\"}")
			.patch("$couponURL/$id")
			.then()
			.statusCode(204)
		
		given()
			.get("$couponURL/$id")
			.then()
			.statusCode(200)
			.body("data.list[0].description", CoreMatchers.equalTo(updatedDescription))
	}
	
	@Test
	fun updateDescriptionNumberWithInvalidInformation() {
		
		val updatedDescription = "UpdatedDescription"
		
		val id = createDefaultCoupon()
		val etag = getEtagFromId(id.toString())
		
		//Invalid JSON Merge Patch syntax
		given().contentType("application/merge-patch+json")
			.header("If-Match", etag)
			.body("{seat: \"$updatedDescription\"}")
			.patch("$couponURL/$id")
			.then()
			.statusCode(409)
		
		//Update with id in JSON Merge Patch body
		given().contentType("application/merge-patch+json")
			.header("If-Match", etag)
			.body("{\"id\": \"$id\",\"seat\": \"$updatedDescription\"}")
			.patch("$couponURL/$id")
			.then()
			.statusCode(400)
		
		//Update with invalid update value
		given().contentType("application/merge-patch+json")
			.header("If-Match", etag)
			.body("{\"abc\": 123}")
			.patch("$couponURL/$id")
			.then()
			.statusCode(400)
		
		//Update non existing ticket
		given().contentType("application/merge-patch+json")
			.header("If-Match", etag)
			.body("{\"abc\": 123}")
			.patch("$couponURL/7777")
			.then()
			.statusCode(404)
	}
	
	fun createDefaultCoupon(): Long {
		return given()
			.contentType(ContentType.JSON)
			.body(CouponDto(null, "1234567899", "DefaultDescription", "2019-01-01 01:00:00", 10))
			.post(couponURL)
			.then()
			.statusCode(201)
			.header("Location", CoreMatchers.containsString("/coupons/"))
			.extract()
			.`as`(CouponResponseDto::class.java).data!!.list.first().id!!.toLong()
	}
	
	fun createCoupon(code: String, description: String, expireAt: String, percentage: Int): Long {
		
		val dto = CouponDto(null, code, description, expireAt, percentage)
		
		return given()
			.contentType(ContentType.JSON)
			.body(dto)
			.post(couponURL)
			.then()
			.statusCode(201)
			.header("Location", CoreMatchers.containsString("/coupons/"))
			.extract()
			.`as`(CouponResponseDto::class.java).data!!.list.first().id!!.toLong()
	}
	
	fun createInvalidCoupon(code: String?, description: String?, expireAt: String?, percentage: Int?, statusCode: Int) {
		given()
			.contentType(ContentType.JSON)
			.body(CouponDto(null, code, description, expireAt, percentage))
			.post(couponURL)
			.then()
			.statusCode(statusCode)
	}
	
	fun updateInvalidCoupon(id: Long, code: String?, description: String?, expireAt: String?, percentage: Int?, etag: String) {
		
		given()
			.contentType(ContentType.JSON)
			.pathParam("id", id)
			.header("If-Match", etag)
			.body(CouponDto(id.toString(), code, description, expireAt, percentage))
			.put("$couponURL/{id}")
			.then()
			.statusCode(400)
	}
	
	fun checkDefaultCouponDto(dto: CouponDto, id: Long) {
		assertEquals(dto.id, id.toString())
		assertEquals(dto.code, "1234567899")
		assertEquals(dto.description, "DefaultDescription")
		assertEquals(dto.expireAt, convertTimeStampToZonedTimeDate("2019-01-01 01:00:00.000000").toString())
		assertEquals(dto.percentage, 10)
	}
	
	fun getEtagFromId(id: String): String {
		return given().accept(ContentType.JSON)
			.get("$couponURL/$id")
			.then()
			.extract().header("ETag")
	}
	
}