package no.ecm.movie

import io.restassured.RestAssured.given
import junit.framework.Assert.assertEquals
import no.ecm.utils.dto.movie.GenreDto
import no.ecm.utils.dto.movie.MovieDto
import no.ecm.utils.dto.movie.NowPlayingDto
import org.hamcrest.CoreMatchers
import org.junit.Test

class NowPlayingTest: TestBase() {
	
	@Test
	fun createAndGetByIdTest() {
		
		assertEquals(nowPlayingCount(), 0)
		
		val cinemaId = 1
		val roomId = 4
		val movieId = createDefaultMovie()
		//val time = "2018-12-20 20:00:00"
		
		val responseBody = getAMockRoomResponse(cinemaId, roomId)
		stubJsonResponse(responseBody)
		
		//val cinemaService = RequestSpecBuilder().setBaseUri("http://localhost").setPort(8086).setBasePath("/").build()
		//val roomId = given().spec(cinemaService).get("/cinemas/$cinemaId").then().extract().jsonPath().getLong("data.list[0].rooms[0].id")
		//given().spec(cinemaService).get("/cinemas/$cinemaId").then().extract().body().jsonPath().prettyPrint()
		
		val newNowPlayingId = createNowPlaying(createDefaultNowPlayingDto(movieId))
		
		given()
			.pathParam("id", newNowPlayingId)
			.get("$nowPlayingURL/{id}")
			.then()
			.statusCode(200)
			.body("data.list[0].id", CoreMatchers.equalTo(newNowPlayingId))
	}
	
	@Test
	fun createNowPlayingWithInvalidData() {
		
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
	fun createTwoSimultaneousNowPlaying() {
		
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
	fun createNowPlayingWithNonExistingCinema() {
		
		val cinemaId = 1
		val roomId = 4
		val movieId = createDefaultMovie()
		
		
		given()
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
		
		given()
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
	fun getInvalidNowPlaying() {
		
		val cinemaId = 1
		val roomId = 4
		val movieId = createDefaultMovie()
		
		val responseBody = getAMockRoomResponse(cinemaId, roomId)
		stubJsonResponse(responseBody)
		
		createNowPlaying(createDefaultNowPlayingDto(movieId))
		
		given()
			.pathParam("id", 123456)
			.get("$nowPlayingURL/{id}")
			.then()
			.statusCode(404)
	}
	
	@Test
	fun findNowPlayingByParams() {
		
		val cinemaId = 1
		val roomId = 4
		val movieId = createDefaultMovie()
		
		
		val responseBody = getAMockRoomResponse(cinemaId, roomId)
		stubJsonResponse(responseBody)
		
		//createNowPlaying(createDefaultNowPlayingDto(movieId))
		
		createNowPlaying(NowPlayingDto(
			id = null,
			movieDto = MovieDto(id = movieId),
			cinemaId = cinemaId.toString(),
			roomId = roomId.toString(),
			time = "2018-12-12 19:00:00"
		))
		
		createNowPlaying(NowPlayingDto(
			id = null,
			movieDto = MovieDto(id = movieId),
			cinemaId = cinemaId.toString(),
			roomId = roomId.toString(),
			time = "2018-12-12 16:00:00"
		))
		
		val size = given()
			.param("title", "My Movie Title")
			.get(nowPlayingURL)
			.then()
			.statusCode(200)
			.extract()
			.jsonPath().getInt("data.totalSize")
		
		assertEquals(2, size)
		
		val size2 = given()
			.param("date", "2018-12-12")
			.get(nowPlayingURL)
			.then()
			.statusCode(200)
			.extract()
			.jsonPath().getInt("data.totalSize")
		
		assertEquals(2, size2)
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
	
	
	private fun createDefaultMovie() : String {
		val genreId = createDefaultGenre()
		return createMovie(
			createDefaultMovieDto(mutableSetOf(GenreDto(genreId)))
		)
	}
	
}