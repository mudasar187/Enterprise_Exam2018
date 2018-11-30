package no.ecm.cinema

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import junit.framework.Assert.assertEquals
import no.ecm.utils.response.CinemaResponse
import no.ecm.utils.dto.cinema.CinemaDto
import no.ecm.utils.dto.cinema.RoomDto
import org.hamcrest.CoreMatchers
import org.junit.Test

class CinemaTest : TestBase() {

    val name = "Saga Cinema"
    val location = "Oslo"

    val newName = "New Name"
    val newLocation = "New Location"

    val roomList = mutableListOf<RoomDto>()

    @Test
    fun testGetAllCinemas() {

        val size = given().accept(ContentType.JSON)
                .get(cinemasUrl)
                .then()
                .statusCode(200)
                .extract()
                .`as`(CinemaResponse::class.java).data!!.list.size

        assertEquals(size, getCinemasCount())

    }

    @Test
    fun getCinemaThatDoesNotExists() {

        assertEquals(0, getCinemasCount())

        given()
                .get("$cinemasUrl/1")
                .then()
                .statusCode(404)
    }

    @Test
    fun testGetAllCinemaByFilter() {

        assertEquals(0, getCinemasCount())

        createCinema("Test Cinema 1", "Oslo")
        createCinema("Test Cinema 2", "Oslo")
        createCinema("Test Cinema 3", "Bergen")
        createCinema("Test Cinema 1", "Bergen")

        given().accept(ContentType.JSON)
                .queryParam("name", "Test Cinema 1")
                .queryParam("location", "Oslo")
                .get(cinemasUrl)
                .then()
                .statusCode(400)

        given().accept(ContentType.JSON)
                .queryParam("name", "Test Cinema 1")
                .get(cinemasUrl)
                .then()
                .statusCode(200)
                .body("data.list.size()", CoreMatchers.equalTo(2))

        given().accept(ContentType.JSON)
                .queryParam("location", "Oslo")
                .get(cinemasUrl)
                .then()
                .statusCode(200)
                .body("data.list.size()", CoreMatchers.equalTo(2))

        given().accept(ContentType.JSON)
                .queryParam("location", "Bergen")
                .get(cinemasUrl)
                .then()
                .statusCode(200)
                .body("data.list.size()", CoreMatchers.equalTo(2))

    }

    @Test
    fun cachingTest() {

        val etag =
                given()
                        .accept(ContentType.JSON)
                        .get(cinemasUrl)
                        .then()
                        .statusCode(200)
                        .header("ETag", CoreMatchers.notNullValue())
                        .extract().header("ETag")

        given()
                .accept(ContentType.JSON)
                .header("If-None-Match", etag)
                .get(cinemasUrl)
                .then()
                .statusCode(304)
                .content(CoreMatchers.equalTo(""))
    }

    @Test
    fun testCreateCinemaWithValidData() {

        assertEquals(0, getCinemasCount())

        val id = createCinema(name, location)

        assertEquals(1, getCinemasCount())

        checkCinemaData("$id",name, location)

        assertEquals(1, getCinemasCount())
    }

    @Test
    fun testCreateCinemaWithInvalidData() {

        assertEquals(0, getCinemasCount())

        createInvalidCinema("1", name, location,null, 400)

        assertEquals(0, getCinemasCount())

        createInvalidCinema("", name, "",null, 400)

        assertEquals(0, getCinemasCount())

        createInvalidCinema("", "", location,null, 400)

        assertEquals(0, getCinemasCount())

        createInvalidCinema("", name, location, roomList, 400)
    }

    @Test
    fun testCreateDuplicatedCinema() {

        assertEquals(0, getCinemasCount())

        createCinema(name, location)

        assertEquals(1, getCinemasCount())

        createInvalidCinema("", name, location, null,409)

        assertEquals(1, getCinemasCount())

    }

    @Test
    fun testPutUpdateCinema() {

        val id = createCinema(name, location)

        checkCinemaData("$id",name, location)

        assertEquals(1, getCinemasCount())

        given().contentType(ContentType.JSON)
                .body(CinemaDto(id.toString(), newName, newLocation))
                .put("$cinemasUrl/$id")
                .then()
                .statusCode(204)

        checkCinemaData("$id",newName, newLocation)

    }

    @Test
    fun testPutUpdateCinemaExceptions() {

        val id = createCinema(name, location)

        checkCinemaData("$id",name, location)

        assertEquals(1, getCinemasCount())

        given().contentType(ContentType.JSON)
                .body(CinemaDto("$id", newName, newLocation))
                .put("$cinemasUrl/2")
                .then()
                .statusCode(404)

        given().contentType(ContentType.JSON)
                .body(CinemaDto("10", newName, newLocation))
                .put("$cinemasUrl/$id")
                .then()
                .statusCode(400)

        given().contentType(ContentType.JSON)
                .body(CinemaDto("$id", "", newLocation))
                .put("$cinemasUrl/$id")
                .then()
                .statusCode(400)

        given().contentType(ContentType.JSON)
                .body(CinemaDto("$id", newName, ""))
                .put("$cinemasUrl/$id")
                .then()
                .statusCode(400)

        given().contentType(ContentType.JSON)
                .body(CinemaDto("$id", newName, ""))
                .put("$cinemasUrl/$id")
                .then()
                .statusCode(400)

        given().contentType(ContentType.JSON)
                .body(CinemaDto("$id", newName, newLocation, roomList))
                .put("$cinemasUrl/$id")
                .then()
                .statusCode(400)

    }

    @Test
    fun testPatchUpdateCinema() {

        val id = createCinema(name, location)

        assertEquals(1, getCinemasCount())

        given().contentType("application/merge-patch+json")
                .body("{\"name\": \"$newName\"}")
                .patch("$cinemasUrl/$id")
                .then()
                .statusCode(204)

        given().contentType("application/merge-patch+json")
                .body("{\"location\": \"$newLocation\"}")
                .patch("$cinemasUrl/$id")
                .then()
                .statusCode(204)

        checkCinemaData("$id", newName, newLocation)
    }

    @Test
    fun testPatchUpdateCinemaExceptions() {

        val id = createCinema(name, location)

        assertEquals(1, getCinemasCount())

        given().contentType("application/merge-patch+json")
                .body("{\"name\": \"$newName\"}")
                .patch("$cinemasUrl/2")
                .then()
                .statusCode(404)

        given().contentType("application/merge-patch+json")
                .body("{name\": \"$newName\"}")
                .patch("$cinemasUrl/$id")
                .then()
                .statusCode(400)

        given().contentType("application/merge-patch+json")
                .body("{\"id\": \"2\"}")
                .patch("$cinemasUrl/$id")
                .then()
                .statusCode(400)

        given().contentType("application/merge-patch+json")
                .body("{\"name\": 2}")
                .patch("$cinemasUrl/$id")
                .then()
                .statusCode(400)

        given().contentType("application/merge-patch+json")
                .body("{\"location\": 2}")
                .patch("$cinemasUrl/$id")
                .then()
                .statusCode(400)

        given().contentType("application/merge-patch+json")
                .body("{\"rooms\": $roomList}")
                .patch("$cinemasUrl/$id")
                .then()
                .statusCode(400)

    }

    @Test
    fun testDeleteByIdWithNoExistsId() {

        assertEquals(0, getCinemasCount())

        given()
                .delete("$cinemasUrl/2")
                .then()
                .statusCode(404)
    }






}