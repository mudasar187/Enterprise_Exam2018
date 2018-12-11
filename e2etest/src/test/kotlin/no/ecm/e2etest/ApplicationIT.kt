package no.ecm.e2etest

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import org.awaitility.Awaitility
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers
import org.junit.Assume
import org.junit.BeforeClass
import org.junit.ClassRule
import org.junit.Test
import org.testcontainers.containers.DockerComposeContainer
import java.io.File
import java.util.concurrent.TimeUnit

class ApplicationIT: TestBase() {

    companion object {

        @BeforeClass
        @JvmStatic
        fun checkEnvironment(){

            /*
                Looks like currently some issues in running Docker-Compose on Travis
             */

            val travis = System.getProperty("TRAVIS") != null
            Assume.assumeTrue(!travis)
        }

        class KDockerComposeContainer(path: File) : DockerComposeContainer<KDockerComposeContainer>(path)


        @ClassRule
        @JvmField
        val env = KDockerComposeContainer(File("../docker-compose.yml"))
                .withLocalCompose(true)

        @BeforeClass
        @JvmStatic
        fun initialize() {
            RestAssured.baseURI = "http://localhost"
            RestAssured.port = 10000
            RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()

            Awaitility.await().atMost(300, TimeUnit.SECONDS)
                    .pollInterval(3, TimeUnit.SECONDS)
                    .ignoreExceptions()
                    .until {
                        RestAssured.given().get("http://localhost:10000/auth-service/user").then().statusCode(401)
                        true
                    }
        }


    }

    // Create an admin to make authenticated HTTP request that are not allowed by normal user with 'ROLE_USER'
    @Test
    fun testCreateAdmin() {
        val username = createUniqueId()
        val password = createUniqueId()

        // create a admin with providing a secret key
        val cookie = testRegisterUser(username, password, "2y12wePwvk5P63kb8XqlvXcWeqpW6cNdbY8xPn6gazUIRMhJTYuBfvW6")

        given().get("/auth-service/user")
                .then()
                .statusCode(401)

        given().cookie("SESSION", cookie)
                .get("/auth-service/user")
                .then()
                .statusCode(200)
                .body("name", CoreMatchers.equalTo(username))
                .body("roles", Matchers.contains("ROLE_ADMIN"))
                .extract().body().jsonPath().prettyPrint()
    }

    @Test
    fun testCreateCinemaAndRoom() {

        val username = createUniqueId()
        val password = createUniqueId()

        // create a admin with providing a secret key
        val cookie = testRegisterUser(username, password, "2y12wePwvk5P63kb8XqlvXcWeqpW6cNdbY8xPn6gazUIRMhJTYuBfvW6")

        val cinemaName = createUniqueId()
        val cinemaLocation = createUniqueId()
        val cinemaId = createCinema(cookie, cinemaName, cinemaLocation)

        val roomName = createUniqueId()
        val roomId = createRoomForSpecificCinema(cookie, cinemaId.toString(), roomName)

        given().cookie("SESSION", cookie)
                .get("/cinema-service/cinemas/$cinemaId")
                .then()
                .statusCode(200)
                .body("data.list[0].id", CoreMatchers.equalTo("$cinemaId"))
                .body("data.list[0].name", CoreMatchers.equalTo(cinemaName))
                .body("data.list[0].location", CoreMatchers.equalTo(cinemaLocation))

        given().cookie("SESSION", cookie)
                .get("/cinema-service/cinemas/$cinemaId/rooms/$roomId")
                .then()
                .statusCode(200)
                .statusCode(200)
                .body("data.list[0].id", CoreMatchers.equalTo("$roomId"))
                .body("data.list[0].name", CoreMatchers.equalTo(roomName))
                .body("data.list[0].seats[0]", CoreMatchers.equalTo("A1"))
                .body("data.list[0].seats[1]", CoreMatchers.equalTo("A2"))
    }

    @Test
    fun testCreateGenreAndMovie() {

        val username = createUniqueId()
        val password = createUniqueId()

        // create a admin with providing a secret key
        val cookie = testRegisterUser(username, password, "2y12wePwvk5P63kb8XqlvXcWeqpW6cNdbY8xPn6gazUIRMhJTYuBfvW6")

        val randomGenre = createUniqueId()
        val genreId = createGenre(cookie, randomGenre)

        val randomMovieTitle = createUniqueId()
        val movieId = createMovie(cookie, createDefaultMovieDto(genreId, randomMovieTitle))

        given().cookie("SESSION", cookie)
                .get("/movie-service/genres/$genreId")
                .then()
                .statusCode(200)
                .body("data.list[0].id", CoreMatchers.equalTo("$genreId"))
                .body("data.list[0].name", CoreMatchers.equalTo("${randomGenre.capitalize()}"))

        given().cookie("SESSION", cookie)
                .get("/movie-service/movies/$movieId")
                .then()
                .statusCode(200)
                .body("data.list[0].id", CoreMatchers.equalTo("$movieId"))
                .body("data.list[0].title", CoreMatchers.equalTo("${randomMovieTitle.capitalize()}"))
                .body("data.list[0].posterUrl", CoreMatchers.equalTo("www.poster-url.com"))
                .body("data.list[0].movieDuration", CoreMatchers.equalTo(120))
                .body("data.list[0].ageLimit", CoreMatchers.equalTo(18))
                .body("data.list[0].genre[0].id", CoreMatchers.equalTo("$genreId"))
                .body("data.list[0].genre[0].name", CoreMatchers.equalTo("${randomGenre.capitalize()}"))

    }
}