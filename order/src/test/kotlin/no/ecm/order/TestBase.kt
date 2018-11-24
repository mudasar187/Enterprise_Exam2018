package no.ecm.order

import io.restassured.RestAssured
import io.restassured.http.ContentType
import no.ecm.utils.dto.order.CouponDto
import no.ecm.utils.response.CouponResponseDto
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
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
		// I use RestAssured to minimize boilerplate code.
		// Here i set the base settings for the tests
		RestAssured.baseURI = "http://localhost"        // defining the base URL
		RestAssured.port = port                        // setting the port to the Random Generated port that Springboot gives us
		RestAssured.basePath = "/coupons"                // defining the base URL path
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
		
		/*
		val list = RestAssured.given().accept(ContentType.JSON).get()
			.then()
			.statusCode(200)
			.extract()
			.`as`(ResponseDto::class.java)
		
		list.page!!.data.stream().forEach {
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
			.body("page.data.size()", CoreMatchers.equalTo(0))
			*/
	}
}