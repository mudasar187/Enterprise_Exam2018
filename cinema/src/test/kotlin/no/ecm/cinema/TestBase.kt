package no.ecm.cinema

import io.restassured.RestAssured
import io.restassured.RestAssured.*
import io.restassured.http.ContentType
import junit.framework.Assert.assertEquals
import no.ecm.utils.response.CinemaResponse
import no.ecm.utils.dto.cinema.CinemaDto
import no.ecm.utils.dto.cinema.RoomDto
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest(
	classes = [(CinemaApplication::class)],
	webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class TestBase {
	
	@LocalServerPort
	protected val port = 0

	var cinemasUrl = "/cinemas"

	@Before
	@After
	fun clean() {
		baseURI = "http://localhost"
		RestAssured.port = port
		basePath = "/"
		enableLoggingOfRequestAndResponseIfValidationFails()

		/*
           Here, we read each resource (GET), and then delete them
           one by one (DELETE)
         */

		val response = given().accept(ContentType.JSON)
				.param("limit", 50)
				.get(cinemasUrl)
				.then()
				.statusCode(200)
				.extract()
				.`as`(CinemaResponse::class.java)

		response.data!!.list.forEach { given()
				.delete("$cinemasUrl/${ it.id }")
				.then()
				.statusCode(200) }


		assertEquals(0, getCinemasCount())
	}

	fun getCinemasCount(): Int {
		return given().accept(ContentType.JSON)
				.get(cinemasUrl)
				.then()
				.statusCode(200)
				.extract()
				.jsonPath().getInt("data.totalSize")
	}

	fun createCinema(name: String, location: String): Long {

		val dto = CinemaDto(null, name, location, null)

		return given().contentType(ContentType.JSON)
				.body(dto)
				.post(cinemasUrl)
				.then()
				.statusCode(201)
				.extract()
				.jsonPath().getLong("data.list[0].id")
	}

	fun createInvalidCinema(id: String, name: String, location: String, rooms: MutableList<RoomDto>?, statusCode: Int) {
		val dto = CinemaDto(id, name, location, rooms)

		given().contentType(ContentType.JSON)
				.body(dto)
				.post(cinemasUrl)
				.then()
				.statusCode(statusCode)
	}

	fun checkCinemaData(id: String, name: String, location: String) {
		given()
				.get("$cinemasUrl/$id")
				.then()
				.statusCode(200)
				.body("data.list[0].id", CoreMatchers.equalTo(id.toString()))
				.body("data.list[0].name", CoreMatchers.equalTo(name))
				.body("data.list[0].location", CoreMatchers.equalTo(location))
	}


}