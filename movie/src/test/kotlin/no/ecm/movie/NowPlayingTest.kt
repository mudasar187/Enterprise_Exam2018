package no.ecm.movie

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.restassured.response.Response
import junit.framework.Assert.assertEquals
import no.ecm.utils.dto.movie.GenreDto
import no.ecm.utils.dto.movie.MovieDto
import no.ecm.utils.dto.movie.NowPlayingDto
import org.hamcrest.CoreMatchers
import org.junit.Test

class NowPlayingTest: TestBase() {
	
	@Test
	fun createAndGetByIdTestTest() {
		
		assertEquals(nowPlayingCount(), 0)
		
		val cinemaId = 1
		val roomId = 4
		val movieId = createDefaultMovie()

		val responseBody = getAMockRoomResponse(cinemaId, roomId)
		stubJsonResponse(responseBody)
		
		val newNowPlayingId = createNowPlaying(createDefaultNowPlayingDto(movieId))
		
		given().auth().basic("admin", "admin")
			.pathParam("id", newNowPlayingId)
			.get("$nowPlayingURL/{id}")
			.then()
			.statusCode(200)
			.body("data.list[0].id", CoreMatchers.equalTo(newNowPlayingId))
	}

	@Test
	fun testInternalServerError() {

		val movieId = createDefaultMovie()

		stubFailJsonResponse()

		given().auth().basic("admin", "admin").contentType(ContentType.JSON)
				.body(createDefaultNowPlayingDto(movieId))
				.post(nowPlayingURL)
				.then()
				.statusCode(500)
	}

//	@Test
//	fun testCircuitBreakerTriggered() {
//	}
	
	@Test
	fun createNowPlayingWithInvalidDataTest() {
		
		val cinemaId = 1
		val roomId = 4
		val movieId = createDefaultMovie()
		
		val responseBody = getAMockRoomResponse(cinemaId, roomId)
		stubJsonResponse(responseBody)
		
		//seats not null
		createNowPlayingWithoutChecks(NowPlayingDto(
			null,
			movieDto = MovieDto(id = movieId),
			cinemaId = cinemaId.toString(),
			roomId = roomId.toString(),
			time = "2018-12-20 20:00:00",
			seats = listOf("A1", "A2"))
		)!!.then().statusCode(400)
		
		//id not null
		createNowPlayingWithoutChecks(NowPlayingDto(
			"123",
			movieDto = MovieDto(id = movieId),
			cinemaId = cinemaId.toString(),
			roomId = roomId.toString(),
			time = "2018-12-20 20:00:00")
		)!!.then().statusCode(400)
		
		//invalid movie
		createNowPlayingWithoutChecks(NowPlayingDto(
			null,
			movieDto = MovieDto(id = "2000"),
			cinemaId = cinemaId.toString(),
			roomId = roomId.toString(),
			time = "2018-12-20 20:00:00")
		)!!.then().statusCode(404)
	}
	
	@Test
	fun createTwoSimultaneousNowPlayingTest() {
		
		val cinemaId = 1
		val roomId = 4
		val movieId = createDefaultMovie()
		
		val responseBody = getAMockRoomResponse(cinemaId, roomId)
		stubJsonResponse(responseBody)
		
		createNowPlaying(createDefaultNowPlayingDto(movieId))
		
		createNowPlayingWithoutChecks(NowPlayingDto(
			null,
			movieDto = MovieDto(id = movieId),
			cinemaId = "1",
			roomId = "4",
			time = "2018-12-20 20:00:00")
		)!!.then().statusCode(409)
	}
	
	@Test
	fun createNowPlayingWithNonExistingCinemaTest() {
		
		val cinemaId = 1
		val roomId = 4
		val movieId = createDefaultMovie()
		
		given().auth().basic("admin", "admin")
			.body("""
				{
				  "cinemaId": "1000",
				  "movieDto": {
					"id": "4"
				  },
				  "roomId": "$roomId",
				  "time": "2018-12-12 20:00:00"
				}
			""".trimIndent())
			.post()
			.then()
			.statusCode(404)
		
		given().auth().basic("admin", "admin")
			.body("""
				{
				  "cinemaId": "$cinemaId",
				  "movieDto": {
					"id": "4"
				  },
				  "roomId": "400",
				  "time": "2018-12-12 20:00:00"
				}
			""".trimIndent())
			.post()
			.then()
			.statusCode(404)
	}
	
