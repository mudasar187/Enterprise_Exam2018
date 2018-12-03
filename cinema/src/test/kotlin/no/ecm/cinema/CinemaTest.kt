package no.ecm.cinema

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import junit.framework.Assert.assertEquals
import no.ecm.utils.response.CinemaResponse
import no.ecm.utils.dto.cinema.CinemaDto
import no.ecm.utils.dto.cinema.RoomDto
import org.hamcrest.CoreMatchers
import org.junit.Test
import org.springframework.http.HttpStatus

class CinemaTest : TestBase() {

    val cinemaName = "Saga Cinema"
    val cinemaLocation = "Oslo"

    val newCinemaName = "New Name"
    val newCinemaLocation = "New Location"

    val roomList = mutableListOf<RoomDto>()

    @Test
    fun testGetAllCinemas() {

        val size = given().accept(ContentType.JSON)
                .get(cinemasUrl)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .`as`(CinemaResponse::class.java).data!!.list.size

        assertEquals(size, getCinemasCount())

    }

    @Test
    fun getCinemaThatDoesNotExists() {

        assertEquals(0, getCinemasCount())

        given()
                .get("$cinemasUrl/100")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
    }

    @Test
    fun testGetAllCinemaByFilter() {

        assertEquals(0, getCinemasCount())

        createCinema("Test Cinema 1", "Oslo")
        createCinema("Test Cinema 2", "Oslo")
        createCinema("Test Cinema 3", "Bergen")
        createCinema("Test Cinema 1", "Bergen")

        assertEquals(4, getCinemasCount())

        // Not allowed to add two filters
        given().accept(ContentType.JSON)
                .queryParam("name", "Test Cinema 1")
                .queryParam("location", "Oslo")
                .get(cinemasUrl)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())

        // Get all cinemas with name: 'Test Cinema 1'
        given().accept(ContentType.JSON)
                .queryParam("name", "Test Cinema 1")
                .get(cinemasUrl)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("data.list.size()", CoreMatchers.equalTo(2))

        // Get all cinemas with location: 'Oslo'
        given().accept(ContentType.JSON)
                .queryParam("location", "Oslo")
                .get(cinemasUrl)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("data.list.size()", CoreMatchers.equalTo(2))

