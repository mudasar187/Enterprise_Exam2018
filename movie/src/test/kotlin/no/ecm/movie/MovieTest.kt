package no.ecm.movie

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import no.ecm.utils.dto.movie.GenreDto
import no.ecm.utils.response.MovieResponse
import no.ecm.utils.dto.movie.MovieDto
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.springframework.http.HttpStatus

class MovieTest: TestBase() {

    @Test
    fun testCreateMovie() {
        createDefaultMovie()
    }

    @Test
    fun testCreateMovieWithManualId() {
        val movieDto = createDefaultMovieDto(mutableSetOf(GenreDto(id = createDefaultGenre())))
        movieDto.id = (123).toString()

        createMovieInvalidOrMissingParameter(movieDto)
    }

    @Test
    fun testCreateMovieWithMissingTitle() {
        val movieDto = createDefaultMovieDto(null)
        movieDto.title = null

        createMovieInvalidOrMissingParameter(movieDto)
    }

    @Test
    fun createMovieWithMissingPosterUrl() {
        val movieDto = createDefaultMovieDto(null)
        movieDto.posterUrl = null

        createMovieInvalidOrMissingParameter(movieDto)
    }

    @Test
    fun testCreateMovieWithMissingAgeLimit() {
        val movieDto = createDefaultMovieDto(null)
        movieDto.ageLimit = null

        createMovieInvalidOrMissingParameter(movieDto)
    }

    @Test
    fun testCreateDuplicateMovies() {
        createDefaultMovie()

        given().auth().basic("admin", "admin").contentType(ContentType.JSON)
                .body(createDefaultMovieDto(mutableSetOf(createDefaultGenreDto())))
                .post(moviesUrl)
                .then()
                .statusCode(HttpStatus.CONFLICT.value())
    }

    @Test
    fun testCreateMovieWithNoGenre() {
        createMovieInvalidOrMissingParameter(movieDto = createDefaultMovieDto(null))

        createMovieInvalidOrMissingParameter(movieDto = createDefaultMovieDto(mutableSetOf()))
    }

    @Test
    fun testCreateMovieWithNonExistingGenre() {
        var id = createDefaultGenre().toLong()
        id += 112

        val movieDto = createDefaultMovieDto(mutableSetOf(GenreDto(id = id.toString())))

        given().auth().basic("admin", "admin").contentType(ContentType.JSON)
                .body(movieDto)
                .post(moviesUrl)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
    }

    @Test
    fun testGetNonExistingMovie() {
        val id = createDefaultMovie()

        given().auth().basic("admin", "admin").contentType(ContentType.JSON)
                .get("$moviesUrl/${id}435")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
    }

    @Test
    fun testGetMoviesByTitle() {
        val (movie) = getMovieById(createDefaultMovie())

        given().auth().basic("admin", "admin").contentType(ContentType.JSON)
                .queryParam("title", movie.title)
                .get(moviesUrl)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("data.list[0].title", CoreMatchers.equalTo(movie.title))
    }

    @Test
    fun testGetMovieByAgeLimit() {
        val (movie) = getMovieById(createDefaultMovie())

        given().auth().basic("admin", "admin").contentType(ContentType.JSON)
                .queryParam("ageLimit", movie.ageLimit)
                .get(moviesUrl)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("data.list.size()", CoreMatchers.not(0))
    }

    @Test
    fun testGetMoviesWithBothFilters() {
        val (movie) = getMovieById(createDefaultMovie())

        given().auth().basic("admin", "admin").contentType(ContentType.JSON)
                .queryParam("ageLimit", movie.ageLimit)
                .queryParam("title", movie.title)
                .get(moviesUrl)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
    }

    @Test
    fun cachingTest() {

        val etag =
                given().auth().basic("admin", "admin")
                        .accept(ContentType.JSON)
                        .get(moviesUrl)
                        .then()
                        .statusCode(200)
                        .header("ETag", CoreMatchers.notNullValue())
                        .extract().header("ETag")

        given().auth().basic("admin", "admin")
                .accept(ContentType.JSON)
                .header("If-None-Match", etag)
                .get(moviesUrl)
                .then()
                .statusCode(304)
                .content(CoreMatchers.equalTo(""))
    }

