package no.ecm.movie

import io.restassured.RestAssured.given
import io.restassured.builder.RequestSpecBuilder
import io.restassured.http.ContentType
import junit.framework.Assert.assertEquals
import no.ecm.utils.dto.movie.GenreDto
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
		
		val responseBody = getAMockCinemaResponse(cinemaId, roomId)
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
	fun testCache() {
		val cinemaId = 1
		val roomId = 4
		val movieId = createDefaultMovie()
		
		val responseBody = getAMockCinemaResponse(cinemaId, roomId)
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