        // Get all cinemas with location: 'Bergen'
        given().accept(ContentType.JSON)
                .queryParam("location", "Bergen")
                .get(cinemasUrl)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("data.list.size()", CoreMatchers.equalTo(2))

    }

    @Test
    fun cachingTest() {

        val etag =
                given()
                        .accept(ContentType.JSON)
                        .get(cinemasUrl)
                        .then()
                        .statusCode(HttpStatus.OK.value())
                        .header("ETag", CoreMatchers.notNullValue())
                        .extract().header("ETag")

        given()
                .accept(ContentType.JSON)
                .header("If-None-Match", etag)
                .get(cinemasUrl)
                .then()
                .statusCode(HttpStatus.NOT_MODIFIED.value())
                .content(CoreMatchers.equalTo(""))
    }

    @Test
    fun testCreateCinemaWithValidData() {

        assertEquals(0, getCinemasCount())

        val id = createCinema(cinemaName, cinemaLocation)

        assertEquals(1, getCinemasCount())

        checkCinemaData("$id", cinemaName, cinemaLocation)

    }

    // TODO: maybe a test with check of location in response header

    @Test
    fun testCreateCinemaWithInvalidData() {

        assertEquals(0, getCinemasCount())

        // Not allowed to add id
        createInvalidCinema("1", cinemaName, cinemaLocation, null, HttpStatus.BAD_REQUEST.value())

        // Not allowed to have location empty
        createInvalidCinema("", cinemaName, "", null, HttpStatus.BAD_REQUEST.value())

        // Not allowed to have name empty
        createInvalidCinema("", "", cinemaLocation, null, HttpStatus.BAD_REQUEST.value())

        // Not allowed to add room list
        createInvalidCinema("", cinemaName, cinemaLocation, roomList, HttpStatus.BAD_REQUEST.value())

        assertEquals(0, getCinemasCount())
    }

    @Test
    fun testCreateDuplicatedCinema() {

        assertEquals(0, getCinemasCount())

        createCinema(cinemaName, cinemaLocation)

        assertEquals(1, getCinemasCount())

        // Add cinema with same name and location
        createInvalidCinema("", cinemaName, cinemaLocation, null, HttpStatus.CONFLICT.value())

        assertEquals(1, getCinemasCount())

    }

    @Test
    fun testPutUpdateCinema() {

        val id = createCinema(cinemaName, cinemaLocation)

        assertEquals(1, getCinemasCount())

        checkCinemaData("$id", cinemaName, cinemaLocation)

        // Update entity with new cinema name and new cinema location
        given().contentType(ContentType.JSON)
                .body(CinemaDto(id.toString(), newCinemaName, newCinemaLocation))
                .put("$cinemasUrl/$id")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())

        checkCinemaData("$id", newCinemaName, newCinemaLocation)

    }

    @Test
    fun testPutUpdateCinemaInvalidData() {

        val id = createCinema(cinemaName, cinemaLocation)

        assertEquals(1, getCinemasCount())

        checkCinemaData("$id", cinemaName, cinemaLocation)

        // Wrong id in url path
        given().contentType(ContentType.JSON)
                .body(CinemaDto("$id", newCinemaName, newCinemaLocation))
                .put("$cinemasUrl/100")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())

        // Not matching id
        given().contentType(ContentType.JSON)
                .body(CinemaDto("100", newCinemaName, newCinemaLocation))
                .put("$cinemasUrl/$id")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())

        // Not allowed to exclude name
        given().contentType(ContentType.JSON)
                .body(CinemaDto("$id", "", newCinemaLocation))
                .put("$cinemasUrl/$id")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())

        // Not allowed to exclude location
        given().contentType(ContentType.JSON)
                .body(CinemaDto("$id", newCinemaName, ""))
                .put("$cinemasUrl/$id")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())

        // Not allowed to add roomlist
        given().contentType(ContentType.JSON)
                .body(CinemaDto("$id", newCinemaName, newCinemaLocation, roomList))
                .put("$cinemasUrl/$id")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())


    }

    @Test
    fun testPatchUpdateCinema() {

        val id = createCinema(cinemaName, cinemaLocation)

        assertEquals(1, getCinemasCount())

        // Patch update name with new cinema name
        given().contentType("application/merge-patch+json")
                .body("{\"name\": \"$newCinemaName\"}")
                .patch("$cinemasUrl/$id")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())

        // Patch update location with new location
        given().contentType("application/merge-patch+json")
                .body("{\"location\": \"$newCinemaLocation\"}")
                .patch("$cinemasUrl/$id")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())

        checkCinemaData("$id", newCinemaName, newCinemaLocation)
    }

    @Test
    fun testPatchUpdateCinemaInvalidData() {

        val id = createCinema(cinemaName, cinemaLocation)

        assertEquals(1, getCinemasCount())

        // Cinema id not exists
        given().contentType("application/merge-patch+json")
                .body("{\"name\": \"$newCinemaName\"}")
                .patch("$cinemasUrl/100")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())

        // Invalid JSON format
        given().contentType("application/merge-patch+json")
                .body("{name\": \"$newCinemaName\"}")
                .patch("$cinemasUrl/$id")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())

        // Not allowed to change id
        given().contentType("application/merge-patch+json")
                .body("{\"id\": \"2\"}")
                .patch("$cinemasUrl/$id")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())

        // Unable to parse name
        given().contentType("application/merge-patch+json")
                .body("{\"name\": 2}")
                .patch("$cinemasUrl/$id")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())

        // Unbale to parse location
        given().contentType("application/merge-patch+json")
                .body("{\"location\": 2}")
                .patch("$cinemasUrl/$id")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())

        // Not allowed to update roomlist
        given().contentType("application/merge-patch+json")
                .body("{\"rooms\": $roomList}")
                .patch("$cinemasUrl/$id")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
    }

    @Test
    fun testDeleteByIdWithNoExistsId() {

        assertEquals(0, getCinemasCount())

        given()
                .delete("$cinemasUrl/2")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
    }

}