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

    @Test
    fun testPatchGenre() {
        val createdGenre = getGenreById(createDefaultGenre().toLong())

        val name = createdGenre.name + " test"

        given().contentType("application/merge-patch+json")
                .body("{\"name\": \"$name\"}")
                .patch("$genresUrl/${createdGenre.id}")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())

        val patchedGenre = getGenreById(createdGenre.id!!.toLong())

        assertEquals(name, patchedGenre.name)
    }

    @Test
    fun testFailingPatchRequests() {

        val createdGenre = getGenreById(createDefaultGenre().toLong())

        val name = createdGenre.name + " test"

        val id = createdGenre.id!!

        //Invalid JSON Merge Patch syntax
        given().contentType("application/merge-patch+json")
                .body("{name: \"$name\"}")
                .patch("$genresUrl/$id")
                .then()
                .statusCode(400)

        //Update with id in JSON Merge Patch body
        given().contentType("application/merge-patch+json")
                .body("{\"id\": \"$id\",\"name\": \"$name\"}")
                .patch("$genresUrl/$id")
                .then()
                .statusCode(400)

        //Update non existing ticket
        given().contentType("application/merge-patch+json")
                .body("{\"name\": \"$name\"}")
                .patch("$genresUrl/7777")
                .then()
                .statusCode(404)

    }

    @Test
    fun testPutGenreAndGetBadRequest() {
        val createdGenre = getGenreById(createDefaultGenre().toLong())

        createdGenre.name = createdGenre.name + " test"
        val id = createdGenre.id!!

        given().contentType(ContentType.JSON)
                .body(createdGenre)
                .put("$genresUrl/$id")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())

        //val updatedGenre = getGenreById(createdGenre.id!!.toLong())

        //assertEquals(createdGenre.name, updatedGenre.name)
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