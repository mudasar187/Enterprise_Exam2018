package no.ecm.movie

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import no.ecm.utils.response.GenreResponse
import no.ecm.utils.dto.movie.GenreDto
import no.ecm.utils.dto.movie.MovieDto
import org.hamcrest.CoreMatchers
import org.junit.Assert.*
import org.junit.Test
import org.springframework.http.HttpStatus

class GenreTest: TestBase() {

    @Test
    fun testCreateGenre() {
        val (createdGenre) = getGenreById(createDefaultGenre().toLong())

        assertEquals(createDefaultGenreDto().name!!.capitalize(), createdGenre.name)
    }

    @Test
    fun testCreateGenreWithManualId() {
        val dto = createDefaultGenreDto()
        dto.id = "1234"

        given().contentType(ContentType.JSON)
                .body(dto)
                .post(genresUrl)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
    }

    @Test
    fun testCreateGenreAndRetriveMovieByGenre() {
        val (createdGenre) = getGenreById(createDefaultGenre().toLong())

        assertEquals(createDefaultGenreDto().name!!.capitalize(), createdGenre.name)

        val movieDto = createDefaultMovieDto(mutableSetOf(createdGenre))

        createMovie(movieDto)

        val (genreResponse) = getGenreById(createdGenre.id!!.toLong())

        assertNotEquals(0, genreResponse.movies!!.size)
    }

    @Test
    fun testCreateGenreWithMovie() {
        val dto = createDefaultGenreDto()
        dto.movies = mutableSetOf(MovieDto(title = "test"))

        given().contentType(ContentType.JSON)
                .body(dto)
                .post(genresUrl)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
    }

    @Test
    fun testGenresCache() {
        testCache(genresUrl)
    }

    @Test
    fun testGenreCache() {
        val id = createDefaultGenre()
        testCache("$genresUrl/$id")
    }

    @Test
    fun testCreateGenreWithEmptyName() {
        given().contentType(ContentType.JSON)
                .body(GenreDto())
                .post(genresUrl)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
    }

    @Test
    fun testCreateGenreTwice() {
        createDefaultGenre()

        given().contentType(ContentType.JSON)
                .body(createDefaultGenreDto())
                .post(genresUrl)
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
    }

    @Test
    fun testGetGenreByName() {
        val (createdGenre) = getGenreById(createDefaultGenre().toLong())

        given().contentType(ContentType.JSON)
                .queryParam("name", createdGenre.name)
                .get(genresUrl)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("data.list[0].name", CoreMatchers.equalTo(createdGenre.name))
    }

    @Test
    fun testGetGenreByIdThatDoesNotExist() {

        assertEquals(0, getGenreCount())
        val randomId = 1001

        given().contentType(ContentType.JSON)
                .get("$genresUrl/$randomId")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
    }

    @Test
    fun testPatchGenre() {
        val (createdGenre, etag) = getGenreById(createDefaultGenre().toLong())

        val name = createdGenre.name + " test"

        given().contentType("application/merge-patch+json")
                .header("If-Match", etag)
                .body("{\"name\": \"$name\"}")
                .patch("$genresUrl/${createdGenre.id}")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())

        val (patchedGenre) = getGenreById(createdGenre.id!!.toLong())

