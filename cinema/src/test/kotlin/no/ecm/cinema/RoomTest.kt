package no.ecm.cinema

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotSame
import no.ecm.utils.dto.cinema.RoomDto
import org.junit.Test
import org.springframework.http.HttpStatus

class RoomTest : TestBase() {

    val cinemaName = "Saga Cinema"
    val cinemaLocation = "Oslo"

    val roomName = "Caesar Theatre"
    val newRoomName = "Afrodite IMAX"

    val roomSeats = setOf("A1", "A2")
    val newRoomSeats = setOf("B1", "B2")

    val invalidRoomSeats = setOf("A1", "sdfgbvd")

    @Test
    fun testGetAllRoomsForSpecificCinema() {

        val cinemaId = createCinema(cinemaName, cinemaLocation)

        assertEquals(0, getRoomsCount("$cinemaId"))

        val roomId = createRoomForSpecificCinema("$cinemaId", roomName, roomSeats)

        assertEquals(1, getRoomsCount("$cinemaId"))

        checkRoomData("$cinemaId", "$roomId", roomName, "A1", "A2")
    }

    @Test
    fun testGetRoomByIdNotExists() {

        val cinemaId = createCinema(cinemaName, cinemaLocation)

        given().auth().basic("admin", "admin").accept(ContentType.JSON)
                .get("$cinemasUrl/$cinemaId/rooms/2")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
    }

    @Test
    fun testCreateRoomWithInvalidIdAndData() {

        val cinemaId = createCinema(cinemaName, cinemaLocation)

        // foreign key 'CinemaId' not match primary key 'Cinema id'
        createRoomWithInvalidData("100", "$cinemaId", "", roomName, roomSeats, HttpStatus.BAD_REQUEST.value())

        // Not allowed to provide room id
        createRoomWithInvalidData("$cinemaId", "$cinemaId", "1", roomName, roomSeats, HttpStatus.BAD_REQUEST.value())

        // Not allowed to exclude name
        createRoomWithInvalidData("$cinemaId", "$cinemaId", "", "", roomSeats, HttpStatus.BAD_REQUEST.value())

        // Not allowed to exclude seats
        createRoomWithInvalidData("$cinemaId", "$cinemaId", "", roomName, null, HttpStatus.BAD_REQUEST.value())
    }

    @Test
    fun testCreateDuplicateRoom() {

        val cinemaId = createCinema(cinemaName, cinemaLocation)

        assertEquals(0, getRoomsCount("$cinemaId"))

        createRoomForSpecificCinema("$cinemaId", roomName, roomSeats)

        assertEquals(1, getRoomsCount("$cinemaId"))

        // Room exists already with same name
        createRoomWithInvalidData("$cinemaId", "$cinemaId", "", roomName, roomSeats, HttpStatus.CONFLICT.value())

        assertEquals(1, getRoomsCount("$cinemaId"))
    }

    @Test
    fun testPatchUpdateRoom() {

        val cinemaId = createCinema(cinemaName, cinemaLocation)

        assertEquals(0, getRoomsCount("$cinemaId"))

        val roomId = createRoomForSpecificCinema("$cinemaId", roomName, roomSeats)

        assertEquals(1, getRoomsCount("$cinemaId"))

        checkRoomData("$cinemaId", "$roomId", roomName, "A1", "A2")

        val etag = getEtagForRoom("$cinemaId", "$roomId")

        // Patch update name with new room name
        given().auth().basic("admin", "admin").contentType("application/merge-patch+json")
                .header("If-Match", etag)
                .body("{\"name\": \"$newRoomName\"}")
                .patch("$cinemasUrl/$cinemaId/rooms/$roomId")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())

        val newETag = getEtagForRoom("$cinemaId", "$roomId")

