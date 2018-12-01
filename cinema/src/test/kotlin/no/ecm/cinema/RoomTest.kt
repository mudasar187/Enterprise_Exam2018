package no.ecm.cinema

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import junit.framework.Assert.assertEquals
import no.ecm.utils.dto.cinema.RoomDto
import no.ecm.utils.response.RoomResponse
import org.junit.Test

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

        given().accept(ContentType.JSON)
                .get("$cinemasUrl/$cinemaId/rooms")
                .then()
                .statusCode(200)
                .extract()
                .`as`(RoomResponse::class.java).data!!.totalSize

        assertEquals(0, getRoomsCount("$cinemaId"))

        val roomId = createRoomForSpecificCinema("$cinemaId", roomName, roomSeats)

        assertEquals(1, getRoomsCount("$cinemaId"))

        checkRoomData("$cinemaId", "$roomId", roomName, "A1", "A2")
    }


    @Test
    fun testGetRoomByIdNotExists() {

        val cinemaId = createCinema(cinemaName, cinemaLocation)

        given().accept(ContentType.JSON)
                .get("$cinemasUrl/$cinemaId/rooms/2")
                .then()
                .statusCode(404)
    }

    @Test
    fun testCreateRoomWithInvalidIdAndData() {

        val cinemaId = createCinema(cinemaName, cinemaLocation)

        // foreign key 'CinemaId' not match primary key 'Cinema id'
        createRoomWithInvalidData("100", "$cinemaId", "", roomName, roomSeats, 400)

        // Not allowed to provide room id
        createRoomWithInvalidData("$cinemaId", "$cinemaId", "1", roomName, roomSeats, 400)

        // Not allowed to exclude name
        createRoomWithInvalidData("$cinemaId", "$cinemaId", "", "", roomSeats, 400)

        // Not allowed to exclude seats
        createRoomWithInvalidData("$cinemaId", "$cinemaId", "", roomName, null, 400)
    }

    @Test
    fun testCreateDuplicateRoom() {

        val cinemaId = createCinema(cinemaName, cinemaLocation)

        assertEquals(0, getRoomsCount("$cinemaId"))

        createRoomForSpecificCinema("$cinemaId", roomName, roomSeats)

        assertEquals(1, getRoomsCount("$cinemaId"))

        // Room exists already with same name
        createRoomWithInvalidData("$cinemaId", "$cinemaId", "", roomName, roomSeats, 409)

        assertEquals(1, getRoomsCount("$cinemaId"))
    }

    @Test
    fun testPatchUpdateRoom() {

        val cinemaId = createCinema(cinemaName, cinemaLocation)

        assertEquals(0, getRoomsCount("$cinemaId"))

        val roomId = createRoomForSpecificCinema("$cinemaId", roomName, roomSeats)

        assertEquals(1, getRoomsCount("$cinemaId"))

        checkRoomData("$cinemaId", "$roomId", roomName, "A1", "A2")


        // Patch update name with new room name
        given().contentType("application/merge-patch+json")
                .body("{\"name\": \"$newRoomName\"}")
                .patch("$cinemasUrl/$cinemaId/rooms/$roomId")
                .then()
                .statusCode(204)

        // Patch update seats with new seats
        given().contentType("application/merge-patch+json")
                .body("{\"seats\": [\"B1\", \"B2\"]}")
                .patch("$cinemasUrl/$cinemaId/rooms/$roomId")
                .then()
                .statusCode(204)

        checkRoomData("$cinemaId", "$roomId", newRoomName, "B2", "B1")

    }

    @Test
    fun testPatchUpdateRoomInvalidData() {

        val cinemaId = createCinema(cinemaName, cinemaLocation)

        assertEquals(0, getRoomsCount("$cinemaId"))

        val roomId = createRoomForSpecificCinema("$cinemaId", roomName, roomSeats)

        // Room not found
        given().contentType("application/merge-patch+json")
                .body("{\"name\": \"$newRoomName\"}")
                .patch("$cinemasUrl/$cinemaId/rooms/100")
                .then()
                .statusCode(404)

        // Id should not be set
        given().contentType("application/merge-patch+json")
                .body("{\"id\": 2}")
                .patch("$cinemasUrl/$cinemaId/rooms/$roomId")
                .then()
                .statusCode(400)

        // Invalid JSON format
        given().contentType("application/merge-patch+json")
                .body("{[\"name\": $newRoomName}]")
                .patch("$cinemasUrl/$cinemaId/rooms/$roomId")
                .then()
                .statusCode(400)

        // Unable to parse name
        given().contentType("application/merge-patch+json")
                .body("{\"name\": 123}")
                .patch("$cinemasUrl/$cinemaId/rooms/$roomId")
                .then()
                .statusCode(400)

        // Unable to parse seats
        given().contentType("application/merge-patch+json")
                .body("{\"seats\": 123}")
                .patch("$cinemasUrl/$cinemaId/rooms/$roomId")
                .then()
                .statusCode(400)

    }

    @Test
    fun testPutUpdateRoom() {

        val cinemaId = createCinema(cinemaName, cinemaLocation)

        val roomId = createRoomForSpecificCinema("$cinemaId", roomName, roomSeats)

        // Update the entity with new room name and new seats
        given().contentType(ContentType.JSON)
                .body(RoomDto("$roomId", newRoomName, newRoomSeats, "$cinemaId"))
                .put("$cinemasUrl/$cinemaId/rooms/$roomId")
                .then()
                .statusCode(204)

        checkRoomData("$cinemaId", "$roomId", newRoomName, "B2", "B1")
    }

    @Test
    fun testPutUpdateRoomInvalidData() {

        val cinemaId = createCinema(cinemaName, cinemaLocation)

        val roomId = createRoomForSpecificCinema("$cinemaId", roomName, roomSeats)


        // Not allowed to give id
        given().contentType(ContentType.JSON)
                .body(RoomDto("100", newRoomName, newRoomSeats, "$cinemaId"))
                .put("$cinemasUrl/$cinemaId/rooms/$roomId")
                .then()
                .statusCode(400)

        // The given Cinema id in DTO doesn't match the cinema id in the database
        given().contentType(ContentType.JSON)
                .body(RoomDto("$roomId", newRoomName, newRoomSeats, "100"))
                .put("$cinemasUrl/$cinemaId/rooms/$roomId")
                .then()
                .statusCode(400)

        // Room with id 100 not exists
        given().contentType(ContentType.JSON)
                .body(RoomDto("$roomId", newRoomName, newRoomSeats, "$cinemaId"))
                .put("$cinemasUrl/$cinemaId/rooms/100")
                .then()
                .statusCode(404)

    }

    @Test
    fun testDeleteRoomByIdAndCinemaId() {

        val cinemaId = createCinema(cinemaName, cinemaLocation)

        val roomId = createRoomForSpecificCinema("$cinemaId", roomName, roomSeats)

        assertEquals(1, getRoomsCount("$cinemaId"))

        given()
                .delete("$cinemasUrl/$cinemaId/rooms/$roomId")
                .then()
                .statusCode(200)

        assertEquals(0, getRoomsCount("$cinemaId"))
    }

    @Test
    fun testDeleteRoomByIdAndCinemaIdWhereRoomNotExists() {

        val cinemaId = createCinema(cinemaName, cinemaLocation)

        given()
                .delete("$cinemasUrl/$cinemaId/rooms/100")
                .then()
                .statusCode(404)
    }

    @Test
    fun testCreateRoomWithInvalidSeatFormat() {

        val cinemaId = createCinema(cinemaName, cinemaLocation)

        val roomId = createRoomForSpecificCinema("$cinemaId", roomName, roomSeats)

        // Create room with invalid seats
        given().contentType(ContentType.JSON)
                .body(RoomDto("$roomId", newRoomName, invalidRoomSeats, "$cinemaId"))
                .put("$cinemasUrl/$cinemaId/rooms/$roomId")
                .then()
                .statusCode(400)
    }

}