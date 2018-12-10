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

        val username = createUniqueId()
        val password = createUniqueId()
        val cookie = testRegisterUser(username, password, null)

        given().get("/auth-service/user")
                .then()
                .statusCode(401)

        given().cookie("SESSION", cookie)
                .get("/auth-service/user")
                .then()
                .statusCode(200)
                .body("name", CoreMatchers.equalTo(username))
                .body("roles", Matchers.contains("ROLE_USER"))


        /*
            Trying to access with userId/password will reset
            the SESSION token.
         */
        val basic = given().auth().basic(username, password)
                .get("/auth-service/user")
                .then()
                .statusCode(200)
                .cookie("SESSION") // new SESSION cookie
                .body("name", CoreMatchers.equalTo(username))
                .body("roles", Matchers.contains("ROLE_USER"))
                .extract().cookie("SESSION")

        Assert.assertNotEquals(basic, cookie)
        checkAuthenticatedCookie(basic, 200)

        /*
            Same with /login
         */
        val login = given().contentType(ContentType.JSON)
                .body(AuthenticationDto(username, password))
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
                .body(AuthenticationDto("hgfd", "hgfds"))
                .post("/auth-service/login")
                .then()
                .statusCode(400)
                .extract().cookie("SESSION")

        checkAuthenticatedCookie(noAuth, 401)

        val username = createUniqueId()
        val password = createUniqueId()
        testRegisterUser(username, password, null)

        val auth = given().contentType(ContentType.JSON)
                .body(AuthenticationDto(username, password))
                .post("/auth-service/login")
                .then()
                .statusCode(204)
                .extract().cookie("SESSION")

        checkAuthenticatedCookie(auth, 200)
    }


    /**
     * Since AMQP sends all information except from password to user-service we can check if it is saved in user-service too
     */
    @Test
    fun testIfUserDetailsIsSavedInUserService() {

        val password = createUniqueId()
        val username = createUniqueId()
        val cookie = testRegisterUser(username, password, null)

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