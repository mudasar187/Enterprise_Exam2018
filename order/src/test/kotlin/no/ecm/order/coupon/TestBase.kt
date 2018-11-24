package no.ecm.order.coupon

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import no.ecm.order.OrderApplication
import no.ecm.utils.dto.order.CouponDto
import no.ecm.utils.response.CouponResponseDto
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest(
	classes = [(OrderApplication::class)],
	webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
abstract class TestBase {
	
	@LocalServerPort
	protected var port = 0
	
	@Before
	@After
	fun clean() {
		RestAssured.baseURI = "http://localhost"
		RestAssured.port = port
		RestAssured.basePath = "/coupons"
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
		
		
		val list = RestAssured.given().accept(ContentType.JSON).get()
			.then()
			.statusCode(200)
			.extract()
			.`as`(CouponResponseDto::class.java)
		
		/*
		// TODO fix this DELETE stuff with foreign keys and shit
		list.data!!.list.stream().forEach {
			RestAssured.given()
				.param("id", it.id)
				.delete()
				.then()
				.statusCode(204)
		}
		
		RestAssured.given()
			.get()
			.then()
			.statusCode(200)
			.body("data.list.size()", CoreMatchers.equalTo(0))
		*/
	}
	
	fun createCoupon(code: String, description: String, expireAt: String): Long {
		
		val dto = CouponDto(null, code, description, expireAt)
		
		return given()
			.contentType(ContentType.JSON)
			.body(dto)
			.post()
			.then()
			.statusCode(201)
			.extract()
			.jsonPath().getLong("data.list[0].id")
	}
	
	fun assertResultSize(size: Int) {
		given().get().then().statusCode(200).body("data.list.size()", CoreMatchers.equalTo(size))
	}
	
}