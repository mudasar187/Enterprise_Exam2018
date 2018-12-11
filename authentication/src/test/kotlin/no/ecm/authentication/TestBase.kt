package no.ecm.authentication

import io.restassured.RestAssured
import io.restassured.http.ContentType
import no.ecm.utils.dto.auth.RegistrationDto
import no.ecm.utils.dto.user.UserDto
import org.hamcrest.CoreMatchers

abstract class TestBase {

    private var counter = System.currentTimeMillis()

    fun testRegisterAsUserOrAdmin(id: String, password: String, adminCode: String?): String {

        val sessionCookie = RestAssured.given().contentType(ContentType.JSON)
                .body(RegistrationDto(password, adminCode, UserDto(id)))
                .post("/signup")
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
        RestAssured.given().cookie("SESSION", cookie)
                .get("/user")
                .then()
                .statusCode(expectedCode)
    }

    fun createUniqueId(): String {
        counter++
        return "foo_$counter"
    }
}