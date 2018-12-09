package no.ecm.user


import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.restassured.response.ValidatableResponse
import no.ecm.user.model.entity.UserEntity
import no.ecm.user.repository.UserRepository
import org.junit.Before
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDate

@ActiveProfiles("test")
@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class TestBase {
	
	@LocalServerPort
	protected var port = 0
	
	@Autowired
	private lateinit var userRepository: UserRepository
	
	@Before
	fun clean() {
		// RestAssured configs shared by all the tests
		RestAssured.baseURI = "http://localhost"
		RestAssured.port = port
		RestAssured.basePath = "/users"
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()

		userRepository.deleteAll()
		val admin = UserEntity(username = "admin", dateOfBirth = LocalDate.now(), name = "Admin user", email = "admin@mail.com")
		userRepository.save(admin)
	}
	
	fun createUser(username: String, dateOfBirth: String, name: String, email: String) : String? {
		val createQuery = """
                    { "query" :
                         "mutation{createUser(user:{username:\"$username\",dateOfBirth:\"$dateOfBirth\",name:\"$name\",email:\"$email\"})}"
                    }
                    """.trimIndent()
		
		return given().auth().basic("$username", "123")
			.accept(ContentType.JSON)
			.contentType(ContentType.JSON)
			.body(createQuery)
			.post()
			.then()
			.statusCode(200)
			.extract().body().path<String>("data.createUser")
	}
	
	fun invalidUserQuery(query: String): ValidatableResponse? {
		return given().auth().basic("admin", "admin")
			.accept(ContentType.JSON)
			.contentType(ContentType.JSON)
			.body(query)
			.post()
			.then()
			.statusCode(200)
	}
	
	fun updateInvalidUser(query: String) {
	
	}
	
	fun getUserByUsername(username: String): ValidatableResponse? {
		
		val getQuery = """
			{
  				userById(id: "$username") {
    				username, email, name, dateOfBirth
  				}
			}
		""".trimIndent()
		
		return given().auth().basic("admin", "admin")
			.accept(ContentType.JSON)
			.contentType(ContentType.JSON)
			.queryParam("query", getQuery)
			.get()
			.then()
	}
}