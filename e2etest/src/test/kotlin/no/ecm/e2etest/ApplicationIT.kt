package no.ecm.e2etest

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import junit.framework.Assert.assertEquals
import no.ecm.utils.dto.order.TicketDto
import no.ecm.utils.response.TicketResponseDto
import no.ecm.utils.response.WrappedResponse
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

    /**
     * cinema service
     */
    @Test
    fun testCinemaService() {

        val username = createUniqueId()
        val password = createUniqueId()
        val cookie = registerUser(username, password, "2y12wePwvk5P63kb8XqlvXcWeqpW6cNdbY8xPn6gazUIRMhJTYuBfvW6")

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
                .body("data.list[0].id", CoreMatchers.equalTo("$roomId"))
                .body("data.list[0].name", CoreMatchers.equalTo(roomName))
                .body("data.list[0].seats[0]", CoreMatchers.equalTo("A1"))
                .body("data.list[0].seats[1]", CoreMatchers.equalTo("A2"))
    }

    /**
     * movie-service
     */

    @Test
    fun testMovieService() {

        val username = createUniqueId()
        val password = createUniqueId()
        val cookie = registerUser(username, password, "2y12wePwvk5P63kb8XqlvXcWeqpW6cNdbY8xPn6gazUIRMhJTYuBfvW6")

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


        given().cookie("SESSION", cookie)
                .get("/movie-service/now-playings")
                .then()
                .statusCode(200)
                .body("data.totalSize", CoreMatchers.equalTo(0))

    }


    /**
     *  Order service
     */
    @Test
    fun testOrderService() {

        val username = createUniqueId()
        val password = createUniqueId()
        val cookie = registerUser(username, password, "2y12wePwvk5P63kb8XqlvXcWeqpW6cNdbY8xPn6gazUIRMhJTYuBfvW6")

        val randomCouponCode = createUniqueId()
        val randomDescription = createUniqueId()

        val couponId = createCoupon(cookie, randomCouponCode, randomDescription, "2018-12-12 20:20:00",20)

        given().cookie("SESSION", cookie)
                .get("/order-service/coupons/$couponId")
                .then()
                .statusCode(200)
                .body("data.list[0].description", CoreMatchers.equalTo(randomDescription))
                .body("data.list[0].code", CoreMatchers.equalTo(randomCouponCode))

                // Docker dosent understand timezone
                //.body("data.list[0].expireAt", CoreMatchers.equalTo(convertedExpiredAt))

                .body("data.list[0].percentage", CoreMatchers.equalTo(20))



        val price = 200.0
        val seat = "A1"
        val invoiceId = "1"
        val ticketDto = createDefaultTicket(price, seat, invoiceId)

        val ticketId = createTicket(cookie, ticketDto)

        val result = given().cookie("SESSION", cookie)
                .get("/order-service/tickets/$ticketId")
                .then()
                .statusCode(200)
                .extract().`as`(TicketResponseDto::class.java).data!!.list.first()


                assertEquals(result.id, "$ticketId")
                assertEquals(result.price, price)
                assertEquals(result.seat, seat)
                assertEquals(result.invoiceId, invoiceId)


        // Normal user cant make a POST
        val normalUsername = createUniqueId()
        val normalUserCookie = registerUser(normalUsername, password, null)

        given().cookie("SESSION", normalUserCookie)
                .contentType(ContentType.JSON)
                .body(ticketDto)
                .post("/order-service/tickets")
                .then()
                .statusCode(403)


        // Normal user tries to get invoices by username, not allowed
//        given().cookie("SESSION", normalUserCookie)
//                .contentType(ContentType.JSON)
//                .body()

    }









    /**
     * Since AMQP sends all information except from password to user-service we can check if it is saved in user-service too
     */
    @Test
    fun testIfUserDetailsIsSavedInUserService() {

        val password = createUniqueId()
        val username = createUniqueId()
        val cookie = registerUser(username, password, null)

        val getQuery = """
			{
  				userById(id: "$username") {
    				username, email, name, dateOfBirth
  				}
			}
		""".trimIndent()

        given().cookie("SESSION", cookie)
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .queryParam("query", getQuery)
                .get("/user-service/graphql")
                .then()
                .statusCode(200)
                .body("data.userById.username", Matchers.equalTo(username))
                .body("data.userById.name", Matchers.equalTo("Foo Bar"))
                .body("data.userById.dateOfBirth", Matchers.equalTo("1986-02-03"))


    }

}