	@Test
	fun getInvalidNowPlayingTest() {
		
		val cinemaId = 1
		val roomId = 4
		val movieId = createDefaultMovie()
		
		val responseBody = getAMockRoomResponse(cinemaId, roomId)
		stubJsonResponse(responseBody)
		
		createNowPlaying(createDefaultNowPlayingDto(movieId))
		
		given().auth().basic("admin", "admin")
			.pathParam("id", 123456)
			.get("$nowPlayingURL/{id}")
			.then()
			.statusCode(404)
	}
	
	@Test
	fun findNowPlayingByParamsTest() {
		
		val cinemaId = 1
		val roomId = 4
		val movieId = createDefaultMovie()
		
		val responseBody = getAMockRoomResponse(cinemaId, roomId)
		stubJsonResponse(responseBody)
		
		createNowPlaying(NowPlayingDto(
			id = null,
			movieDto = MovieDto(id = movieId),
			cinemaId = cinemaId.toString(),
			roomId = roomId.toString(),
			time = "2018-12-11 19:00:00"
		))
		
		createNowPlaying(NowPlayingDto(
			id = null,
			movieDto = MovieDto(id = movieId),
			cinemaId = cinemaId.toString(),
			roomId = roomId.toString(),
			time = "2018-12-12 16:00:00"
		))
		
		given().auth().basic("admin", "admin")
			.param("title", "My Movie Title")
			.get(nowPlayingURL)
			.then()
			.statusCode(200)
			.body("data.totalSize", CoreMatchers.equalTo(2))
		
		given().auth().basic("admin", "admin")
			.param("date", "2018-12-12")
			.get(nowPlayingURL)
			.then()
			.statusCode(200)
			.body("data.totalSize", CoreMatchers.equalTo(1))
	}
	
	@Test
	fun findNowPlayingByInvalidParamsTest() {
		
		val cinemaId = 1
		val roomId = 4
		val movieId = createDefaultMovie()
		
		val responseBody = getAMockRoomResponse(cinemaId, roomId)
		stubJsonResponse(responseBody)
		
		createNowPlaying(createDefaultNowPlayingDto(movieId))
		
		given().auth().basic("admin", "admin")
			.param("date", "2018-12-12")
			.param("title", "My Movie Title")
			.get(nowPlayingURL)
			.then()
			.statusCode(400)
	}
	
	@Test
	fun updateNowPlayingSeats() {
		
		val cinemaId = 1
		val roomId = 4
		val movieId = createDefaultMovie()
		
		val responseBody = getAMockRoomResponse(cinemaId, roomId)
		stubJsonResponse(responseBody)
		
		val nowPlayingId = createNowPlaying(createDefaultNowPlayingDto(movieId))
		
		val etag1 = getEtagFromNowPlayingId(nowPlayingId)
		val seatsIsNull = """{"seats": null}"""
		patchRequest(seatsIsNull, nowPlayingId, etag1).then().statusCode(204)
		
		val etag2 = getEtagFromNowPlayingId(nowPlayingId)
		val validJsonPatch = """{"seats": ["A1","B1","C1"]}"""
		patchRequest(validJsonPatch, nowPlayingId, etag2).then().statusCode(204)
		
		given().auth().basic("admin", "admin")
			.pathParam("id", nowPlayingId)
			.get("$nowPlayingURL/{id}")
			.then()
			.statusCode(200)
			.body("data.list[0].id", CoreMatchers.equalTo(nowPlayingId))
	}
	
