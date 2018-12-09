package no.ecm.movie

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.client.WireMock.urlMatching
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import io.restassured.RestAssured
import io.restassured.RestAssured.*
import io.restassured.http.ContentType
import io.restassured.response.Response
import no.ecm.utils.response.GenreResponse
import no.ecm.utils.response.MovieResponse
import no.ecm.utils.dto.movie.GenreDto
import no.ecm.utils.dto.movie.MovieDto
import no.ecm.utils.dto.movie.NowPlayingDto
import no.ecm.utils.response.NowPlayingReponse
import org.hamcrest.CoreMatchers
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
abstract class TestBase {

    @LocalServerPort
    protected var port = 0

    val genresUrl = "/genres"
    val moviesUrl = "/movies"
    val nowPlayingURL = "/now-playings"
    
    companion object {
    	private lateinit var wireMockServer: WireMockServer
        
        @BeforeClass @JvmStatic
        fun initWireMock() {
            
            wireMockServer = WireMockServer(wireMockConfig().port(7086))
            wireMockServer.start()
        }
        
        @AfterClass @JvmStatic
        fun tearDown() {
            wireMockServer.stop()
        }
    }

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
        cleanNowPlaying()
    }

    private fun cleanNowPlaying() {

        val response = given().accept(ContentType.JSON)
                .param("limit", 100)
                .get(nowPlayingURL)
                .then()
                .statusCode(200)
                .extract()
                .`as`(NowPlayingReponse::class.java)

        response.data!!.list.forEach {
            given()
                    .auth().basic("admin", "admin")
                    .delete("$nowPlayingURL/${it.id}")
                    .then()
                    .statusCode(200) }

        assertEquals(0, nowPlayingCount())
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
                .auth().basic("admin", "admin")
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
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
                .auth().basic("admin", "admin")
                .delete("$moviesUrl/${ it.id }")
                .then()
                .statusCode(200) }

        assertEquals(0, getMovieCount())
    }

    fun nowPlayingCount(): Int {
        return given().accept(ContentType.JSON)
                .get(nowPlayingURL)
                .then()
                .statusCode(200)
                .extract()
                .jsonPath().getInt("data.totalSize")
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
    
    fun createNowPlaying(nowPlayingDto: NowPlayingDto): String {
        val response = given()
            .auth().basic("admin", "admin")
            .contentType(ContentType.JSON)
            .body(nowPlayingDto)
            .post(nowPlayingURL)
            .then()
            .statusCode(201)
            .extract()
            .`as`(NowPlayingReponse::class.java)
    
        Assert.assertNotNull(response.data)
        assertEquals(1, response.data!!.list.size)
        Assert.assertNotNull(response.data!!.list.first().id!!)
    
        return response.data!!.list.first().id!!
    }
    
    fun getEtagFromNowPlayingId(id: String): String {
        return RestAssured.given().accept(ContentType.JSON)
            .get("$nowPlayingURL/$id")
            .then()
            .extract().header("ETag")
    }

    fun createNowPlayingWithoutChecks(nowPlayingDto: NowPlayingDto): Response? {
        return given().contentType(ContentType.JSON)
                .auth().basic("admin", "admin")
                .body(nowPlayingDto)
                .post(nowPlayingURL)
    }

    fun createGenre(genreDto: GenreDto): String {
        val response = given().contentType(ContentType.JSON)
                .auth().basic("admin", "admin")
                .body(genreDto)
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

    fun createDefaultGenre() : String {
        return createGenre(createDefaultGenreDto())
    }
    
    fun createDefaultNowPlayingDto(movieId: String): NowPlayingDto {
        return NowPlayingDto(
            cinemaId = "1",
            roomId = "4",
            movieDto = MovieDto(id = movieId),
            time = "2018-12-20 20:00:00")
    }
    
    

    fun createDefaultGenreDto() : GenreDto {
        return GenreDto(
                name = "horror")
    }

    fun createDefaultMovieDto(genreDtos: MutableSet<GenreDto>?): MovieDto {
        return MovieDto(
                title = "My Movie Title",
                posterUrl = "url.com",
                movieDuration = 120,
                ageLimit = 10,
                genre = genreDtos
        )
    }

    fun createMovie(movieDto: MovieDto) : String {
        return given().contentType(ContentType.JSON)
                .auth().basic("admin", "admin")
                .body(movieDto)
                .post(moviesUrl)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .`as`(MovieResponse::class.java).data!!.list.first().id!!
    }

    fun testCache(url: String) {
        val etag =
                given()
                        .accept(ContentType.JSON)
                        .get(url)
                        .then()
                        .statusCode(200)
                        .header("ETag", CoreMatchers.notNullValue())
                        .extract().header("ETag")

        given()
                .accept(ContentType.JSON)
                .header("If-None-Match", etag)
                .get(url)
                .then()
                .statusCode(304)
                .content(CoreMatchers.equalTo(""))
    }
    
    /*
        MOCKING
     */
    
    protected fun getAMockRoomResponse(cinemaId: Int, roomId: Int) : String {
        return """
			{
              "code": 200,
              "message": null,
              "status": "SUCCESS",
              "data": {
                "list": [
                  {
                    "id": "$roomId",
                    "name": "Sal 1",
                    "seats": [
                      "A1",
                      "A2",
                      "A3",
                      "A4",
                      "A5",
                      "A6",
                      "A7",
                      "A8",
                      "A9",
                      "A10",
                      "A11",
                      "A12",
                      "A13",
                      "A14",
                      "E11",
                      "E10",
                      "E13",
                      "E12",
                      "E14",
                      "B1",
                      "B2",
                      "B3",
                      "B4",
                      "B5",
                      "B6",
                      "B7",
                      "B8",
                      "B9",
                      "C1",
                      "C2",
                      "C3",
                      "C4",
                      "C5",
                      "C6",
                      "C7",
                      "C8",
                      "C9",
                      "D10",
                      "D12",
                      "D11",
                      "D14",
                      "D13",
                      "D1",
                      "D2",
                      "D3",
                      "D4",
                      "D5",
                      "D6",
                      "D7",
                      "D8",
                      "D9",
                      "E1",
                      "E2",
                      "E3",
                      "E4",
                      "E5",
                      "E6",
                      "E7",
                      "E8",
                      "E9",
                      "C11",
                      "C10",
                      "C13",
                      "C12",
                      "G11",
                      "C14",
                      "G10",
                      "G13",
                      "G12",
                      "F1",
                      "G14",
                      "F2",
                      "F3",
                      "F4",
                      "F5",
                      "F6",
                      "F7",
                      "F8",
                      "F9",
                      "G1",
                      "G2",
                      "G3",
                      "G4",
                      "G5",
                      "G6",
                      "G7",
                      "G8",
                      "G9",
                      "B10",
                      "B11",
                      "B12",
                      "B13",
                      "F10",
                      "B14",
                      "F12",
                      "F11",
                      "F14",
                      "F13"
                    ],
                    "cinemaId": "$cinemaId"
                  }
                ],
                "rangeMin": 0,
                "rangeMax": 0,
                "totalSize": 1,
                "_links": {}
              }
            }
		""".trimIndent()
    }
    
    protected fun stubJsonResponse(json: String) {
        wireMockServer.stubFor(
            get(urlMatching("/cinemas.*/rooms.*"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json; charset=utf-8")
                        .withHeader("Content-Length", "" + json.toByteArray(charset("utf-8")).size)
                        .withBody(json)
                )
        )


    }

    protected fun stubFailJsonResponse() {


        wireMockServer.stubFor(
                get(urlMatching("/cinemas.*/rooms.*"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withStatus(500)
                                .withStatusMessage("Internal Server Error")
                                .withBody("""
                                    {
                                        "code": 500,
                                        "message": "Internal Server Error",
                                        "data": null,
                                        "status": "FAIL"
                                    }
                                """.trimIndent())
                        )
        )

    }
    
}