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

        response.data!!.list.forEach {
            given()
                    .delete("$cinemasUrl/${it.id}")
                    .then()
                    .statusCode(200)
        }


        assertEquals(0, getCinemasCount())
    }

    /**
     * Cinema helpers
     */

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
                .body("data.list[0].id", CoreMatchers.equalTo(id))
                .body("data.list[0].name", CoreMatchers.equalTo(name))
                .body("data.list[0].location", CoreMatchers.equalTo(location))
    }

    /**
     * Room helpers
     */

    fun getRoomsCount(id: String): Int {
        return given().accept(ContentType.JSON)
                .get("$cinemasUrl/$id/rooms")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath().getInt("data.totalSize")
    }

    fun createRoomForSpecificCinema(id: String, name: String, seats: Set<String>): Long {

        val dto = RoomDto(null, name, seats, "$id")

        return given().contentType(ContentType.JSON)
                .body(dto)
                .post("$cinemasUrl/$id/rooms")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath().getLong("data.list[0].id")
    }

    fun createRoomWithInvalidData(cinemaId: String, cinemaUrlId: String, roomId: String, name: String, seats: Set<String>?, statusCode: Int) {

        val dto = RoomDto(roomId, name, seats, "$cinemaId")

        given().contentType(ContentType.JSON)
                .body(dto)
                .post("$cinemasUrl/$cinemaUrlId/rooms")
                .then()
                .statusCode(statusCode)
    }

    fun checkRoomData(cinemaId: String, roomid: String, name: String, seatOne: String, seatTwo: String) {
        given()
                .get("$cinemasUrl/$cinemaId/rooms/$roomid")
                .then()
                .statusCode(200)
                .body("data.list[0].id", CoreMatchers.equalTo(roomid))
                .body("data.list[0].name", CoreMatchers.equalTo(name))
                .body("data.list[0].seats[0]", CoreMatchers.equalTo(seatOne))
                .body("data.list[0].seats[1]", CoreMatchers.equalTo(seatTwo))
    }

}