	@Test
	fun updateNowPlayingWithMissingIfMatchHeaderTest() {
		
		val cinemaId = 1
		val roomId = 4
		val movieId = createDefaultMovie()
		
		val responseBody = getAMockRoomResponse(cinemaId, roomId)
		stubJsonResponse(responseBody)
		
		val nowPlayingId = createNowPlaying(createDefaultNowPlayingDto(movieId))
		
		val validJsonPatch = """{"seats": ["A1","B1","C1"]}"""
		
		given().auth().basic("admin", "admin")
			.contentType("application/merge-patch+json")
			.pathParam("id", nowPlayingId)
			.body(validJsonPatch)
			.patch("$nowPlayingURL/{id}")
			.then()
			.statusCode(400)
	}
	
	@Test
	fun updateNowPlayingWithInvalidFieldsTest() {
		
		val cinemaId = 1
		val roomId = 4
		val movieId = createDefaultMovie()
		
		val responseBody = getAMockRoomResponse(cinemaId, roomId)
		stubJsonResponse(responseBody)
		
		val nowPlayingId = createNowPlaying(createDefaultNowPlayingDto(movieId))
		val etag = getEtagFromNowPlayingId(nowPlayingId)
		
		
		val idValueInJson = """
				{
					"id": "$nowPlayingId",
					"seats": ["A1","B1","C1"]
				}""".trimIndent()
		patchRequest(idValueInJson, nowPlayingId, etag).then().statusCode(400)
		
		
		val movieDtoInJson = """
				{
					"movieDto": "{}",
					"seats": ["A1","B1","C1"]
				}""".trimIndent()
		patchRequest(movieDtoInJson, nowPlayingId, etag).then().statusCode(400)
		
		
		val seatsArrayMissing = """
				{
					"id": "$nowPlayingId"
				}""".trimIndent()
		patchRequest(seatsArrayMissing, nowPlayingId, etag).then().statusCode(400)
		
		
		val seatsAsObjectInsteadOfArray = """
				{
					"seats": {}
				}""".trimIndent()
		patchRequest(seatsAsObjectInsteadOfArray, nowPlayingId, etag).then().statusCode(400)
		
		//invalid JSON format in patch object
		val invalidJsonFormat = """
				{
					seats: ["A1","B1","C1"]
				}""".trimIndent()
		patchRequest(invalidJsonFormat, nowPlayingId, etag).then().statusCode(412)
	}
	
	@Test
	fun testCache() {
		val cinemaId = 1
		val roomId = 4
		val movieId = createDefaultMovie()
		
		val responseBody = getAMockRoomResponse(cinemaId, roomId)
		stubJsonResponse(responseBody)
		
		val newNowPlayingId = createNowPlaying(createDefaultNowPlayingDto(movieId))
		
		testCache("$nowPlayingURL/$newNowPlayingId")
	}
	
	@Test
	fun testDeleteNowPlaying() {
		
		val cinemaId = 1
		val roomId = 4
		val movieId = createDefaultMovie()
		
		val responseBody = getAMockRoomResponse(cinemaId, roomId)
		stubJsonResponse(responseBody)
		
		val newNowPlayingId = createNowPlaying(createDefaultNowPlayingDto(movieId))
		
		given().auth().basic("admin", "admin")
			.pathParam("id", newNowPlayingId)
			.delete("$nowPlayingURL/{id}")
			.then()
			.statusCode(200)
		
		given().auth().basic("admin", "admin")
			.pathParam("id", newNowPlayingId)
			.get("$nowPlayingURL/{id}")
			.then()
			.statusCode(404)
	}
	
	@Test
	fun testNonExistingNowPlaying() {
		given().auth().basic("admin", "admin")
			.pathParam("id", 1234)
			.delete("$nowPlayingURL/{id}")
			.then()
			.statusCode(404)
	}
	
	private fun createDefaultMovie() : String {
		val genreId = createDefaultGenre()
		return createMovie(
			createDefaultMovieDto(mutableSetOf(GenreDto(genreId)))
		)
	}
	
	private fun patchRequest(json: String, nowPlayingId: String, etag: String): Response {
		return given().auth().basic("admin", "admin")
			.contentType("application/merge-patch+json")
			.pathParam("id", nowPlayingId)
			.header("If-Match", etag)
			.body(json)
			.patch("$nowPlayingURL/{id}")
	}
}