package no.ecm.e2etest

import io.restassured.RestAssured.*
import io.restassured.http.ContentType
import no.ecm.utils.dto.auth.AuthenticationDto
import org.awaitility.Awaitility.await
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers
import org.junit.*
import org.testcontainers.containers.DockerComposeContainer
import java.io.File
import java.util.concurrent.TimeUnit


class AuthenticationIT: TestBase() {

    val name = "testName"
    val password = "testPassword"

    companion object {

        class KDockerComposeContainer(path: File) : DockerComposeContainer<KDockerComposeContainer>(path)


        @ClassRule
        @JvmField
        val env = KDockerComposeContainer(File("../docker-compose.yml"))
                .withLocalCompose(true)

        @BeforeClass
        @JvmStatic
        fun initialize() {
            baseURI = "http://localhost"
            port = 10000
            enableLoggingOfRequestAndResponseIfValidationFails()

            await().atMost(300, TimeUnit.SECONDS)
                    .pollInterval(3, TimeUnit.SECONDS)
                    .ignoreExceptions()
                    .until {
                        given().get("http://localhost:10000/auth-service/user").then().statusCode(401)
                        true
                    }
        }
    }


    @Test
    fun testUnauthorizedAccess() {

        given().get("/auth-service/user")
                .then()
                .statusCode(401)
    }

    @Test
    fun testLoginWithUserRole() {

        checkAuthenticatedCookie("invalid cookie", 401)

        val id = createUniqueId()
        val cookie = testRegisterUser(id, password)

        given().get("/auth-service/user")
                .then()
                .statusCode(401)

        given().cookie("SESSION", cookie)
                .get("/auth-service/user")
                .then()
                .statusCode(200)
                .body("name", CoreMatchers.equalTo(id))
                .body("roles", Matchers.contains("ROLE_USER"))


        /*
            Trying to access with userId/password will reset
            the SESSION token.
         */
        val basic = given().auth().basic(id, password)
                .get("/auth-service/user")
                .then()
                .statusCode(200)
                .cookie("SESSION") // new SESSION cookie
                .body("name", CoreMatchers.equalTo(id))
                .body("roles", Matchers.contains("ROLE_USER"))
                .extract().cookie("SESSION")

        Assert.assertNotEquals(basic, cookie)
        checkAuthenticatedCookie(basic, 200)

        /*
            Same with /login
         */
        val login = given().contentType(ContentType.JSON)
                .body(AuthenticationDto(id, password))
                .post("/auth-service/login")
                .then()
                .statusCode(204)
                .cookie("SESSION") // new SESSION cookie
                .extract().cookie("SESSION")

        Assert.assertNotEquals(login, cookie)
        Assert.assertNotEquals(login, basic)
        checkAuthenticatedCookie(login, 200)
    }

    @Test
    fun testWrongLogin() {

        val noAuth = given().contentType(ContentType.JSON)
                .body(AuthenticationDto(name, password))
                .post("/auth-service/login")
                .then()
                .statusCode(400)
                .extract().cookie("SESSION")

        checkAuthenticatedCookie(noAuth, 401)

        testRegisterUser(name, password)

        val auth = given().contentType(ContentType.JSON)
                .body(AuthenticationDto(name, password))
                .post("/auth-service/login")
                .then()
                .statusCode(204)
                .extract().cookie("SESSION")

        checkAuthenticatedCookie(auth, 200)
    }
}