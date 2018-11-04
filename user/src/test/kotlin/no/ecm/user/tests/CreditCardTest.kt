package no.ecm.user.tests

import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import no.ecm.user.TestBase
import org.hamcrest.Matchers.*
import org.junit.Assert.assertNotNull
import org.junit.Test


class CreditCardTest : TestBase() {
	
	@Test
	fun testCreateAndGetById() {
		
		val username = "johndoe"
		val creditcardNumber = "12345"
		val cvc = 123
		val expDate = "20/01"
		
		val id = createCreditcard(username, creditcardNumber, expDate, cvc)
		
		assertNotNull(id)
		
		getCreditcardById(id!!)!!
			.statusCode(200)
			
			// all queries params are present
			.body("data.creditcardById.size()", equalTo(5))
			
			// check individual params
			.body("data.creditcardById.id", equalTo(id.toString()))
			.body("data.creditcardById.username", equalTo(username))
			.body("data.creditcardById.cardNumber", equalTo(creditcardNumber))
			.body("data.creditcardById.cvc", equalTo(cvc))
			.body("data.creditcardById.expirationDate", equalTo(expDate))
	}
	
	@Test
	fun testCreateCreditcardWithMissingArguments() {
		
		val expDate = "20/01"
		
		val createQuery = """
                    { "query" :
                         "mutation{createCreditCard(creditCard:{expirationDate:\"$expDate\"})}"
                    }
                    """.trimIndent()
		
		given()
			.accept(ContentType.JSON)
			.contentType(ContentType.JSON)
			.body(createQuery)
			.post()
			.then()
			.statusCode(200)
			.body("data.createCreditCard", equalTo(null))
			.extract().body().path<String>("data.createCreditCard")
		
			//TODO get the errormessages in errors.message[0]. Problem in converter?
		
			
			//.extract().response().body.prettyPeek()
	}
	
	@Test
	fun testGetNonExistingCreditcard() {
		
		getCreditcardById("-1")!!
			.statusCode(200)
			.body("data.creditcardById", equalTo(null))
	}
	
	@Test
	fun testDeleteCreditcard() {
		
		val username = "johndoe"
		val creditcardNumber = "12345"
		val cvc = 123
		val expDate = "20/01"
		
		val id = createCreditcard(username, creditcardNumber, expDate, cvc)
		
		assertNotNull(id)
		
		getCreditcardById(id!!)!!
			.statusCode(200)
		
		//DELETE
		val deleteQuery = """
                    { "query" :
                         "mutation{deleteCreditCardById(id:\"$id\")}"
                    }
                    """.trimIndent()
		
		given()
			.accept(ContentType.JSON)
			.contentType(ContentType.JSON)
			.body(deleteQuery)
			.post()
			.then()
			.statusCode(200)
			.body("data.deleteCreditCardById", equalTo(true.toString()))
		
	}
	
}