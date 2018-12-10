package no.ecm.e2etest

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import no.ecm.utils.dto.auth.RegistrationDto
import no.ecm.utils.dto.cinema.CinemaDto
import no.ecm.utils.dto.cinema.RoomDto
import no.ecm.utils.dto.movie.GenreDto
import no.ecm.utils.dto.movie.MovieDto
import no.ecm.utils.dto.user.UserDto
import org.hamcrest.CoreMatchers
import org.springframework.http.HttpStatus

abstract class TestBase {

    val roomSeats = setOf("A1", "A2")

    private var counter = System.currentTimeMillis()

    fun testRegisterUser(username: String, password: String, secretPassword: String?): String {

        val email = createUniqueId()

        val sessionCookie = given().contentType(ContentType.JSON)
                .body(RegistrationDto(password, secretPassword, UserDto(username, "1986-02-03", "Foo Bar", email)))
                .post("/auth-service/signup")
                .then()
                .statusCode(204)
                .header("Set-Cookie", CoreMatchers.not(CoreMatchers.equalTo(null)))
                .extract().cookie("SESSION")

        /*
            From now on, the user/admin is authenticated based on USER role or ADMIN role
            I do not need to use userid/password in the following requests.
            But each further request will need to have the SESSION cookie.
         */

        return sessionCookie
    }

    fun checkAuthenticatedCookie(cookie: String, expectedCode: Int){
        given().cookie("SESSION", cookie)
                .get("/auth-service/user")
                .then()
                .statusCode(expectedCode)
    }

    fun createUniqueId(): String {
        counter++
        return "foo_$counter"
    }

    fun createCinema(cookie: String, cinemaName: String, location: String): Long {

        val cinemaDto = CinemaDto(null, cinemaName, location, null)


        return given().cookie("SESSION", cookie)
                .contentType(ContentType.JSON)
                .body(cinemaDto)
                .post("/cinema-service/cinemas")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath().getLong("data.list[0].id")
    }

    fun createRoomForSpecificCinema(cookie: String, id: String, name: String): Long {

        val dto = RoomDto(null, name, roomSeats, "$id")

        return given().cookie("SESSION", cookie)
                .contentType(ContentType.JSON)
                .body(dto)
                .post("/cinema-service/cinemas/$id/rooms")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath().getLong("data.list[0].id")
    }

    fun createGenre(cookie: String, genre: String): Long {

        val genreDto = GenreDto(null, genre)

        return given().contentType(ContentType.JSON)
                .cookie("SESSION", cookie)
                .body(genreDto)
                .post("/movie-service/genres")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .jsonPath().getLong("data.list[0].id")

    }

    fun createDefaultMovieDto(genreId: Long, movieTitle: String): MovieDto {
        return MovieDto(
                title = movieTitle,
                posterUrl = "www.poster-url.com",
                movieDuration = 120,
                ageLimit = 18,
                genre = mutableSetOf(GenreDto(genreId.toString()))
        )
    }

    fun createMovie(cookie: String, movieDto: MovieDto) : Long {


        return given().contentType(ContentType.JSON)
                .cookie("SESSION", cookie)
                .body(movieDto)
                .post("/movie-service/movies")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .jsonPath().getLong("data.list[0].id")
    }
}