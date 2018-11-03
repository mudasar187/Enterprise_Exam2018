package no.ecm.creditcard


import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.restassured.response.Response
import io.restassured.response.ValidatableResponse
import junit.framework.Assert.assertTrue
import no.ecm.creditcard.repository.CreditCardRepository
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.core.io.Resource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.util.StreamUtils
import java.nio.charset.StandardCharsets

@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest(
	classes = [(CreditCardApplication::class)],
	webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TestBase {
	
	@LocalServerPort
	protected var port = 0
	
	@field:Autowired
	private lateinit var creditCardRepository: CreditCardRepository
	
	//TODO make these files work
	@Value("classpath/graphql/get-creditcard.graphql")
	private lateinit var getCreditcardFile: Resource
	
	@Value("classpath/graphql/create-creditcard.graphql")
	private lateinit var createCreditcardFile: Resource
	
	
	fun getCreditcardPayload(): String {
		return StreamUtils.copyToString(getCreditcardFile.inputStream, StandardCharsets.UTF_8)
	}
	
	fun createCreditcardPayload(): String {
		return StreamUtils.copyToString(createCreditcardFile.inputStream, StandardCharsets.UTF_8)
	}
	
	@Before
	fun clean() {
		// RestAssured configs shared by all the tests
		RestAssured.baseURI = "http://localhost"
		RestAssured.port = port
		RestAssured.basePath = "/graphql"
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
		
		creditCardRepository.deleteAll()
	}
	
	fun createCreditcard(username: String, creditcardNumber: String, expDate: String, cvc: Int) : String? {
		val createQuery = """
                    { "query" :
                         "mutation{createCreditCard(creditCard:{expirationDate:\"$expDate\",cvc: $cvc,username:\"$username\",cardNumber:\"$creditcardNumber\"})}"
                    }
                    """.trimIndent()
		
		return given()
			.accept(ContentType.JSON)
			.contentType(ContentType.JSON)
			.body(createQuery)
			.post()
			.then()
			.statusCode(200)
			.extract().body().path<String>("data.createCreditCard")
			//.extract().response().body.prettyPeek()
	}
	
	//TODO make this method generic enough to put elsewhere
	fun getCreditcardById(id: String): Response? {
		
		val getQuery = """
			{
  				creditcardById(id: "$id") {
    				id, username, cardNumber, cvc, expirationDate
  				}
			}
		""".trimIndent()
		
		return given()
			.accept(ContentType.JSON)
			.contentType(ContentType.JSON)
			.queryParam("query", getQuery)
			.get().thenReturn()
		
	}
}