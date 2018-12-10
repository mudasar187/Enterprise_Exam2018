package no.ecm.authentication

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import no.ecm.authentication.repository.AuthenticationRepository
import no.ecm.utils.dto.auth.AuthenticationDto
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers
import org.junit.*
import org.junit.Assert.assertNotEquals
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import org.testcontainers.containers.GenericContainer

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = [(SecurityTest.Companion.Initializer::class)])
@ActiveProfiles("test")
class SecurityTest : TestBase() {

    @Autowired
    private lateinit var authenticationRepository: AuthenticationRepository

    @LocalServerPort
    private var port = 0

    @Value("\${adminCode}")
    private lateinit var adminCode: String

    val name = "foo"
    val pwd = "bar"

    companion object {

        @BeforeClass
        @JvmStatic
        fun checkEnvironment(){

            /*
                TODO
                Looks like currently some issues in running Docker-Compose on Travis
             */

            val travis = System.getProperty("TRAVIS") != null
            Assume.assumeTrue(!travis)
        }

        class KGenericContainer(imageName: String) : GenericContainer<KGenericContainer>(imageName)

        /*
            Here, going to use an actual Redis instance started in Docker
         */

        @ClassRule
        @JvmField
        val redis = KGenericContainer("redis:latest").withExposedPorts(6379)

        @ClassRule
        @JvmField
        val rabbitMQ = KGenericContainer("rabbitmq:3").withExposedPorts(5672)


        class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
            override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {

                val redisHost = redis.containerIpAddress
                val redisPort = redis.getMappedPort(6379)

                val rabbitHost = rabbitMQ.containerIpAddress
                val rabbitPort = rabbitMQ.getMappedPort(5672)



                TestPropertyValues
                        .of("spring.redis.host=$redisHost", "spring.redis.port=$redisPort",
                                "spring.rabbitmq.host=$rabbitHost", "spring.rabbitmq.port=$rabbitPort")
                        .applyTo(configurableApplicationContext.environment)

            }
        }
    }

    @Before
    fun initialize() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = port
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()

        authenticationRepository.deleteAll()
    }

    @Test
    fun testUnauthorizedAccess() {

        given().get("/user")
                .then()
                .statusCode(401)
    }

    @Test
    fun testLoginWithUserRole() {

        checkAuthenticatedCookie("invalid cookie", 401)

        val cookie = testRegisterAsUserOrAdmin(name, pwd, null)

        given().get("/user")
                .then()
                .statusCode(401)

        given().cookie("SESSION", cookie)
                .get("/user")
                .then()
                .statusCode(200)
                .body("name", CoreMatchers.equalTo(name))
                .body("roles", Matchers.contains("ROLE_USER"))


        /*
            Trying to access with userId/password will reset
            the SESSION token.
         */
        val basic = given().auth().basic(name, pwd)
                .get("/user")
                .then()
                .statusCode(200)
                .cookie("SESSION") // new SESSION cookie
                .body("name", CoreMatchers.equalTo(name))
                .body("roles", Matchers.contains("ROLE_USER"))
                .extract().cookie("SESSION")

        assertNotEquals(basic, cookie)
        checkAuthenticatedCookie(basic, 200)

        /*
            Same with /login
         */
        val login = given().contentType(ContentType.JSON)
                .body(AuthenticationDto(name, pwd))
                .post("/login")
                .then()
                .statusCode(204)
                .cookie("SESSION") // new SESSION cookie
                .extract().cookie("SESSION")

        assertNotEquals(login, cookie)
        assertNotEquals(login, basic)
        checkAuthenticatedCookie(login, 200)
    }

    @Test
    fun testLoginWithAdminRole() {

        val name = "foo"
        val pwd = "bar"

        checkAuthenticatedCookie("invalid cookie", 401)

        val cookie = testRegisterAsUserOrAdmin(name, pwd, adminCode)

        given().get("/user")
                .then()
                .statusCode(401)

        given().cookie("SESSION", cookie)
                .get("/user")
                .then()
                .statusCode(200)
                .body("name", CoreMatchers.equalTo(name))
                .body("roles", Matchers.contains("ROLE_ADMIN"))


        /*
            Trying to access with userId/password will reset
            the SESSION token.
         */
        val basic = given().auth().basic(name, pwd)
                .get("/user")
                .then()
                .statusCode(200)
                .cookie("SESSION") // new SESSION cookie
                .body("name", CoreMatchers.equalTo(name))
                .body("roles", Matchers.contains("ROLE_ADMIN"))
                .extract().cookie("SESSION")

        assertNotEquals(basic, cookie)
        checkAuthenticatedCookie(basic, 200)

        /*
            Same with /login
         */
        val login = given().contentType(ContentType.JSON)
                .body(AuthenticationDto(name, pwd))
                .post("/login")
                .then()
                .statusCode(204)
                .cookie("SESSION") // new SESSION cookie
                .extract().cookie("SESSION")

        assertNotEquals(login, cookie)
        assertNotEquals(login, basic)
        checkAuthenticatedCookie(login, 200)
    }



    @Test
    fun testWrongLogin() {

        val name = "foo"
        val pwd = "bar"

        val noAuth = given().contentType(ContentType.JSON)
                .body(AuthenticationDto(name, pwd))
                .post("/login")
                .then()
                .statusCode(400)
                .extract().cookie("SESSION")

        checkAuthenticatedCookie(noAuth, 401)

        testRegisterAsUserOrAdmin(name, pwd, null)

        val auth = given().contentType(ContentType.JSON)
                .body(AuthenticationDto(name, pwd))
                .post("/login")
                .then()
                .statusCode(204)
                .extract().cookie("SESSION")

        checkAuthenticatedCookie(auth, 200)
    }

}