package no.ecm.e2etest

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import no.ecm.utils.converter.ConvertionHandler
import no.ecm.utils.validation.ValidationHandler
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

            Awaitility.await().atMost(500, TimeUnit.SECONDS)
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

    /**
     * Testing whole application
     */
    @Test
    fun testApplicationEndPoints() {

        val username = createUniqueId()
        val password = createUniqueId()
        // create a admin with providing a secret key
        val adminCookie = testRegisterUser(username, password, "2y12wePwvk5P63kb8XqlvXcWeqpW6cNdbY8xPn6gazUIRMhJTYuBfvW6")



        // Create a normal user to make a order
        val normalUserName = createUniqueId()
        val normalUserCookie = testRegisterUser(normalUserName, password, null)


        val cinemaName = createUniqueId()
        val cinemaLocation = createUniqueId()
        val cinemaId = createCinema(adminCookie, cinemaName, cinemaLocation)

        val roomName = createUniqueId()
        val roomId = createRoomForSpecificCinema(adminCookie, cinemaId.toString(), roomName)

        given().cookie("SESSION", adminCookie)
                .get("/cinema-service/cinemas/$cinemaId")
                .then()
                .statusCode(200)
                .body("data.list[0].id", CoreMatchers.equalTo("$cinemaId"))
                .body("data.list[0].name", CoreMatchers.equalTo(cinemaName))
                .body("data.list[0].location", CoreMatchers.equalTo(cinemaLocation))

        given().cookie("SESSION", adminCookie)
                .get("/cinema-service/cinemas/$cinemaId/rooms/$roomId")
                .then()
                .statusCode(200)
                .body("data.list[0].id", CoreMatchers.equalTo("$roomId"))
                .body("data.list[0].name", CoreMatchers.equalTo(roomName))
                .body("data.list[0].seats[0]", CoreMatchers.equalTo("A1"))
                .body("data.list[0].seats[1]", CoreMatchers.equalTo("A2"))

        val randomGenre = createUniqueId()
        val genreId = createGenre(adminCookie, randomGenre)

        val randomMovieTitle = createUniqueId()
        val movieId = createMovie(adminCookie, createDefaultMovieDto(genreId, randomMovieTitle))

        given().cookie("SESSION", adminCookie)
                .get("/movie-service/genres/$genreId")
                .then()
                .statusCode(200)
                .body("data.list[0].id", CoreMatchers.equalTo("$genreId"))
                .body("data.list[0].name", CoreMatchers.equalTo("${randomGenre.capitalize()}"))

        given().cookie("SESSION", adminCookie)
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


//        val time = "2018-12-01 20:00:00"
//        val convertedTime = ConvertionHandler.convertTimeStampToZonedTimeDate("$time.000000")
//        val nowPlayingId = createNowPlaying(adminCookie, "$cinemaId", "$roomId", "$movieId")

//        given().cookie("SESSION", adminCookie)
//                .get("/movie-service/now-playings/$nowPlayingId")
//                .then()
//                .statusCode(200)
//                .body("data.list[0].id", CoreMatchers.equalTo("$nowPlayingId"))
//                .body("data.list[0].time", CoreMatchers.equalTo("2018-12-28 18:00:00"))
//                .body("data.list[0].roomId", CoreMatchers.equalTo("$roomId"))
//                .body("data.list[0].seats", CoreMatchers.notNullValue())
//                .body("data.list[0].cinemaId", CoreMatchers.equalTo("$cinemaId"))
//                .body("data.list[0].movieDto.title", CoreMatchers.equalTo("$randomMovieTitle"))
//                .body("data.list[0].movieDto.movieDuration", CoreMatchers.equalTo(120))
//                .body("data.list[0].movieDto.ageLimit", CoreMatchers.equalTo(18))
//                .body("data.list[0].movieDto.posterUrl", CoreMatchers.equalTo("www.poster-url.com"))
//                .body("data.list[0].movieDto.genre[0].name", CoreMatchers.equalTo("$randomGenre"))
//                .body("data.list[0].movieDto.genre[0].id", CoreMatchers.equalTo("$genreId"))
//
//
//        val orderDate = "2018-12-24 20:04:15"
//        val seat = "A1"
//        val convertedOrderDate = ConvertionHandler.convertTimeStampToZonedTimeDate("$orderDate.000000")
//        val invoiceDto = createDefaultInvoiceDto(username, orderDate, "$nowPlayingId", seat)
//
//         // Normal user makes an order
//        val invoiceId = given().cookie("SESSION", normalUserCookie)
//                .body(invoiceDto)
//                .post("/order-service/invoices")
//                .then()
//                .statusCode(201)
//                .extract()
//                .jsonPath().getLong("data.list[0].id")
//
//
//        // Admin retrive all orders made by normal users
//        given().contentType(ContentType.JSON)
//                .cookie("SESSION", adminCookie)
//                .queryParam("username", normalUserName)
//                .get("/order-service/invoices")
//                .then()
//                .statusCode(200)
//                .body("data.list[0].nowPlayingId", CoreMatchers.equalTo("$nowPlayingId"))
//                .body("data.list[0].id", CoreMatchers.equalTo("$invoiceId"))
//                .body("data.list[0].orderDate", CoreMatchers.equalTo("$convertedOrderDate"))
//                .body("data.list[0].username", CoreMatchers.equalTo("$normalUserName"))
//                .body("data.list[0].tickets[0].seat", CoreMatchers.equalTo("$seat"))
//                .body("data.list[0].tickets[0].invoiceId", CoreMatchers.equalTo("$invoiceId"))
//                .body("data.list[0].tickets[0].price", CoreMatchers.equalTo(100.0))

    }
}