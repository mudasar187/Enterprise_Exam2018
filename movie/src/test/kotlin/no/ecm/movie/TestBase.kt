package no.ecm.movie

import io.restassured.RestAssured
import io.restassured.RestAssured.*
import io.restassured.http.ContentType
import no.ecm.movie.response.GenreResponse
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [(MovieApplication::class)],
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class TestBase {

    @LocalServerPort
    protected var port = 0

    val genresUrl = "/genres"

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

    fun getGenreCount(): Int {
        return given().accept(ContentType.JSON)
                .get(genresUrl)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath().getInt("data.totalSize")
    }
}