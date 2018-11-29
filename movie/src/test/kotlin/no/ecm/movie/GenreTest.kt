package no.ecm.movie

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import no.ecm.movie.response.GenreResponse
import no.ecm.utils.dto.movie.GenreDto
import org.junit.Assert.*
import org.junit.Test
import org.springframework.http.HttpStatus

class GenreTest: TestBase() {

    @Test
    fun testCreateGenre() {
        val createdGenre = getGenreById(createDefaultGenre().toLong())

        assertEquals(createDefaultGenreDto().name, createdGenre.name)
    }

    @Test
    fun testGetGenreByName() {
        val createdGenre = getGenreById(createDefaultGenre().toLong())

        val genreResponse = given().contentType(ContentType.JSON)
                .queryParam("name", createdGenre.name)
                .get(genresUrl)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .`as`(GenreResponse::class.java)

        assertNotNull(genreResponse.data)

        assertEquals(createdGenre.name, genreResponse.data!!.list.first().name)
    }

    private fun getGenreById(id: Long): GenreDto {
        val response = given().contentType(ContentType.JSON)
                .get("$genresUrl/$id")
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .`as`(GenreResponse::class.java)

        assertNotNull(response.data)
        assertEquals(1, response.data!!.list.size)

        return response.data!!.list.first()
    }

    private fun createDefaultGenre() : String {
        val response = given().contentType(ContentType.JSON)
                        .body(createDefaultGenreDto())
                        .post(genresUrl)
                        .then()
                        .statusCode(HttpStatus.CREATED.value())
                        .extract()
                        .`as`(GenreResponse::class.java)

        assertNotNull(response.data)
        assertEquals(1, response.data!!.list.size)
        assertNotNull(response.data!!.list.first().id!!)

        return response.data!!.list.first().id!!
    }

    private fun createDefaultGenreDto() : GenreDto{
        return GenreDto(
                name = "Horror")
    }
}