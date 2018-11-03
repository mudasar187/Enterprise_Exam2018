package no.ecm.creditcard


import io.restassured.RestAssured
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
}