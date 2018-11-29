package no.ecm.movie

import io.restassured.RestAssured
import io.restassured.RestAssured.*
import io.restassured.http.ContentType
import no.ecm.movie.response.GenreResponse
import no.ecm.movie.response.MovieResponse
import no.ecm.utils.dto.movie.GenreDto
import no.ecm.utils.dto.movie.MovieDto
import org.junit.After
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [(MovieApplication::class)],
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
abstract class TestBase {

    @LocalServerPort
    protected var port = 0

    val genresUrl = "/genres"
    val moviesUrl = "/movies"

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

        cleanGenres()
        cleanMovies()
    }

    private fun cleanGenres() {
        val response = given().accept(ContentType.JSON)
                .param("limit", 100)
                .get(genresUrl)
                .then()
                .statusCode(200)
                .extract()
                .`as`(GenreResponse::class.java)

        response.data!!.list.forEach { given()
                .delete("$genresUrl/${ it.id }")
                .then()
                .statusCode(200) }

        assertEquals(0, getGenreCount())
    }

    private fun cleanMovies() {
        val response = given().accept(ContentType.JSON)
                .param("limit", 100)
                .get(moviesUrl)
                .then()
                .statusCode(200)
                .extract()
                .`as`(MovieResponse::class.java)

        response.data!!.list.forEach { given()
                .delete("$moviesUrl/${ it.id }")
                .then()
                .statusCode(200) }

        assertEquals(0, getMovieCount())
    }

    fun getMovieCount(): Int {
        return given().accept(ContentType.JSON)
                .get(moviesUrl)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath().getInt("data.totalSize")
    }

    fun getGenreCount(): Int {
        return given().accept(ContentType.JSON)
                .get(genresUrl)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath().getInt("data.totalSize")
    }

    fun createDefaultGenre() : String {
        val response = given().contentType(ContentType.JSON)
                .body(createDefaultGenreDto())
                .post(genresUrl)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .`as`(GenreResponse::class.java)

        Assert.assertNotNull(response.data)
        assertEquals(1, response.data!!.list.size)
        Assert.assertNotNull(response.data!!.list.first().id!!)

        return response.data!!.list.first().id!!
    }

    fun createDefaultGenreDto() : GenreDto {
        return GenreDto(
                name = "horror")
    }

    fun createDefaultMovieDto(): MovieDto {
        return MovieDto(
                title = "My Movie Title",
                posterUrl = "url.com",
                movieDuration = 120,
                ageLimit = 10,
                genre = mutableSetOf(GenreDto(id = createDefaultGenre()))
        )
    }

    fun createDefaultMovie() : String {
        val dto = createDefaultMovieDto()

        return given().contentType(ContentType.JSON)
                .body(dto)
                .post(moviesUrl)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .`as`(MovieResponse::class.java).data!!.list.first().id!!
    }
}