        // Patch update seats with new seats
        given().auth().basic("admin", "admin").contentType("application/merge-patch+json")
                .header("If-Match", newETag)
                .body("{\"seats\": [\"B1\", \"B2\"]}")
                .patch("$cinemasUrl/$cinemaId/rooms/$roomId")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())

        checkRoomData("$cinemaId", "$roomId", newRoomName, "B2", "B1")

    }

    @Test
    fun testPatchUpdateRoomInvalidData() {

        val cinemaId = createCinema(cinemaName, cinemaLocation)

        assertEquals(0, getRoomsCount("$cinemaId"))

        val roomId = createRoomForSpecificCinema("$cinemaId", roomName, roomSeats)

        val etag = getEtagForRoom("$cinemaId", "$roomId")

        // Room not found
        given().auth().basic("admin", "admin").contentType("application/merge-patch+json")
                .header("If-Match", etag)
                .body("{\"name\": \"$newRoomName\"}")
                .patch("$cinemasUrl/$cinemaId/rooms/100")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())

        // Id should not be set
        given().auth().basic("admin", "admin").contentType("application/merge-patch+json")
                .header("If-Match", etag)
                .body("{\"id\": 2}")
                .patch("$cinemasUrl/$cinemaId/rooms/$roomId")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())

        // Invalid JSON format
        given().auth().basic("admin", "admin").contentType("application/merge-patch+json")
                .header("If-Match", etag)
                .body("{[\"name\": $newRoomName}]")
                .patch("$cinemasUrl/$cinemaId/rooms/$roomId")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())

        // Unable to parse name
        given().auth().basic("admin", "admin").contentType("application/merge-patch+json")
                .header("If-Match", etag)
                .body("{\"name\": 123}")
                .patch("$cinemasUrl/$cinemaId/rooms/$roomId")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())

        // Unable to parse seats
        given().auth().basic("admin", "admin").contentType("application/merge-patch+json")
                .header("If-Match", etag)
                .body("{\"seats\": 123}")
                .patch("$cinemasUrl/$cinemaId/rooms/$roomId")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())

    }

    @Test
    fun testPutUpdateRoom() {

        val cinemaId = createCinema(cinemaName, cinemaLocation)

        val roomId = createRoomForSpecificCinema("$cinemaId", roomName, roomSeats)

        val etag = getEtagForRoom("$cinemaId", "$roomId")

        // Update the entity with new room name and new seats
        given().auth().basic("admin", "admin").contentType(ContentType.JSON)
                .header("If-Match", etag)
                .body(RoomDto("$roomId", newRoomName, newRoomSeats, "$cinemaId"))
                .put("$cinemasUrl/$cinemaId/rooms/$roomId")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())

        checkRoomData("$cinemaId", "$roomId", newRoomName, "B2", "B1")
    }

    @Test
    fun testPutUpdateRoomInvalidData() {

        val cinemaId = createCinema(cinemaName, cinemaLocation)

        val roomId = createRoomForSpecificCinema("$cinemaId", roomName, roomSeats)

        val etag = getEtagForRoom("$cinemaId", "$roomId")

        // Not allowed to give id
        given().auth().basic("admin", "admin").contentType(ContentType.JSON)
                .header("If-Match", etag)
                .body(RoomDto("100", newRoomName, newRoomSeats, "$cinemaId"))
                .put("$cinemasUrl/$cinemaId/rooms/$roomId")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())

        // The given Cinema id in DTO doesn't match the cinema id in the database
        given().auth().basic("admin", "admin").contentType(ContentType.JSON)
                .header("If-Match", etag)
                .body(RoomDto("$roomId", newRoomName, newRoomSeats, "100"))
                .put("$cinemasUrl/$cinemaId/rooms/$roomId")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())

        // Room with id 100 not exists
        given().auth().basic("admin", "admin").contentType(ContentType.JSON)
                .header("If-Match", etag)
                .body(RoomDto("$roomId", newRoomName, newRoomSeats, "$cinemaId"))
                .put("$cinemasUrl/$cinemaId/rooms/100")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())

    }

    @Test
    fun testDeleteRoomByIdAndCinemaId() {

        val cinemaId = createCinema(cinemaName, cinemaLocation)

        val roomId = createRoomForSpecificCinema("$cinemaId", roomName, roomSeats)

        assertEquals(1, getRoomsCount("$cinemaId"))

        given().auth().basic("admin", "admin")
                .delete("$cinemasUrl/$cinemaId/rooms/$roomId")
                .then()
                .statusCode(HttpStatus.OK.value())

        assertEquals(0, getRoomsCount("$cinemaId"))
    }

    @Test
    fun testDeleteRoomByIdAndCinemaIdWhereRoomNotExists() {

        val cinemaId = createCinema(cinemaName, cinemaLocation)

        given().auth().basic("admin", "admin")
                .delete("$cinemasUrl/$cinemaId/rooms/100")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
    }

    @Test
    fun testCreateRoomWithInvalidSeatFormat() {

        val cinemaId = createCinema(cinemaName, cinemaLocation)

        val roomId = createRoomForSpecificCinema("$cinemaId", roomName, roomSeats)

        // Create room with invalid seats
        given().auth().basic("admin", "admin").contentType(ContentType.JSON)
                .body(RoomDto("$roomId", newRoomName, invalidRoomSeats, "$cinemaId"))
                .put("$cinemasUrl/$cinemaId/rooms/$roomId")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
    }

    @Test
    fun testEtag() {

        val cinemaId = createCinema(cinemaName, cinemaLocation)

        val roomId = createRoomForSpecificCinema("$cinemaId", roomName, roomSeats)

        val etag = getEtagForRoom("$cinemaId", "$roomId")

        // Patch update name with new cinema name
        given().auth().basic("admin", "admin").contentType("application/merge-patch+json")
                .header("If-Match", etag)
                .body("{\"name\": \"$newRoomName\"}")
                .patch("$cinemasUrl/$cinemaId/rooms/$roomId")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())

        val newEtag = getEtagForRoom("$cinemaId", "$roomId")

        assertNotSame(etag, newEtag)

    }

}