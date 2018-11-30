package no.ecm.movie

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import no.ecm.utils.response.MovieResponse
import no.ecm.utils.dto.movie.MovieDto
import org.hamcrest.CoreMatchers
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.springframework.http.HttpStatus

class MovieTest: TestBase() {

    @Test
    fun testCreateMovie() {
        createDefaultMovie()
        assertNotEquals(0, getMovieCount())
    }

    @Test
    fun testGetMoviesByTitle() {
        val movie = getMovieById(createDefaultMovie())

        given().contentType(ContentType.JSON)
                .queryParam("title", movie.title)
                .get(moviesUrl)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("data.list[0].title", CoreMatchers.equalTo(movie.title))
    }

    @Test
    fun testGetMovieByAgeLimit() {
        val movie = getMovieById(createDefaultMovie())

        given().contentType(ContentType.JSON)
                .queryParam("ageLimit", movie.ageLimit)
                .get(moviesUrl)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("data.list.size()", CoreMatchers.not(0))
    }

    @Test
    fun cachingTest() {

        val etag =
                given()
                        .accept(ContentType.JSON)
                        .get(moviesUrl)
                        .then()
                        .statusCode(200)
                        .header("ETag", CoreMatchers.notNullValue())
                        .extract().header("ETag")

        given()
                .accept(ContentType.JSON)
                .header("If-None-Match", etag)
                .get(moviesUrl)
                .then()
                .statusCode(304)
                .content(CoreMatchers.equalTo(""))
    }

    private fun getMovieById(id: String) : MovieDto{
        return given().contentType(ContentType.JSON)
                .get("$moviesUrl/$id")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .`as`(MovieResponse::class.java).data!!.list.first()
    }
}