    @Test
    fun testPatchMovieTitle() {
        val (defaultMovie, etag) = getMovieById(createDefaultMovie())

        val title = defaultMovie.title + " test"
        patchMovie("title", title, defaultMovie.id!!, etag)

        val (patchedMovie, etag2) = getMovieById(defaultMovie.id!!)
        Assert.assertEquals(title, patchedMovie.title)
        assertNotEquals(etag, etag2)

        //Null value for title
        given().auth().basic("admin", "admin").contentType("application/merge-patch+json")
                .header("If-Match", etag2)
                .body("{\"title\": null}")
                .patch("$moviesUrl/${defaultMovie.id}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
    }

    @Test
    fun testPatchMoviePosterUrl() {
        val (defaultMovie, etag) = getMovieById(createDefaultMovie())

        val posterUrl = defaultMovie.posterUrl + ".test"
        patchMovie("posterUrl", posterUrl, defaultMovie.id!!, etag)

        val (patchedMovie, etag2) = getMovieById(defaultMovie.id!!)
        Assert.assertEquals(posterUrl, patchedMovie.posterUrl)

        //Null value for title
        given().auth().basic("admin", "admin").contentType("application/merge-patch+json")
                .header("If-Match", etag2)
                .body("{\"posterUrl\": null}")
                .patch("$moviesUrl/${defaultMovie.id}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
    }

    @Test
    fun testPatchMovieAgeLimit() {
        val (defaultMovie, etag) = getMovieById(createDefaultMovie())

        val ageLimit = defaultMovie.ageLimit!! + 3

        given().auth().basic("admin", "admin").contentType("application/merge-patch+json")
                .header("If-Match", etag)
                .body("{\"ageLimit\": $ageLimit}")
                .patch("$moviesUrl/${defaultMovie.id}")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())

        val (patchedMovie, etag2) = getMovieById(defaultMovie.id!!)
        Assert.assertEquals(ageLimit, patchedMovie.ageLimit)

        //Wrong type of field (expected int, god string)
        given().auth().basic("admin", "admin").contentType("application/merge-patch+json")
                .header("If-Match", etag2)
                .body("{\"ageLimit\": \"30\"}")
                .patch("$moviesUrl/${defaultMovie.id}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
    }

    @Test
    fun testPatchMovieDuration() {
        val (defaultMovie, etag) = getMovieById(createDefaultMovie())

        val movieDuration = defaultMovie.movieDuration!! + 30

        given().auth().basic("admin", "admin").contentType("application/merge-patch+json")
                .header("If-Match", etag)
                .body("{\"movieDuration\": $movieDuration}")
                .patch("$moviesUrl/${defaultMovie.id}")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())

        val (patchedMovie, etag2) = getMovieById(defaultMovie.id!!)
        Assert.assertEquals(movieDuration, patchedMovie.movieDuration)

        //Wrong type of field (expected int, god string)
        given().auth().basic("admin", "admin").contentType("application/merge-patch+json")
                .header("If-Match", etag2)
                .body("{\"movieDuration\": \"30\"}")
                .patch("$moviesUrl/${defaultMovie.id}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
    }

    @Test
    fun testPatchGenre() {
        val (defaultMovie, etag) = getMovieById(createDefaultMovie())

        val genreName = createDefaultGenreDto().name!!.capitalize() + " test"
        val genreId = createGenre(GenreDto(name = genreName))

        given().auth().basic("admin", "admin").contentType("application/merge-patch+json")
                .header("If-Match", etag)
                .body("{\"genre\": [{\"id\" : \"$genreId\"}]}")
                .patch("$moviesUrl/${defaultMovie.id}")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())


        val (patchedMovie, etag2) = getMovieById(defaultMovie.id!!)
        Assert.assertEquals(genreName, patchedMovie.genre!!.first().name)

        //Non existing genre
        given().auth().basic("admin", "admin").contentType("application/merge-patch+json")
                .header("If-Match", etag2)
                .body("{\"genre\": [{\"id\" : \"${defaultMovie.genre!!.first().id + "2"}\"}]}")
                .patch("$moviesUrl/${defaultMovie.id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
    }

    @Test
    fun testPatchMovieNoIfMatchHeader() {
        val (defaultMovie, etag) = getMovieById(createDefaultMovie())

        given().auth().basic("admin", "admin").contentType("application/merge-patch+json")
                .body("{\"title\": \"test\"}")
                .patch("$moviesUrl/${defaultMovie.id}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
    }

    @Test
    fun testPatchWithNotMatchingEtag() {
        val (defaultMovie, etag) = getMovieById(createDefaultMovie())

        given().auth().basic("admin", "admin").contentType("application/merge-patch+json")
                .header("If-Match", "${etag.replace("\"", "")}123")
                .body("{\"title\": \"test\"}")
                .patch("$moviesUrl/${defaultMovie.id}")
                .then()
                .statusCode(HttpStatus.PRECONDITION_FAILED.value())
    }

    @Test
    fun testFailingPatchRequestsBadFormat() {
        val (defaultMovie, etag) = getMovieById(createDefaultMovie())

        //Non existing movie
        given().auth().basic("admin", "admin").contentType("application/merge-patch+json")
                .header("If-Match", etag)
                .body("{\"movieDuration\": \"30\"}")
                .patch("$moviesUrl/${defaultMovie.id + "3"}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())

        //Bad JSON
        given().auth().basic("admin", "admin").contentType("application/merge-patch+json")
                .header("If-Match", etag)
                .body("{movieDuration\": \"30\"")
                .patch("$moviesUrl/${defaultMovie.id}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())

        //Non existing movie
        given().auth().basic("admin", "admin").contentType("application/merge-patch+json")
                .header("If-Match", etag)
                .body("{\"genre\": {\"id\" : \"${defaultMovie.genre!!.first().id}\"}}")
                .patch("$moviesUrl/${defaultMovie.id}23")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())

        //Bad array format in genre field
        given().auth().basic("admin", "admin").contentType("application/merge-patch+json")
                .header("If-Match", etag)
                .body("{\"genre\": {\"id\" : \"${defaultMovie.genre!!.first().id}\"}}")
                .patch("$moviesUrl/${defaultMovie.id}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())

        //Containing illegal field: id
        given().auth().basic("admin", "admin").contentType("application/merge-patch+json")
                .header("If-Match", etag)
                .body("{\"id\": \"3\"}")
                .patch("$moviesUrl/${defaultMovie.id}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
    }

    @Test
    fun testMoviesCache() {
        testCache(moviesUrl)
    }

    @Test
    fun testMovieCache() {
        val id = createDefaultMovie()
        testCache("$moviesUrl/$id")
    }

    @Test
    fun testPutMovie() {
        val (defaultMovie, etag) = getMovieById(createDefaultMovie())

        defaultMovie.title = defaultMovie.title + " test"

        val id = createGenre(GenreDto(name = "Action"))
        defaultMovie.genre = mutableSetOf(GenreDto(id = id))
        defaultMovie.ageLimit = defaultMovie.ageLimit!! + 1

        defaultMovie.posterUrl = defaultMovie.posterUrl + ".com"

        defaultMovie.movieDuration = defaultMovie.movieDuration!! + 23

        given().auth().basic("admin", "admin").contentType(ContentType.JSON)
                .header("If-Match", etag)
                .body(defaultMovie)
                .put("$moviesUrl/${defaultMovie.id}")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())

        val (res) = getMovieById(defaultMovie.id.toString())

        assertEquals(defaultMovie.ageLimit, res.ageLimit)
        assertEquals(defaultMovie.posterUrl, res.posterUrl)
        assertEquals(defaultMovie.title, res.title)
    }

    @Test
    fun testPutNotMatchingId() {
        val (defaultMovie, etag) = getMovieById(createDefaultMovie())

        defaultMovie.id = defaultMovie.id  + "123"

        given().auth().basic("admin", "admin").contentType(ContentType.JSON)
                .header("If-Match", etag)
                .body(defaultMovie)
                .put("$moviesUrl/${defaultMovie.id}")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())

    }

    @Test
    fun testPutMovieNullGenre() {
        val (defaultMovie, etag) = getMovieById(createDefaultMovie())

        defaultMovie.genre = null

        given().auth().basic("admin", "admin").contentType(ContentType.JSON)
                .header("If-Match", etag)
                .body(defaultMovie)
                .put("$moviesUrl/${defaultMovie.id}")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
    }

    @Test
    fun testDoubleDelete() {

        val id = createDefaultMovie()

        given().auth().basic("admin", "admin").contentType(ContentType.JSON)
                .delete("$moviesUrl/$id")
                .then()
                .statusCode(HttpStatus.OK.value())

        given().auth().basic("admin", "admin").contentType(ContentType.JSON)
                .delete("$moviesUrl/$id")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
    }

    private fun createDefaultMovie() : String {
        val genreId = createDefaultGenre()
        return createMovie(
                createDefaultMovieDto(mutableSetOf(GenreDto(genreId)))
        )
    }

    private fun getMovieById(id: String) : Pair<MovieDto, String> {
        val response = given().auth().basic("admin", "admin").contentType(ContentType.JSON)
                .get("$moviesUrl/$id")
                .then()
                .statusCode(HttpStatus.OK.value())

        val dto = response
                .extract()
                .`as`(MovieResponse::class.java).data!!.list.first()

        Assert.assertNotNull(response)
        assertEquals(id.toLong(), dto.id!!.toLong())

        return Pair(dto, response.extract().header("ETag"))

    }

    private fun createMovieInvalidOrMissingParameter(movieDto: MovieDto){
        given().auth().basic("admin", "admin").contentType(ContentType.JSON)
                .body(movieDto)
                .post(moviesUrl)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
    }

    private fun patchMovie(field: String, value: String, id: String, etag: String){
        given().auth().basic("admin", "admin").contentType("application/merge-patch+json")
                .header("If-Match", etag)
                .body("{\"$field\": \"$value\"}")
                .patch("$moviesUrl/$id")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
    }
}