        assertEquals(name, patchedGenre.name)
    }

    @Test
    fun testBadJsonFormat() {
        val (response, etag) = getGenreById(createDefaultGenre().toLong())
        given().contentType("application/merge-patch+json")
                .header("If-Match", etag)
                .body("{name: \"action\"}")
                .patch("$genresUrl/${response.id}")
                .then()
                .statusCode(400)
    }

    @Test
    fun testPatchContainingId() {
        val (response, etag) = getGenreById(createDefaultGenre().toLong())
        given().contentType("application/merge-patch+json")
                .header("If-Match", etag)
                .body("{\"id\": \"${response.id}\",\"name\": \"name\"}")
                .patch("$genresUrl/${response.id}")
                .then()
                .statusCode(400)
    }

    @Test
    fun testPatchContainingMovie() {
        val (response, etag) = getGenreById(createDefaultGenre().toLong())
        given().contentType("application/merge-patch+json")
                .header("If-Match", etag)
                .body("{\"movies\": \"movie1\"}")
                .patch("$genresUrl/${response.id}")
                .then()
                .statusCode(400)
    }

    @Test
    fun testPatchWrongId() {
        val (response, etag) = getGenreById(createDefaultGenre().toLong())
        val name = "test"
        given().contentType("application/merge-patch+json")
                .header("If-Match", etag)
                .body("{\"name\": \"$name\"}")
                .patch("$genresUrl/7777")
                .then()
                .statusCode(404)
    }

    @Test
    fun testPatchGenreWithWrongNameFormat() {
        val (response, etag) = getGenreById(createDefaultGenre().toLong())

        given().contentType("application/merge-patch+json")
                .header("If-Match", etag)
                .body("{\"name\": 1234}")
                .patch("$genresUrl/${response.id}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
    }

    @Test
    fun testPatchWithoutNameInBody() {
        val (response, etag) = getGenreById(createDefaultGenre().toLong())

        given().contentType("application/merge-patch+json")
                .header("If-Match", etag)
                .body("{\"abc\": 1234}")
                .patch("$genresUrl/${response.id}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
    }

    @Test
    fun testPutGenreContainingMovie() {
        val (createdGenre, etag) = getGenreById(createDefaultGenre().toLong())

        createdGenre.name = createdGenre.name + " test"
        val id = createdGenre.id!!

        given().contentType(ContentType.JSON)
                .header("If-Match", etag)
                .body(createdGenre)
                .put("$genresUrl/$id")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
    }

    @Test
    fun testPutGenreWithNoIdInBody() {

        val (createdGenre, etag) = getGenreById(createDefaultGenre().toLong())

        given().contentType(ContentType.JSON)
                .header("If-Match", etag)
                .body(createDefaultGenreDto())
                .put("$genresUrl/${createdGenre.id}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
    }

    @Test
    fun testPutWithNotMatchingIds() {
        val (createdGenre, etag) = getGenreById(createDefaultGenre().toLong())

        val dto = createDefaultGenreDto()
        dto.id = (createdGenre.id!!.toLong() + 1).toString()

        given().contentType(ContentType.JSON)
                .header("If-Match", etag)
                .body(dto)
                .put("$genresUrl/${createdGenre.id}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
    }

    @Test
    fun testPutGenre() {
        val (createdGenre, etag) = getGenreById(createDefaultGenre().toLong())

        createdGenre.name = createdGenre.name + " test"
        createdGenre.movies = null

        val id = createdGenre.id!!

        given().contentType(ContentType.JSON)
                .header("If-Match", etag)
                .body(createdGenre)
                .put("$genresUrl/$id")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())

        val (updatedGenre) = getGenreById(createdGenre.id!!.toLong())

        assertEquals(createdGenre.name, updatedGenre.name)
    }

    @Test
    fun testDeleteGenreThatDoesNotExist() {
        val id = createDefaultGenre().toLong()

        given()
                .delete("$genresUrl/$id")
                .then()
                .statusCode(HttpStatus.OK.value())

        given()
                .delete("$genresUrl/$id")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())

    }

    private fun getGenreById(id: Long): Pair<GenreDto, String> {
        val response = given().contentType(ContentType.JSON)
                .get("$genresUrl/$id")
                .then()
                .statusCode(HttpStatus.OK.value())

        val dto = response.
                extract()
                .`as`(GenreResponse::class.java).data!!.list.first()

        assertNotNull(response)
        assertEquals(id, dto.id!!.toLong())

        return Pair(dto, response.extract().header("ETag"))
    }


}