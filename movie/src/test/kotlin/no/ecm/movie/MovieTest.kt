package no.ecm.movie

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import no.ecm.movie.response.MovieResponse
import no.ecm.utils.dto.movie.MovieDto
import org.hamcrest.CoreMatchers
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
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

    private fun getMovieById(id: String) : MovieDto{
        return given().contentType(ContentType.JSON)
                .get(moviesUrl)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .`as`(MovieResponse::class.java).data!!.list.first()
    }
}